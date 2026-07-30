param(
    [string]$MinecraftVersion = '1.20.6'
)

$ErrorActionPreference = 'Stop'

$javaVersion = (& java -version 2>&1 | Out-String)
if ($javaVersion -notmatch 'version "21(\.|")') {
    throw "Java 21 is required to build this branch. Found: $($javaVersion.Trim())"
}

$wrapper = Join-Path $PSScriptRoot 'gradlew.bat'
& $wrapper --no-daemon --max-workers=1 clean build
if ($LASTEXITCODE -ne 0) {
    throw "Gradle build failed with exit code $LASTEXITCODE"
}

$dist = Join-Path $PSScriptRoot 'build\dist'
New-Item -ItemType Directory -Force -Path $dist | Out-Null

$artifactBase = "level10-enchantments-$MinecraftVersion-1.5.1"
$binarySource = Join-Path $PSScriptRoot "build\libs\$artifactBase.jar"
$sourcesSource = Join-Path $PSScriptRoot "build\libs\$artifactBase-sources.jar"
foreach ($artifact in @($binarySource, $sourcesSource)) {
    if (-not (Test-Path -LiteralPath $artifact)) {
        throw "Gradle did not create expected artifact: $artifact"
    }
}

$binaryOutput = Join-Path $dist "$artifactBase.jar"
$sourcesOutput = Join-Path $dist "$artifactBase-sources.jar"
Copy-Item -LiteralPath $binarySource -Destination $binaryOutput -Force
Copy-Item -LiteralPath $sourcesSource -Destination $sourcesOutput -Force

Write-Output "Built $binaryOutput"
Write-Output "Built $sourcesOutput"
