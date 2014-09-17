@echo off
rem ensure environment variables only exist in this space
setlocal

rem set the Java options for Emissarius
set ASPIRE_JAVA_OPTS=-Xmx1g -Xms1g -XX:MaxPermSize=256m

rem main script
if not defined EMISSARIUS_HOME goto noHome
if exist "%EMISSARIUS_HOME%" goto Home

:noHome
if exist bundle goto SetThisDir
if exist ..\bundle goto SetParentDir

echo Unable to determine the location of EMISSARIUS_HOME.
echo Either set your working directory to the installation directory, or set the EMISSARIUS_HOME environment variable.
exit /b 0

:SetThisDir
set EMISSARIUS_HOME=%CD%
goto Home

:SetParentDir
cd ..
set EMISSARIUS_HOME=%CD%
goto Home

:Home
cd %EMISSARIUS_HOME%

if exist bundle goto HomeOkay

echo Emissarius Home is not set properly. It should be set to the Emissarius installation directory.
echo Check your EMISSARIUS_HOME environment variable or your installation.
exit /b 0

:HomeOkay
echo Removing Felix-Cache and AppBundle-Cache directories
rmdir /s /q cache >:nul 2>&1

set felix_start_prop=-Dfelix.config.properties=file:conf/felix.properties
set felix_start_prop=%felix_start_prop% "-Dcom.arkitechtura.emissarius.home=%ASPIRE_HOME%"
if defined ASPIRE_LOG_DIR set felix_start_prop=%felix_start_prop% "-Dcom.searchtechnologies.aspire.log.directory=%ASPIRE_LOG_DIR%"

java %ASPIRE_JAVA_OPTS% %felix_start_prop% -jar bin\felix.jar
