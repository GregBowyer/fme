@echo off
rem ***********************************************
rem
rem   File: generate_xml.bat
rem
rem   Author: Matthias Ladkau
rem
rem   Description:
rem
rem     Script to generate the transformation 
rem     list from the FermaT engine source code.
rem
rem ***********************************************
cd %~ds0
call ..\..\env.bat source

perl generate_transformation_table.pl > transformation_table.xml

if "%errorlevel%" == "0" (
    echo File "transformation_table.xml" was successfully generated.
)
pause
