@echo off
rem ***********************************************
rem
rem   File: fme.bat
rem
rem   Author: Matthias Ladkau
rem
rem   Description:
rem
rem     This is the FME start script.
rem
rem ***********************************************

set ROOT_PATH=%~sdp0
cd %ROOT_PATH%

rem Make sure the environment is set up
call env.bat source

if not "%errorlevel%" == "0" (
    set ERRORMSG="java" command not found. Please install a current JDK and put its bin directory in your PATH variable.
    goto error
)


start /B javaw -classpath ./lib/fme.jar;./lib/stringsearch-2.jar;./lib/toniclf-1.0.5.jar;./lib/xercesImpl.jar;./lib/xmlParserAPIs.jar;./lib/zvtm.jar fme.FMEMain > FME_out.txt 2> FME_err.txt

:error
