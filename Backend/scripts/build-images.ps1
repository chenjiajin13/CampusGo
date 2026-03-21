param(
  [string]$Tag = "latest",
  [string]$Prefix = "campusgo"
)

$ErrorActionPreference = "Stop"

$services = @(
  "registry-eureka",
  "config-server",
  "gateway",
  "user-service",
  "order-service",
  "auth-service",
  "runner-service",
  "merchant-service",
  "payment-service",
  "notification-service",
  "admin-service"
)

Write-Host "[1/2] Packaging jars..."
cmd /c mvn -DskipTests clean package

Write-Host "[2/2] Building docker images..."
foreach($s in $services){
  $img = "$Prefix/$s`:$Tag"
  Write-Host "Building $img"
  docker build -t $img $s
}

Write-Host "Done."
