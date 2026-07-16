param(
    [switch]$ExcludeTaxFreeLevels
)

$ErrorActionPreference = 'Stop'

function Assert([bool]$Condition, [string]$Message) {
    if (-not $Condition) {
        throw "Assertion failed: $Message"
    }
}

$workspace = if ([string]::IsNullOrWhiteSpace($env:LEVEL10_WORKSPACE)) {
    Resolve-Path (Join-Path $PSScriptRoot '..\..')
} else {
    Resolve-Path -LiteralPath $env:LEVEL10_WORKSPACE
}
$buildRoot = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot 'build'))
$variant = if ($ExcludeTaxFreeLevels) { 'without-taxfreelevels' } else { 'with-taxfreelevels' }
$testRoot = [System.IO.Path]::GetFullPath((Join-Path $buildRoot "integration-server-$variant"))
$patchRoot = [System.IO.Path]::GetFullPath($PSScriptRoot)

Assert ($testRoot.StartsWith($patchRoot + [System.IO.Path]::DirectorySeparatorChar,
        [System.StringComparison]::OrdinalIgnoreCase)) 'integration directory stays inside patch directory'

if (Test-Path -LiteralPath $testRoot) {
    foreach ($junctionName in @('libraries', 'versions')) {
        $junction = Join-Path $testRoot $junctionName
        if (Test-Path -LiteralPath $junction) {
            $item = Get-Item -LiteralPath $junction -Force
            if (($item.Attributes -band [System.IO.FileAttributes]::ReparsePoint) -ne 0) {
                [System.IO.Directory]::Delete($junction, $false)
            }
        }
    }
    Remove-Item -LiteralPath $testRoot -Recurse -Force
}

New-Item -ItemType Directory -Force -Path $testRoot | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $testRoot 'mods') | Out-Null
Copy-Item (Join-Path $workspace 'fabric-server.jar') $testRoot
$candidateJar = Join-Path $PSScriptRoot 'build\dist\level10-enchantments-1.4.1.jar'
Assert (Test-Path -LiteralPath $candidateJar) 'tested Level 10 Enchantments candidate exists'
Get-ChildItem (Join-Path $workspace 'mods') -Filter '*.jar' -File |
    Where-Object { $_.Name -notlike 'level10-enchantments-*.jar' } |
    Where-Object { -not $ExcludeTaxFreeLevels -or $_.Name -notlike 'TaxFreeLevels-*.jar' } |
    Copy-Item -Destination (Join-Path $testRoot 'mods')
Copy-Item $candidateJar (Join-Path $testRoot 'mods')
if (Test-Path -LiteralPath (Join-Path $workspace 'config')) {
    Copy-Item (Join-Path $workspace 'config') $testRoot -Recurse
}
if (Test-Path -LiteralPath (Join-Path $workspace 'defaultconfigs')) {
    Copy-Item (Join-Path $workspace 'defaultconfigs') $testRoot -Recurse
}
Copy-Item (Join-Path $workspace 'libraries') $testRoot -Recurse
Copy-Item (Join-Path $workspace 'versions') $testRoot -Recurse

$utf8NoBom = [System.Text.UTF8Encoding]::new($false)
[System.IO.File]::WriteAllText((Join-Path $testRoot 'eula.txt'), "eula=true`n", $utf8NoBom)
$properties = @(
    'allow-flight=true',
    'difficulty=easy',
    'enable-query=false',
    'enable-rcon=false',
    'level-name=test-world',
    'max-players=1',
    'motd=Level 10 Enchantments integration test',
    'online-mode=false',
    'server-ip=127.0.0.1',
    'server-port=0',
    'simulation-distance=2',
    'spawn-protection=0',
    'view-distance=2'
) -join "`n"
[System.IO.File]::WriteAllText((Join-Path $testRoot 'server.properties'), $properties + "`n", $utf8NoBom)

$java = (Get-Command java).Source
$startInfo = [System.Diagnostics.ProcessStartInfo]::new()
$startInfo.FileName = $java
$startInfo.Arguments = '-Xms1G -Xmx2G -jar fabric-server.jar nogui'
$startInfo.WorkingDirectory = $testRoot
$startInfo.UseShellExecute = $false
$startInfo.CreateNoWindow = $true
$startInfo.RedirectStandardInput = $true
$startInfo.RedirectStandardOutput = $true
$startInfo.RedirectStandardError = $true

$process = [System.Diagnostics.Process]::new()
$process.StartInfo = $startInfo
Assert ($process.Start()) 'Java process started'

$stdoutTask = $process.StandardOutput.ReadLineAsync()
$stderrTask = $process.StandardError.ReadToEndAsync()
$lines = [System.Collections.Generic.List[string]]::new()
$deadline = [DateTime]::UtcNow.AddSeconds(120)
$sawDone = $false

while (-not $process.HasExited -and [DateTime]::UtcNow -lt $deadline -and -not $sawDone) {
    if ($stdoutTask.Wait(250)) {
        $line = $stdoutTask.Result
        if ($null -eq $line) {
            break
        }
        $lines.Add($line)
        if ($line -match 'Done \(') {
            $sawDone = $true
            break
        }
        $stdoutTask = $process.StandardOutput.ReadLineAsync()
    }
}

if ($sawDone) {
    $process.StandardInput.WriteLine('stop')
    $process.StandardInput.Flush()
    $stdoutTask = $process.StandardOutput.ReadLineAsync()
}

$shutdownDeadline = [DateTime]::UtcNow.AddSeconds(60)
while (-not $process.HasExited -and [DateTime]::UtcNow -lt $shutdownDeadline) {
    if ($stdoutTask.Wait(250)) {
        $line = $stdoutTask.Result
        if ($null -ne $line) {
            $lines.Add($line)
            $stdoutTask = $process.StandardOutput.ReadLineAsync()
        }
    }
}

if (-not $process.HasExited) {
    $process.Kill()
    throw 'Integration server did not stop within 60 seconds'
}
$process.WaitForExit()

while ($null -ne ($line = $process.StandardOutput.ReadLine())) {
    $lines.Add($line)
}
$stderr = $stderrTask.Result
$combinedLog = (($lines -join "`n") + "`n" + $stderr)
$logPath = Join-Path $PSScriptRoot "build\integration-server-$variant.log"
[System.IO.File]::WriteAllText($logPath, $combinedLog, $utf8NoBom)

Assert $sawDone 'server reached Done'
Assert ($process.ExitCode -eq 0) "server exit code $($process.ExitCode)"
Assert ($combinedLog -match 'level10enchantments') 'mod listed as loaded'
Assert ($combinedLog -notmatch 'Mixin apply failed|InjectionError|Couldn''t parse data file|Failed to start the minecraft server') 'no fatal load error'
$taxFreeListed = $combinedLog -match '(?m)^\s*-\s+taxfreelevels\s'
if ($ExcludeTaxFreeLevels) {
    Assert (-not $taxFreeListed) 'TaxFreeLevels omitted from isolated mod list'
} else {
    Assert $taxFreeListed 'TaxFreeLevels present in isolated mod list'
}
Assert (Test-Path -LiteralPath (Join-Path $testRoot 'test-world\level.dat')) 'isolated test world created'
Assert (-not ([System.IO.Path]::GetFullPath((Join-Path $testRoot 'test-world')).StartsWith(
        [System.IO.Path]::GetFullPath((Join-Path $workspace 'world')) + [System.IO.Path]::DirectorySeparatorChar,
        [System.StringComparison]::OrdinalIgnoreCase))) 'test world is separate from production world'

Write-Output "INTEGRATION_LOG=$logPath"
Write-Output "PASS: isolated Fabric server ($variant) loaded Level 10 Enchantments and stopped cleanly."
