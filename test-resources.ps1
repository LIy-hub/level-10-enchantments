param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$GradleArgs
)

$ErrorActionPreference = 'Stop'
& (Join-Path $PSScriptRoot 'gradlew.bat') runResourceParityTest @GradleArgs
if ($LASTEXITCODE -ne 0) {
    throw "Gradle resource parity test failed with exit code $LASTEXITCODE"
}
