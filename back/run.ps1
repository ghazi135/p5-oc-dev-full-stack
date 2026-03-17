# Lance l'API MDD. Requiert JDK 17 ou 21 (Spring Boot 3).
# Si vous avez "release version 21 not supported", installez JDK 21 et définissez JAVA_HOME.

$minVersion = 17
$versionLine = & java -version 2>&1 | Select-Object -First 1
if ($versionLine -match 'version "(\d+)') {
    $ver = [int]$matches[1]
    if ($ver -lt $minVersion) {
        Write-Host "Erreur : ce projet requiert JDK $minVersion ou 21. Version actuelle : $ver" -ForegroundColor Red
        Write-Host ""
        Write-Host "Installez JDK 21 (recommandé) :" -ForegroundColor Yellow
        Write-Host "  https://adoptium.net/temurin/releases/?version=21&os=windows"
        Write-Host ""
        Write-Host "Puis définissez JAVA_HOME vers le JDK 21, ex. :" -ForegroundColor Yellow
        Write-Host '  $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot"'
        exit 1
    }
} else {
    Write-Host "Impossible de détecter la version Java. Vérifiez que 'java' est dans le PATH." -ForegroundColor Red
    exit 1
}

$jar = "target\mdd-api-0.0.1-SNAPSHOT.jar"
if (Test-Path $jar) {
    Write-Host "Démarrage de l'API via le JAR..." -ForegroundColor Green
    & java -jar $jar
} else {
    Write-Host "JAR absent. Lancement avec Maven (tests ignorés)..." -ForegroundColor Green
    & .\mvnw.cmd spring-boot:run "-Dmaven.test.skip=true"
}
