$ErrorActionPreference = 'Stop'

function Assert([bool]$Condition, [string]$Message) {
    if (-not $Condition) {
        throw "Assertion failed: $Message"
    }
}

function Read-ZipJson($Zip, [string]$Path) {
    $entry = $Zip.GetEntry($Path)
    if ($null -eq $entry) {
        throw "Missing zip entry: $Path"
    }
    $reader = [System.IO.StreamReader]::new($entry.Open())
    try {
        return ($reader.ReadToEnd() | ConvertFrom-Json)
    } finally {
        $reader.Dispose()
    }
}

$workspace = Resolve-Path (Join-Path $PSScriptRoot '..\..')
$serverJar = Join-Path $workspace 'versions\26.1.2\server-26.1.2.jar'
$rulesPath = Join-Path $PSScriptRoot 'src\main\resources\level10-enchantments.rules.csv'
$generator = Join-Path $PSScriptRoot 'generate-resources.ps1'
$output = Join-Path $PSScriptRoot 'build\resource-test'

if (Test-Path -LiteralPath $output) {
    Remove-Item -LiteralPath $output -Recurse -Force
}
& $generator -Output $output

$rules = @(Import-Csv $rulesPath)
Assert ($rules.Count -eq 29) 'manifest contains 29 rules'
$generatedFiles = @(Get-ChildItem (Join-Path $output 'data\minecraft\enchantment') -Filter '*.json' -File)
Assert ($generatedFiles.Count -eq 29) 'generator produced exactly 29 enchantment files'
$elytraChestEnchantments = @('protection', 'fire_protection', 'blast_protection', 'projectile_protection', 'thorns')
$compatibleProtectionEnchantments = @('protection', 'fire_protection', 'blast_protection', 'projectile_protection')
$compatibleDamageEnchantments = @('sharpness', 'smite', 'bane_of_arthropods')
$elytraArmorTag = '#level10enchantments:chest_armor_plus_elytra'

Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($serverJar)
try {
    foreach ($rule in $rules) {
        $name = $rule.id -replace '^minecraft:', ''
        $relative = "data/minecraft/enchantment/$name.json"
        $generatedPath = Join-Path $output ($relative -replace '/', '\')
        Assert (Test-Path -LiteralPath $generatedPath) "generated $relative"

        $vanilla = Read-ZipJson $zip $relative
        $generated = Get-Content -Raw -Encoding UTF8 $generatedPath | ConvertFrom-Json
        Assert ([int]$generated.max_level -eq 10) "$name max level is 10"
        $vanilla.max_level = 10

        if ($rule.effect_mode -eq 'mending') {
            $factor = $generated.effects.'minecraft:repair_with_xp'[0].effect.factor
            Assert ($factor.type -eq 'minecraft:linear') 'mending factor type'
            Assert ([math]::Abs(([double]$factor.base) - 2.0) -lt 0.000001) 'mending factor base'
            Assert ([math]::Abs(([double]$factor.per_level_above_first) - (2.0 / 3.0)) -lt 0.000001) 'mending slope'
            Assert ([math]::Abs((2.0 + 9.0 * [double]$factor.per_level_above_first) - 8.0) -lt 0.000001) 'mending X repairs 8'
            $vanilla.effects.'minecraft:repair_with_xp'[0].effect.factor = [pscustomobject][ordered]@{
                type = 'minecraft:linear'
                base = 2.0
                per_level_above_first = (2.0 / 3.0)
            }
        } elseif ($rule.effect_mode -eq 'thorns') {
            $trigger = $generated.effects.'minecraft:post_attack'[0]
            $chance = $trigger.requirements.chance.amount
            $damageEffects = @($trigger.effect.effects)
            Assert ($chance.type -eq 'minecraft:linear') 'thorns chance is level based'
            Assert ([math]::Abs(([double]$chance.base) - 0.1) -lt 0.000001) 'thorns I chance is 10%'
            Assert ([math]::Abs((0.1 + 9.0 * [double]$chance.per_level_above_first) - 1.0) -lt 0.000001) 'thorns X chance is 100%'
            Assert ($damageEffects.Count -eq 1) 'thorns has no durability damage effect'
            Assert ($damageEffects[0].type -eq 'minecraft:damage_entity') 'thorns retains retaliation damage'
            $minDamage = $damageEffects[0].min_damage
            $maxDamage = $damageEffects[0].max_damage
            Assert ([math]::Abs(([double]$minDamage.base + 9.0 * [double]$minDamage.per_level_above_first) - 10.0) -lt 0.000001) 'thorns X minimum damage is 10'
            Assert ([math]::Abs(([double]$maxDamage.base + 9.0 * [double]$maxDamage.per_level_above_first) - 15.0) -lt 0.000001) 'thorns X maximum damage is 15'

            $expectedTrigger = $vanilla.effects.'minecraft:post_attack'[0]
            $expectedDamage = @($expectedTrigger.effect.effects | Where-Object { $_.type -eq 'minecraft:damage_entity' })[0]
            $expectedTrigger.requirements.chance.amount = [pscustomobject][ordered]@{
                type = 'minecraft:linear'
                base = 0.1
                per_level_above_first = 0.1
            }
            $expectedDamage.min_damage = [pscustomobject][ordered]@{
                type = 'minecraft:linear'
                base = 1.0
                per_level_above_first = 1.0
            }
            $expectedDamage.max_damage = [pscustomobject][ordered]@{
                type = 'minecraft:linear'
                base = 5.0
                per_level_above_first = (10.0 / 9.0)
            }
            $expectedTrigger.effect.effects = [object[]]@($expectedDamage)
        } elseif ($rule.effect_mode -eq 'lunge') {
            $lungeEffects = @($generated.effects.'minecraft:post_piercing_attack'[0].effect.effects)
            $exhaustionEffects = @($lungeEffects | Where-Object { $_.type -eq 'minecraft:apply_exhaustion' })
            Assert ($exhaustionEffects.Count -eq 1) 'lunge has one exhaustion effect'
            $amount = $exhaustionEffects[0].amount
            Assert ($amount.type -eq 'minecraft:clamped') 'lunge exhaustion is clamped'
            Assert ([double]$amount.max -eq 12.0) 'lunge exhaustion cap is 12'
            Assert ([double]$amount.value.base -eq 4.0) 'lunge I exhaustion is 4'
            Assert ([math]::Min([double]$amount.max,
                    [double]$amount.value.base + 9.0 * [double]$amount.value.per_level_above_first) -eq 12.0) 'lunge X exhaustion remains 12'

            $expectedEffects = @($vanilla.effects.'minecraft:post_piercing_attack'[0].effect.effects)
            $expectedExhaustion = @($expectedEffects | Where-Object { $_.type -eq 'minecraft:apply_exhaustion' })[0]
            $expectedExhaustion.amount = [pscustomobject][ordered]@{
                type = 'minecraft:clamped'
                min = 0.0
                max = 12.0
                value = [pscustomobject][ordered]@{
                    type = 'minecraft:linear'
                    base = 4.0
                    per_level_above_first = 4.0
                }
            }
        }
        if ($elytraChestEnchantments -contains $name) {
            Assert ($generated.supported_items -eq $elytraArmorTag) "$name supports elytra plus armor"
            $vanilla.supported_items = $elytraArmorTag
        }
        if (($compatibleProtectionEnchantments -contains $name) -or
                ($compatibleDamageEnchantments -contains $name)) {
            Assert ($generated.PSObject.Properties.Name -notcontains 'exclusive_set') "$name is no longer exclusive"
            $vanilla.PSObject.Properties.Remove('exclusive_set')
        }

        $expectedNormalized = $vanilla | ConvertTo-Json -Depth 100 -Compress
        $actualNormalized = $generated | ConvertTo-Json -Depth 100 -Compress
        Assert ($actualNormalized -ceq $expectedNormalized) "$name differs from vanilla beyond allowed fields"
    }
} finally {
    $zip.Dispose()
}

foreach ($excluded in @('breach', 'depth_strider', 'feather_falling', 'lure', 'quick_charge', 'swift_sneak')) {
    Assert (-not (Test-Path (Join-Path $output "data\minecraft\enchantment\$excluded.json"))) "excluded $excluded absent"
}

$tagPath = Join-Path $output 'data\level10enchantments\tags\item\chest_armor_plus_elytra.json'
Assert (Test-Path -LiteralPath $tagPath) 'elytra armor item tag generated'
$tag = Get-Content -Raw -Encoding UTF8 $tagPath | ConvertFrom-Json
Assert ($tag.replace -eq $false) 'elytra armor tag does not replace other data'
Assert ($tag.values.Count -eq 2) 'elytra armor tag has exactly two entries'
Assert ($tag.values[0] -eq '#minecraft:enchantable/armor') 'elytra armor tag retains vanilla armor'
Assert ($tag.values[1] -eq 'minecraft:elytra') 'elytra armor tag adds elytra'

Write-Output 'PASS: 29 overrides validated; compatibility unlocked; Thorns X reworked; Lunge exhaustion capped at 12.'
