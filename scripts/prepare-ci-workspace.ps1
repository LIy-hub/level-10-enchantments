param(
    [string]$Workspace = (Join-Path $PSScriptRoot '..\.ci-workspace')
)

$ErrorActionPreference = 'Stop'
$workspaceRoot = [System.IO.Path]::GetFullPath($Workspace)
$launcher = Join-Path $workspaceRoot 'fabric-server.jar'
$serverJar = Join-Path $workspaceRoot 'versions\26.1.2\server-26.1.2.jar'
$libraries = Join-Path $workspaceRoot 'libraries'
$launcherUrl = 'https://meta.fabricmc.net/v2/versions/loader/26.1.2/0.19.3/1.1.1/server/jar'

New-Item -ItemType Directory -Force -Path $workspaceRoot | Out-Null

if (-not (Test-Path -LiteralPath $launcher)) {
    Invoke-WebRequest -UseBasicParsing -Uri $launcherUrl -OutFile $launcher
}

if (-not (Test-Path -LiteralPath $serverJar) -or -not (Test-Path -LiteralPath $libraries)) {
    Push-Location $workspaceRoot
    try {
        & java -jar $launcher nogui
        if ($LASTEXITCODE -ne 0) {
            throw "Fabric server bootstrap failed with exit code $LASTEXITCODE"
        }
    } finally {
        Pop-Location
    }
}

foreach ($dependency in @($launcher, $serverJar, $libraries)) {
    if (-not (Test-Path -LiteralPath $dependency)) {
        throw "Fabric server bootstrap did not create $dependency"
    }
}

Write-Output "LEVEL10_WORKSPACE=$workspaceRoot"
