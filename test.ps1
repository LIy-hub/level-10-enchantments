$ErrorActionPreference = 'Stop'

function Assert([bool]$Condition, [string]$Message) {
    if (-not $Condition) {
        throw "Assertion failed: $Message"
    }
}

function Invoke-Native([scriptblock]$Command, [string]$Description) {
    & $Command
    if ($LASTEXITCODE -ne 0) {
        throw "$Description failed with exit code $LASTEXITCODE"
    }
}

$testClasses = Join-Path $PSScriptRoot 'build\test-classes'
$jarPath = Join-Path $PSScriptRoot 'build\dist\level10-enchantments-1.5.1.jar'

& (Join-Path $PSScriptRoot 'test-resources.ps1')

if (Test-Path -LiteralPath $testClasses) {
    Remove-Item -LiteralPath $testClasses -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $testClasses | Out-Null

$coreSources = @(
    (Join-Path $PSScriptRoot 'src\main\java\com\liy\level10enchantments\EnchantmentRules.java'),
    (Join-Path $PSScriptRoot 'src\main\java\com\liy\level10enchantments\EnchantingLevelPolicy.java'),
    (Join-Path $PSScriptRoot 'src\main\java\com\liy\level10enchantments\AnvilLevelMergePolicy.java'),
    (Join-Path $PSScriptRoot 'src\main\java\com\liy\level10enchantments\LootBalancePolicy.java'),
    (Join-Path $PSScriptRoot 'src\main\java\com\liy\level10enchantments\MasterLibrarianTradePolicy.java'),
    (Join-Path $PSScriptRoot 'src\main\java\com\liy\level10enchantments\CompatibilitySurcharge.java'),
    (Join-Path $PSScriptRoot 'src\main\java\com\liy\level10enchantments\AnvilCostPolicy.java'),
    (Join-Path $PSScriptRoot 'src\main\java\com\liy\level10enchantments\RainbowColors.java')
)
& javac --release 25 -d $testClasses $coreSources
if ($LASTEXITCODE -ne 0) { throw "Core compilation failed with exit code $LASTEXITCODE" }
Copy-Item (Join-Path $PSScriptRoot 'src\main\resources\level10-enchantments.rules.csv') $testClasses
$testSources = @(
    (Join-Path $PSScriptRoot 'src\test\java\com\liy\level10enchantments\EnchantingLevelPolicyTest.java'),
    (Join-Path $PSScriptRoot 'src\test\java\com\liy\level10enchantments\AnvilLevelMergePolicyTest.java'),
    (Join-Path $PSScriptRoot 'src\test\java\com\liy\level10enchantments\LootBalancePolicyTest.java'),
    (Join-Path $PSScriptRoot 'src\test\java\com\liy\level10enchantments\MasterLibrarianTradePolicyTest.java'),
    (Join-Path $PSScriptRoot 'src\test\java\com\liy\level10enchantments\CompatibilitySurchargeTest.java'),
    (Join-Path $PSScriptRoot 'src\test\java\com\liy\level10enchantments\AnvilCostPolicyTest.java'),
    (Join-Path $PSScriptRoot 'src\test\java\com\liy\level10enchantments\RainbowColorsTest.java')
)
& javac --release 25 -classpath $testClasses -d $testClasses $testSources
if ($LASTEXITCODE -ne 0) { throw "Test compilation failed with exit code $LASTEXITCODE" }
& java -cp $testClasses com.liy.level10enchantments.EnchantingLevelPolicyTest
if ($LASTEXITCODE -ne 0) { throw "Enchanting policy tests failed with exit code $LASTEXITCODE" }
& java -cp $testClasses com.liy.level10enchantments.AnvilLevelMergePolicyTest
if ($LASTEXITCODE -ne 0) { throw "Anvil merge policy tests failed with exit code $LASTEXITCODE" }
& java -cp $testClasses com.liy.level10enchantments.LootBalancePolicyTest
if ($LASTEXITCODE -ne 0) { throw "Loot balance policy tests failed with exit code $LASTEXITCODE" }
& java -cp $testClasses com.liy.level10enchantments.MasterLibrarianTradePolicyTest
if ($LASTEXITCODE -ne 0) { throw "Librarian policy tests failed with exit code $LASTEXITCODE" }
& java -cp $testClasses com.liy.level10enchantments.CompatibilitySurchargeTest
if ($LASTEXITCODE -ne 0) { throw "Surcharge tests failed with exit code $LASTEXITCODE" }
& java -cp $testClasses com.liy.level10enchantments.AnvilCostPolicyTest
if ($LASTEXITCODE -ne 0) { throw "Anvil cost policy tests failed with exit code $LASTEXITCODE" }
& java -cp $testClasses com.liy.level10enchantments.RainbowColorsTest
if ($LASTEXITCODE -ne 0) { throw "Rainbow tests failed with exit code $LASTEXITCODE" }

& (Join-Path $PSScriptRoot 'build.ps1')
if (-not (Test-Path -LiteralPath $jarPath)) {
    throw "Build did not create $jarPath"
}

$entries = @(& jar tf $jarPath)
if ($LASTEXITCODE -ne 0) { throw "jar tf failed with exit code $LASTEXITCODE" }
$requiredEntries = @(
    'fabric.mod.json',
    'level10enchantments.mixins.json',
    'level10-enchantments.rules.csv',
    'com/liy/level10enchantments/EnchantmentRules.class',
    'com/liy/level10enchantments/EnchantmentRules$Rule.class',
    'com/liy/level10enchantments/EnchantingLevelPolicy.class',
    'com/liy/level10enchantments/AnvilLevelMergePolicy.class',
    'com/liy/level10enchantments/LootBalancePolicy.class',
    'com/liy/level10enchantments/LootBalancePolicy$Profile.class',
    'com/liy/level10enchantments/MasterLibrarianTradePolicy.class',
    'com/liy/level10enchantments/MasterLibrarianTradePolicy$Trade.class',
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
    try { $metadata = $metadataReader.ReadToEnd() | ConvertFrom-Json } finally { $metadataReader.Dispose() }
    Assert ($metadata.environment -eq '*') 'Fabric metadata supports server and client'
    Assert ($metadata.id -eq 'level10enchantments') 'Fabric mod id'
    Assert ($metadata.version -eq '1.5.1') 'Fabric mod version'
    Assert ($metadata.depends.'fabric-api' -eq '>=0.146.1') 'Fabric API minimum dependency'

    $mixinEntry = $zip.GetEntry('level10enchantments.mixins.json')
    $mixinReader = [System.IO.StreamReader]::new($mixinEntry.Open())
    try { $mixins = $mixinReader.ReadToEnd() | ConvertFrom-Json } finally { $mixinReader.Dispose() }
    Assert ($mixins.required -eq $true) 'Mixin configuration required'
    Assert ($mixins.compatibilityLevel -eq 'JAVA_25') 'Mixin Java 25 compatibility'
    Assert ($mixins.mixins -contains 'AbstractVillagerMixin') 'librarian Mixin configured'
    Assert ($mixins.mixins -contains 'EnchantRandomlyFunctionMixin') 'random enchantment Mixin configured'
    Assert ($mixins.mixins -contains 'EnchantmentMenuMixin') 'enchantment Mixin configured'
    Assert ($mixins.mixins -contains 'AnvilMenuMixin') 'anvil Mixin configured'
    Assert ($mixins.mixins -contains 'EnchantmentHelperMixin') 'bookshelf cap Mixin configured'
    Assert ($mixins.mixins -contains 'LootTableMixin') 'loot table Mixin configured'
    Assert ($mixins.client -contains 'EnchantmentNameMixin') 'rainbow name client Mixin configured'
} finally {
    $zip.Dispose()
}

$bytecode = & javap -classpath $jarPath -p `
    com.liy.level10enchantments.EnchantingLevelPolicy `
    com.liy.level10enchantments.AnvilLevelMergePolicy `
    com.liy.level10enchantments.LootBalancePolicy `
    com.liy.level10enchantments.MasterLibrarianTradePolicy `
    com.liy.level10enchantments.EnchantmentLevelNormalizer `
    com.liy.level10enchantments.HighLevelLootApplier `
    com.liy.level10enchantments.CompatibilitySurcharge `
    com.liy.level10enchantments.RainbowColors `
    com.liy.level10enchantments.mixin.AbstractVillagerMixin `
    com.liy.level10enchantments.mixin.EnchantRandomlyFunctionMixin `
    com.liy.level10enchantments.mixin.EnchantmentMenuMixin `
    com.liy.level10enchantments.mixin.AnvilMenuMixin `
    com.liy.level10enchantments.mixin.EnchantmentHelperMixin `
    com.liy.level10enchantments.mixin.LootTableMixin `
    com.liy.level10enchantments.mixin.EnchantmentNameMixin 2>&1 | Out-String
if ($LASTEXITCODE -ne 0) { throw "javap failed with exit code $LASTEXITCODE" }
foreach ($method in @(
    'calculate',
    'capToVanillaMaximum',
    'merge',
    'selectHighLevel',
    'allowsHighLevelInDimension',
    'select',
    'apply',
    'rgbForIndex',
    'level10$ensureMasterLibrarianTrade',
    'level10$capRandomlyGeneratedEnchantments',
    'level10$capTableEnchantments',
    'level10$capBeforeTooExpensiveCheck',
    'level10$raiseTooExpensiveThreshold',
    'level10$finalizeAnvilCost',
    'level10$raiseBookshelfCap',
    'level10$capRegistryGeneratedEnchantments',
    'level10$capStreamGeneratedEnchantments',
    'level10$capProviderGeneratedEnchantments',
    'level10$applyHighLevelLoot',
    'level10$rainbowLevelTenName',
    'enchantmentLevels'
)) {
    Assert ($bytecode.Contains($method)) "bytecode contains $method"
}
Assert (-not $bytecode.Contains('BreakthroughSelector')) 'enchanting-table breakthrough path removed'

$hash = (Get-FileHash $jarPath -Algorithm SHA256).Hash
Write-Output "JAR_SHA256=$hash"
Write-Output 'PASS: universal server/client JAR structure and Mixin bytecode validated.'
