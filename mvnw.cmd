@REM Maven wrapper script for Windows
@setlocal
set DIRNAME=%~dp0
set APP_HOME=%DIRNAME%
set CLASSPATH=%APP_HOME%\.mvn\wrapper\maven-wrapper.jar
java "-Dmaven.multiModuleProjectDirectory=%DIRNAME%." -cp "%CLASSPATH%" org.apache.maven.wrapper.MavenWrapperMain %*
@endlocal
