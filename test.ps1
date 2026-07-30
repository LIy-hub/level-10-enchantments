param(
    [string]$MinecraftVersion
)

$ErrorActionPreference = 'Stop'

if ([string]::IsNullOrWhiteSpace($MinecraftVersion)) {
    $MinecraftVersion = ((Get-Content (Join-Path $PSScriptRoot 'gradle.properties') |
        Where-Object { $_ -like 'minecraft_version=*' }) -split '=', 2)[1]
}
$fabricApiMinimum = ((Get-Content (Join-Path $PSScriptRoot 'gradle.properties') |
    Where-Object { $_ -like 'fabric_api_min_version=*' }) -split '=', 2)[1]

function Assert([bool]$Condition, [string]$Message) {
    if (-not $Condition) {
        throw "Assertion failed: $Message"
    }
}

& (Join-Path $PSScriptRoot 'build.ps1') -MinecraftVersion $MinecraftVersion
& (Join-Path $PSScriptRoot 'test-resources.ps1') -MinecraftVersion $MinecraftVersion

$jarName = "level10-enchantments-$MinecraftVersion-1.5.1.jar"
$jarPath = Join-Path $PSScriptRoot "build\dist\$jarName"
Assert (Test-Path -LiteralPath $jarPath) "build created $jarName"

$entries = @(& jar tf $jarPath)
if ($LASTEXITCODE -ne 0) {
    throw "jar tf failed with exit code $LASTEXITCODE"
}

$requiredEntries = @(
    'fabric.mod.json',
    'level10enchantments.mixins.json',
    'level10-enchantments.rules.csv',
    'com/liy/level10enchantments/EnchantmentRules.class',
    'com/liy/level10enchantments/EnchantingLevelPolicy.class',
    'com/liy/level10enchantments/AnvilLevelMergePolicy.class',
    'com/liy/level10enchantments/LootBalancePolicy.class',
    'com/liy/level10enchantments/MasterLibrarianTradePolicy.class',
    'com/liy/level10enchantments/EnchantmentLevelNormalizer.class',
    'com/liy/level10enchantments/HighLevelLootApplier.class',
    'com/liy/level10enchantments/CompatibilitySurcharge.class',
    'com/liy/level10enchantments/AnvilCostPolicy.class',
    'com/liy/level10enchantments/RainbowColors.class',
    'com/liy/level10enchantments/mixin/AbstractVillagerMixin.class',
    'com/liy/level10enchantments/mixin/EnchantRandomlyFunctionMixin.class',
    'com/liy/level10enchantments/mixin/EnchantmentMenuMixin.class',
    'com/liy/level10enchantments/mixin/AnvilMenuMixin.class',
    'com/liy/level10enchantments/mixin/EnchantmentHelperMixin.class',
    'com/liy/level10enchantments/mixin/LootTableMixin.class',
    'com/liy/level10enchantments/mixin/EnchantmentNameMixin.class',
    'data/minecraft/enchantment/mending.json',
    'data/minecraft/enchantment/thorns.json',
    'data/minecraft/enchantment/lunge.json',
    'data/level10enchantments/tags/item/chest_armor_plus_elytra.json'
)
foreach ($entry in $requiredEntries) {
    Assert ($entries -contains $entry) "JAR contains $entry"
}

$enchantmentEntries = @($entries | Where-Object { $_ -like 'data/minecraft/enchantment/*.json' })
Assert ($enchantmentEntries.Count -eq 29) 'JAR contains exactly 29 enchantment JSON files'

Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($jarPath)
try {
    $metadataEntry = $zip.GetEntry('fabric.mod.json')
    $metadataReader = [System.IO.StreamReader]::new($metadataEntry.Open())
    try {
        $metadata = $metadataReader.ReadToEnd() | ConvertFrom-Json
    } finally {
        $metadataReader.Dispose()
    }
    Assert ($metadata.environment -eq '*') 'Fabric metadata supports server and client'
    Assert ($metadata.id -eq 'level10enchantments') 'Fabric mod id'
    Assert ($metadata.version -eq '1.5.1') 'Fabric mod version'
    Assert ($metadata.depends.minecraft -eq $MinecraftVersion) 'Minecraft dependency is branch exact'
    Assert ($metadata.depends.fabricloader -eq '>=0.19.3') 'Fabric Loader minimum dependency'
    Assert ($metadata.depends.'fabric-api' -eq ">=$fabricApiMinimum") 'Fabric API minimum dependency'
    Assert ($metadata.depends.java -eq '>=21') 'Java minimum dependency'

    $mixinEntry = $zip.GetEntry('level10enchantments.mixins.json')
    $mixinReader = [System.IO.StreamReader]::new($mixinEntry.Open())
    try {
        $mixins = $mixinReader.ReadToEnd() | ConvertFrom-Json
    } finally {
        $mixinReader.Dispose()
    }
    Assert ($mixins.required -eq $true) 'Mixin configuration required'
    Assert ($mixins.compatibilityLevel -eq 'JAVA_21') 'Mixin Java 21 compatibility'
    Assert ($mixins.mixins.Count -eq 6) 'six common Mixins configured'
    Assert ($mixins.client.Count -eq 1) 'one client Mixin configured'
} finally {
    $zip.Dispose()
}

$hash = (Get-FileHash -LiteralPath $jarPath -Algorithm SHA256).Hash
Write-Output "JAR_SHA256=$hash"
Write-Output 'PASS: pure gameplay-policy tests, generated resources, metadata, and remapped JAR validated.'
