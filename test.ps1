param(
    [string]$MinecraftVersion = '1.20.5'
)

$ErrorActionPreference = 'Stop'

function Assert([bool]$Condition, [string]$Message) {
    if (-not $Condition) {
        throw "Assertion failed: $Message"
    }
}

function Read-ZipText($Zip, [string]$Path) {
    $entry = $Zip.GetEntry($Path)
    if ($null -eq $entry) {
        throw "Missing zip entry: $Path"
    }
    $reader = [System.IO.StreamReader]::new($entry.Open())
    try {
        return $reader.ReadToEnd()
    } finally {
        $reader.Dispose()
    }
}

$properties = @{}
Get-Content (Join-Path $PSScriptRoot 'gradle.properties') |
    Where-Object { $_ -match '^[^#=\s]+=' } |
    ForEach-Object {
        $key, $value = $_ -split '=', 2
        $properties[$key] = $value
    }

Assert ($MinecraftVersion -eq $properties.minecraft_version) 'requested version matches branch'
& (Join-Path $PSScriptRoot 'build.ps1') -MinecraftVersion $MinecraftVersion
& (Join-Path $PSScriptRoot 'test-resources.ps1') -MinecraftVersion $MinecraftVersion

$jarName = "level10-enchantments-$MinecraftVersion-$($properties.mod_version).jar"
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
    'com/liy/level10enchantments/LegacyEffectPolicy.class',
    'com/liy/level10enchantments/RainbowColors.class',
    'com/liy/level10enchantments/mixin/AbstractVillagerMixin.class',
    'com/liy/level10enchantments/mixin/EnchantmentMixin.class',
    'com/liy/level10enchantments/mixin/ExperienceOrbMixin.class',
    'com/liy/level10enchantments/mixin/ThornsEnchantmentMixin.class',
    'com/liy/level10enchantments/mixin/EnchantRandomlyFunctionMixin.class',
    'com/liy/level10enchantments/mixin/EnchantmentMenuMixin.class',
    'com/liy/level10enchantments/mixin/AnvilMenuMixin.class',
    'com/liy/level10enchantments/mixin/EnchantmentHelperMixin.class',
    'com/liy/level10enchantments/mixin/LootTableMixin.class',
    'com/liy/level10enchantments/mixin/EnchantmentNameMixin.class'
)
foreach ($entry in $requiredEntries) {
    Assert ($entries -contains $entry) "JAR contains $entry"
}

$enchantmentData = @($entries | Where-Object { $_ -like 'data/minecraft/enchantment/*.json' })
Assert ($enchantmentData.Count -eq 0) `
    'legacy JAR contains no unsupported data-driven enchantment definitions'

$expectedJava = if ($MinecraftVersion -in @('1.20.1', '1.20.2', '1.20.3', '1.20.4')) {
    17
} else {
    21
}
$expectedClassMajor = if ($expectedJava -eq 17) { 61 } else { 65 }
$expectedFabricApiMinimum = '>=' + ($properties.fabric_api_version -split '\+')[0]
$expectedMixinCompatibility = "JAVA_$expectedJava"

Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($jarPath)
try {
    $metadata = (Read-ZipText $zip 'fabric.mod.json') | ConvertFrom-Json
    Assert ($metadata.environment -eq '*') 'Fabric metadata supports server and client'
    Assert ($metadata.id -eq 'level10enchantments') 'Fabric mod id'
    Assert ($metadata.version -eq $properties.mod_version) 'Fabric mod version'
    Assert ($metadata.depends.minecraft -eq $MinecraftVersion) 'Minecraft dependency is branch exact'
    Assert ($metadata.depends.fabricloader -eq '>=' + $properties.loader_version) `
        'Fabric Loader dependency is exact minimum'
    Assert ($metadata.depends.'fabric-api' -eq $expectedFabricApiMinimum) `
        'Fabric API dependency is exact minimum'
    Assert ($metadata.depends.java -eq ">=$expectedJava") 'Java dependency matches branch'

    $mixins = (Read-ZipText $zip 'level10enchantments.mixins.json') | ConvertFrom-Json
    Assert ($mixins.required -eq $true) 'Mixin configuration required'
    Assert ($mixins.compatibilityLevel -eq $expectedMixinCompatibility) `
        'Mixin Java compatibility matches branch'
    Assert ($mixins.mixins.Count -eq 9) 'nine common Mixins configured'
    Assert ($mixins.client.Count -eq 1) 'one client Mixin configured'

    $rulesText = Read-ZipText $zip 'level10-enchantments.rules.csv'
    $packagedRules = @($rulesText -split "`r?`n" | Where-Object { $_ -match '^minecraft:' })
    Assert ($packagedRules.Count -eq 26) 'JAR packages exactly 26 rules'

    foreach ($classEntry in @($zip.Entries | Where-Object { $_.FullName -like '*.class' })) {
        $stream = $classEntry.Open()
        try {
            $header = [byte[]]::new(8)
            Assert ($stream.Read($header, 0, 8) -eq 8) "read class header for $($classEntry.FullName)"
            $major = ([int]$header[6] -shl 8) -bor [int]$header[7]
            Assert ($major -eq $expectedClassMajor) `
                "$($classEntry.FullName) has Java class major $expectedClassMajor"
        } finally {
            $stream.Dispose()
        }
    }
} finally {
    $zip.Dispose()
}

$hash = (Get-FileHash -LiteralPath $jarPath -Algorithm SHA256).Hash
Write-Output "JAR_PATH=$jarPath"
Write-Output "JAR_SHA256=$hash"
Write-Output "CLASS_MAJOR=$expectedClassMajor"
Write-Output 'RULE_COUNT=26'
Write-Output 'PASS: gameplay-policy tests, legacy resource contract, metadata, bytecode, and remapped JAR validated.'
