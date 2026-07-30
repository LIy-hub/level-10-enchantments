param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$GradleArgs
)

$ErrorActionPreference = 'Stop'
Write-Output 'Starting Loom isolated development server. Use "stop" in the server console to exit.'
& (Join-Path $PSScriptRoot 'gradlew.bat') runServer @GradleArgs
if ($LASTEXITCODE -ne 0) {
    throw "Gradle runServer failed with exit code $LASTEXITCODE"
}
