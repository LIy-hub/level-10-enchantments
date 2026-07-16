param(
    [Parameter(Mandatory = $true)]
    [string]$Output
)

$ErrorActionPreference = 'Stop'

$workspace = if ([string]::IsNullOrWhiteSpace($env:LEVEL10_WORKSPACE)) {
    Resolve-Path (Join-Path $PSScriptRoot '..\..')
} else {
    Resolve-Path -LiteralPath $env:LEVEL10_WORKSPACE
}
$serverJar = Join-Path $workspace 'versions\26.1.2\server-26.1.2.jar'
$rulesPath = Join-Path $PSScriptRoot 'src\main\resources\level10-enchantments.rules.csv'
$rules = @(Import-Csv $rulesPath)

if ($rules.Count -ne 29) {
    throw "Expected 29 rules, found $($rules.Count)"
}
if (-not (Test-Path -LiteralPath $serverJar)) {
    throw "Missing Minecraft server JAR: $serverJar"
}

New-Item -ItemType Directory -Force -Path $Output | Out-Null
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($serverJar)
$utf8NoBom = [System.Text.UTF8Encoding]::new($false)
$elytraArmorTag = '#level10enchantments:chest_armor_plus_elytra'
$elytraChestEnchantments = @(
    'protection',
    'fire_protection',
    'blast_protection',
    'projectile_protection',
    'thorns'
)
$compatibleProtectionEnchantments = @('protection', 'fire_protection', 'blast_protection', 'projectile_protection')
$compatibleDamageEnchantments = @('sharpness', 'smite', 'bane_of_arthropods')

function Write-JsonFile([string]$Destination, [object]$Json) {
    New-Item -ItemType Directory -Force -Path (Split-Path $Destination) | Out-Null
    $content = $Json | ConvertTo-Json -Depth 100
    [System.IO.File]::WriteAllText($Destination, $content, $utf8NoBom)
}

function Read-ZipJson([string]$Path) {
    $entry = $zip.GetEntry($Path)
    if ($null -eq $entry) {
        throw "Missing vanilla entry $Path"
    }

    $reader = [System.IO.StreamReader]::new($entry.Open())
    try {
        return $reader.ReadToEnd() | ConvertFrom-Json
    } finally {
        $reader.Dispose()
    }
}

try {
    foreach ($rule in $rules) {
        $name = $rule.id -replace '^minecraft:', ''
        $path = "data/minecraft/enchantment/$name.json"
        $json = Read-ZipJson $path

        if ([int]$json.max_level -ne [int]$rule.vanilla_max) {
            throw "Vanilla max mismatch for $($rule.id): expected $($rule.vanilla_max), got $($json.max_level)"
        }

        $json.max_level = 10
        if ($rule.effect_mode -eq 'mending') {
            $repairEffects = $json.effects.'minecraft:repair_with_xp'
            if ($repairEffects.Count -ne 1 -or $repairEffects[0].effect.type -ne 'minecraft:multiply') {
                throw 'Vanilla mending repair effect has an unexpected structure'
            }
            $repairEffects[0].effect.factor = [pscustomobject][ordered]@{
                type = 'minecraft:linear'
                base = 2.0
                per_level_above_first = (2.0 / 3.0)
            }
        } elseif ($rule.effect_mode -eq 'thorns') {
            $trigger = $json.effects.'minecraft:post_attack'[0]
            $effects = @($trigger.effect.effects)
            $damageEffect = @($effects | Where-Object { $_.type -eq 'minecraft:damage_entity' })
            $durabilityEffects = @($effects | Where-Object { $_.type -eq 'minecraft:change_item_damage' })
            if ($damageEffect.Count -ne 1 -or $durabilityEffects.Count -ne 1) {
                throw 'Vanilla thorns effect has an unexpected structure'
            }
            $trigger.requirements.chance.amount = [pscustomobject][ordered]@{
                type = 'minecraft:linear'
                base = 0.1
                per_level_above_first = 0.1
            }
            $damageEffect[0].min_damage = [pscustomobject][ordered]@{
                type = 'minecraft:linear'
                base = 1.0
                per_level_above_first = 1.0
            }
            $damageEffect[0].max_damage = [pscustomobject][ordered]@{
                type = 'minecraft:linear'
                base = 5.0
                per_level_above_first = (10.0 / 9.0)
            }
            $trigger.effect.effects = [object[]]@($damageEffect[0])
        } elseif ($rule.effect_mode -eq 'lunge') {
            $lungeEffects = @($json.effects.'minecraft:post_piercing_attack'[0].effect.effects)
            $exhaustionEffects = @($lungeEffects | Where-Object { $_.type -eq 'minecraft:apply_exhaustion' })
            if ($exhaustionEffects.Count -ne 1) {
                throw 'Vanilla lunge exhaustion effect has an unexpected structure'
            }
            $exhaustionEffects[0].amount = [pscustomobject][ordered]@{
                type = 'minecraft:clamped'
                min = 0.0
                max = 12.0
                value = [pscustomobject][ordered]@{
                    type = 'minecraft:linear'
                    base = 4.0
                    per_level_above_first = 4.0
                }
            }
        } elseif ($rule.effect_mode -ne 'vanilla') {
            throw "Unknown effect mode $($rule.effect_mode) for $($rule.id)"
        }

        if ($elytraChestEnchantments -contains $name) {
            if ($json.supported_items -ne '#minecraft:enchantable/armor') {
                throw "Vanilla $name supported_items changed unexpectedly: $($json.supported_items)"
            }
            $json.supported_items = $elytraArmorTag
        }

        if (($compatibleProtectionEnchantments -contains $name) -or
                ($compatibleDamageEnchantments -contains $name)) {
            if ($null -eq $json.exclusive_set) {
                throw "Vanilla $name exclusive_set is missing"
            }
            $json.PSObject.Properties.Remove('exclusive_set')
        }

        $destination = Join-Path $Output ($path -replace '/', '\')
        Write-JsonFile $destination $json
    }

    $tagPath = Join-Path $Output 'data\level10enchantments\tags\item\chest_armor_plus_elytra.json'
    $tag = [pscustomobject][ordered]@{
        replace = $false
        values = @('#minecraft:enchantable/armor', 'minecraft:elytra')
    }
    Write-JsonFile $tagPath $tag
} finally {
    $zip.Dispose()
}
