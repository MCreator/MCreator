@echo off
color a
title MCreator Launcher
::
:: ---------------------------------------------------------------------
:: MCreator Launcher
:: ---------------------------------------------------------------------
echo.
echo   -------------------------------------
echo            MCreator Launcher
echo    ------------------------------------

::
echo.
:: set the classpath
set CLASSPATH=./lib/mcreator.jar;./lib/*

echo.
:: launch MCreator with bundled java
"./jdk/bin/java.exe" --add-opens=java.base/java.lang=ALL-UNNAMED net.mcreator.Launcher
echo.
