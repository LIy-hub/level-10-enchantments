param(
    [string]$MinecraftVersion = '1.20.1'
)

$ErrorActionPreference = 'Stop'

function Assert([bool]$Condition, [string]$Message) {
    if (-not $Condition) {
        throw "Assertion failed: $Message"
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

$rulesPath = Join-Path $PSScriptRoot 'src\main\resources\level10-enchantments.rules.csv'
$rules = @(Import-Csv $rulesPath)
$expectedIds = @(
    'minecraft:sharpness',
    'minecraft:smite',
    'minecraft:bane_of_arthropods',
    'minecraft:impaling',
    'minecraft:power',
    'minecraft:fire_aspect',
    'minecraft:knockback',
    'minecraft:punch',
    'minecraft:piercing',
    'minecraft:sweeping',
    'minecraft:looting',
    'minecraft:riptide',
    'minecraft:loyalty',
    'minecraft:efficiency',
    'minecraft:fortune',
    'minecraft:unbreaking',
    'minecraft:luck_of_the_sea',
    'minecraft:protection',
    'minecraft:fire_protection',
    'minecraft:blast_protection',
    'minecraft:projectile_protection',
    'minecraft:thorns',
    'minecraft:respiration',
    'minecraft:frost_walker',
    'minecraft:soul_speed',
    'minecraft:mending'
)

Assert ($rules.Count -eq 26) 'manifest contains exactly 26 legacy-version rules'
Assert (($rules.id | Sort-Object -Unique).Count -eq 26) 'rule identifiers are unique'
Assert ((Compare-Object ($expectedIds | Sort-Object) ($rules.id | Sort-Object)).Count -eq 0) `
    'rule identifiers match the frozen Minecraft 1.20.x roster'
Assert (@($rules | Where-Object { $_.table_eligible -eq 'true' }).Count -eq 23) `
    'exactly 23 rules are enchanting-table eligible'
Assert (@($rules | Where-Object { $_.effect_mode -eq 'vanilla' }).Count -eq 24) `
    'exactly 24 rules retain vanilla effect scaling'
Assert (@($rules | Where-Object { $_.effect_mode -eq 'mending' }).Count -eq 1) `
    'exactly one Mending effect rule exists'
Assert (@($rules | Where-Object { $_.effect_mode -eq 'thorns' }).Count -eq 1) `
    'exactly one Thorns effect rule exists'

foreach ($absent in @('minecraft:density', 'minecraft:wind_burst', 'minecraft:lunge')) {
    Assert ($rules.id -notcontains $absent) "$absent is absent on Minecraft 1.20.x"
}
foreach ($treasureOnly in @('minecraft:frost_walker', 'minecraft:soul_speed', 'minecraft:mending')) {
    $rule = @($rules | Where-Object { $_.id -eq $treasureOnly })
    Assert ($rule.Count -eq 1) "$treasureOnly has one rule"
    Assert ($rule[0].table_eligible -eq 'false') "$treasureOnly stays off the enchanting table"
}

$legacyDataDirectory = Join-Path $PSScriptRoot 'src\main\resources\data\minecraft\enchantment'
Assert (-not (Test-Path -LiteralPath $legacyDataDirectory)) `
    'legacy branch does not package unsupported data-driven enchantment overrides'

Write-Output "PASS: $MinecraftVersion legacy roster contains 26 exact rules and no unsupported enchantments."
