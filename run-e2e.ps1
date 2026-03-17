# Lance le back, puis le front + Cypress E2E.
# Usage : depuis la racine du projet, .\run-e2e.ps1
# Pre requis : Docker demarre (MySQL), Node et Java installes.

$ErrorActionPreference = "Stop"
$backDir = Join-Path $PSScriptRoot "back"
$frontDir = Join-Path $PSScriptRoot "front"
$envPath = Join-Path $backDir ".env"

# Creer .env si absent (valeurs dev pour E2E)
if (-not (Test-Path $envPath)) {
    Write-Host "Creation de back/.env avec des valeurs de dev..." -ForegroundColor Yellow
    @"
DB_NAME=mdd
DB_USER=mdd
DB_PASSWORD=mdd
DB_ROOT_PASSWORD=root_password
DB_HOST=localhost
DB_PORT=3307
JWT_EXPIRATION_MS=900000
REFRESH_TOKEN_EXPIRATION_MS=1209600000
TOKEN_SECRET=dev-secret-change-me-in-production-min-32-bytes
COOKIE_SECURE=false
SEED_DEMO_DATA=false
"@ | Set-Content $envPath -Encoding UTF8
}

# 1) Demarrer le back en arriere-plan
Write-Host "Demarrage du back-end (port 8080)..." -ForegroundColor Cyan
$backJob = Start-Job -ScriptBlock {
    Set-Location $using:backDir
    & .\mvnw.cmd spring-boot:run -q 2>&1
}

# Attendre que le back reponde
$maxWait = 90
$elapsed = 0
while ($elapsed -lt $maxWait) {
    try {
        $r = Invoke-WebRequest -Uri "http://localhost:8080/api/health" -UseBasicParsing -TimeoutSec 2 -ErrorAction SilentlyContinue
        if ($r.StatusCode -eq 200) { break }
    } catch {}
    Start-Sleep -Seconds 3
    $elapsed += 3
    Write-Host "  Attente back... ${elapsed}s" -ForegroundColor Gray
}
if ($elapsed -ge $maxWait) {
    Stop-Job $backJob
    Remove-Job $backJob
    Write-Host "Le back n a pas demarre a temps. Verifiez Docker (MySQL) et back/.env" -ForegroundColor Red
    exit 1
}
Write-Host "Back pret." -ForegroundColor Green

# 2) Lancer le front + Cypress (start-server-and-test attend le front puis lance Cypress)
Write-Host "Lancement du front et des tests Cypress..." -ForegroundColor Cyan
Push-Location $frontDir
try {
    & npm run e2e
    $e2eExit = $LASTEXITCODE
} finally {
    Pop-Location
}

# Arreter le back
Stop-Job $backJob -ErrorAction SilentlyContinue
Remove-Job $backJob -ErrorAction SilentlyContinue
exit $e2eExit
