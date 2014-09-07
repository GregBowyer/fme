@echo off

set ROOT_PATH=%~sdp0
cd %ROOT_PATH%

rem Make sure the environment is set up
call env.bat source

if not "%errorlevel%" == "0" (
    set ERRORMSG="Could not setup environment"
    goto error
)

cmd
