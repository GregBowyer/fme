@echo off

rem ***********************************************
rem
rem   File: shell.bat
rem
rem   Author: Matthias Ladkau
rem
rem   Description:
rem
rem     Script to set up the environment for the FME.
rem
rem ***********************************************

rem Set paths

set ROOT_PATH=%~sdp0
set SZIP_HOME=%ROOT_PATH%res\7zip_4_65_win
set PERL_HOME=%ROOT_PATH%engine\strawberry-perl-5.10.0.5-portable
set FERMAT3_HOME=%ROOT_PATH%engine\fermat3\MinGW

set PATH=%PATH%;%SZIP_HOME%;%PERL_HOME%\perl\bin;%PERL_HOME%\c\bin;%FERMAT3_HOME%

rem Extract external resources if necessary

if not exist "%ROOT_PATH%engine" (mkdir "%ROOT_PATH%engine")
if not exist "%ROOT_PATH%engine\fermat3" (7za x -o%ROOT_PATH%engine %ROOT_PATH%res\fermat3.7z)
if not exist "%ROOT_PATH%engine\strawberry-perl-5.10.0.5-portable" (7za x -o%ROOT_PATH%engine %ROOT_PATH%res\strawberry-perl-5.10.0.5-portable.7z)

rem FermaT initialization

set FermaT=%~sdp0engine\fermat3
set PATH=%FERMAT%\dosbin;%PATH%
set SCHEME_LIBRARY_PATH=%FERMAT%\slib\
set SCM_INIT_PATH=%FERMAT%\scm\Init5f1.scm
if exist %FERMAT%\scm\slibcat del %FERMAT%\scm\slibcat

rem Test commands

cmd /C java -version > NUL 2> NUL
if not "%errorlevel%" == "0" (
    set ERRORMSG="java" command not found. Please install a current JDK and put its bin directory in your PATH variable.
    goto error
)

cmd /C scmfmt --version > NUL 2> NUL
if not "%errorlevel%" == "0" (
    set ERRORMSG="scmfmt" command not found. Cannot continue...
    goto error
)

if "%1%" == "source" (
    goto end
)

if not "%1%" == "" (
    start %1 %2 %3 %4
    exit 0
)

cmd

goto end

:error
echo ERROR:
echo %ERRORMSG%
pause

:end
