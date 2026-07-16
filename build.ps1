$ErrorActionPreference = 'Stop'

$workspace = Resolve-Path (Join-Path $PSScriptRoot '..\..')
$serverJar = Join-Path $workspace 'versions\26.1.2\server-26.1.2.jar'
$libraries = Join-Path $workspace 'libraries'
$build = Join-Path $PSScriptRoot 'build\package'
$classes = Join-Path $build 'classes'
$dist = Join-Path $PSScriptRoot 'build\dist'
$output = Join-Path $dist 'level10-enchantments-1.4.0.jar'
$sourcesOutput = Join-Path $dist 'level10-enchantments-1.4.0-sources.jar'

$javacVersion = (& javac -version 2>&1 | Out-String).Trim()
if ($javacVersion -notmatch '^javac 25\.') {
    throw "Java 25 javac is required, found: $javacVersion"
}
foreach ($dependency in @($serverJar, $libraries)) {
    if (-not (Test-Path -LiteralPath $dependency)) {
        throw "Missing build dependency: $dependency"
    }
}

if (Test-Path -LiteralPath $build) {
    Remove-Item -LiteralPath $build -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $classes | Out-Null
New-Item -ItemType Directory -Force -Path $dist | Out-Null

& (Join-Path $PSScriptRoot 'generate-resources.ps1') -Output $classes

$sources = @(Get-ChildItem (Join-Path $PSScriptRoot 'src\main\java') -Filter '*.java' -Recurse -File |
    Sort-Object FullName | Select-Object -ExpandProperty FullName)
if ($sources.Count -ne 9) {
    throw "Expected 9 Java sources, found $($sources.Count)"
}

$libraryJars = @(Get-ChildItem $libraries -Filter '*.jar' -Recurse -File |
    Sort-Object FullName | Select-Object -ExpandProperty FullName)
if ($libraryJars.Count -eq 0) {
    throw "No server libraries found under $libraries"
}
$classpath = (@($serverJar) + $libraryJars) -join ';'
& javac --release 25 -proc:none -classpath $classpath -d $classes $sources
if ($LASTEXITCODE -ne 0) {
    throw "javac failed with exit code $LASTEXITCODE"
}

$resources = Join-Path $PSScriptRoot 'src\main\resources'
Copy-Item (Join-Path $resources 'fabric.mod.json') $classes
Copy-Item (Join-Path $resources 'level10enchantments.mixins.json') $classes
Copy-Item (Join-Path $resources 'level10-enchantments.rules.csv') $classes
Copy-Item (Join-Path $PSScriptRoot 'LICENSE') $classes
Copy-Item (Join-Path $PSScriptRoot 'COPYING') $classes
Copy-Item (Join-Path $PSScriptRoot 'COPYING.LESSER') $classes
$iconSource = Join-Path $resources 'assets\level10enchantments\icon.png'
$iconTarget = Join-Path $classes 'assets\level10enchantments'
New-Item -ItemType Directory -Force -Path $iconTarget | Out-Null
Copy-Item $iconSource $iconTarget

if (Test-Path -LiteralPath $output) {
    Remove-Item -LiteralPath $output -Force
}
& jar --create --file $output -C $classes .
if ($LASTEXITCODE -ne 0) {
    throw "jar failed with exit code $LASTEXITCODE"
}

if (Test-Path -LiteralPath $sourcesOutput) {
    Remove-Item -LiteralPath $sourcesOutput -Force
}
& jar --create --file $sourcesOutput `
    -C $PSScriptRoot LICENSE `
    -C $PSScriptRoot COPYING `
    -C $PSScriptRoot COPYING.LESSER `
    -C $PSScriptRoot build.ps1 `
    -C $PSScriptRoot generate-resources.ps1 `
    -C $PSScriptRoot test-resources.ps1 `
    -C $PSScriptRoot test.ps1 `
    -C $PSScriptRoot verify-server.ps1 `
    -C $PSScriptRoot src
if ($LASTEXITCODE -ne 0) {
    throw "sources jar failed with exit code $LASTEXITCODE"
}

Write-Output "Built $output"
Write-Output "Built $sourcesOutput"
