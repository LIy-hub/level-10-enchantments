param(
    [string]$Workspace = (Join-Path $PSScriptRoot '..\.ci-workspace'),
    [string]$MinecraftVersion
)

$ErrorActionPreference = 'Stop'

if ([string]::IsNullOrWhiteSpace($MinecraftVersion)) {
    $MinecraftVersion = ((Get-Content (Join-Path $PSScriptRoot '..\gradle.properties') |
        Where-Object { $_ -like 'minecraft_version=*' }) -split '=', 2)[1]
}

$workspaceRoot = [System.IO.Path]::GetFullPath($Workspace)
$versionDirectory = Join-Path $workspaceRoot "versions\$MinecraftVersion"
$serverJar = Join-Path $versionDirectory "server-$MinecraftVersion.jar"
$bundleJar = Join-Path $versionDirectory "server-bundle-$MinecraftVersion.jar"

if (Test-Path -LiteralPath $serverJar) {
    Write-Output "LEVEL10_WORKSPACE=$workspaceRoot"
    Write-Output "MINECRAFT_SERVER_JAR=$serverJar"
    exit 0
}

New-Item -ItemType Directory -Force -Path $versionDirectory | Out-Null

$manifestUrl = 'https://piston-meta.mojang.com/mc/game/version_manifest_v2.json'
$manifest = Invoke-RestMethod -UseBasicParsing -Uri $manifestUrl
$versionEntry = @($manifest.versions | Where-Object { $_.id -eq $MinecraftVersion })
if ($versionEntry.Count -ne 1) {
    throw "Minecraft version $MinecraftVersion is not present in Mojang's version manifest"
}

$versionMetadata = Invoke-RestMethod -UseBasicParsing -Uri $versionEntry[0].url
if ($null -eq $versionMetadata.downloads.server.url) {
    throw "Minecraft version $MinecraftVersion does not publish a server download"
}

Invoke-WebRequest -UseBasicParsing -Uri $versionMetadata.downloads.server.url -OutFile $bundleJar
$downloadHash = (Get-FileHash -LiteralPath $bundleJar -Algorithm SHA1).Hash.ToLowerInvariant()
if ($downloadHash -ne $versionMetadata.downloads.server.sha1.ToLowerInvariant()) {
    throw "Minecraft server SHA-1 mismatch: expected $($versionMetadata.downloads.server.sha1), got $downloadHash"
}

Add-Type -AssemblyName System.IO.Compression.FileSystem
$bundle = [System.IO.Compression.ZipFile]::OpenRead($bundleJar)
try {
    $nestedPath = "META-INF/versions/$MinecraftVersion/server-$MinecraftVersion.jar"
    $nestedServer = $bundle.GetEntry($nestedPath)
    if ($null -eq $nestedServer) {
        Move-Item -LiteralPath $bundleJar -Destination $serverJar
    } else {
        [System.IO.Compression.ZipFileExtensions]::ExtractToFile($nestedServer, $serverJar, $true)
    }
} finally {
    $bundle.Dispose()
}

if (Test-Path -LiteralPath $bundleJar) {
    Remove-Item -LiteralPath $bundleJar -Force
}
if (-not (Test-Path -LiteralPath $serverJar)) {
    throw "Minecraft server preparation did not create $serverJar"
}

Write-Output "LEVEL10_WORKSPACE=$workspaceRoot"
Write-Output "MINECRAFT_SERVER_JAR=$serverJar"
