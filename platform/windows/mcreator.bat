@echo off
color a
echo.
title MCreator Launcher
::
:: ---------------------------------------------------------------------
:: MCreator Launcher
:: ---------------------------------------------------------------------
echo.
echo.
echo   -------------------------------------
echo            MCreator Launcher
echo   -------------------------------------

echo.
echo.

echo  Launching MCreator...
:: set the classpath
ECHO.
set CLASSPATH=./lib/mcreator.jar;./lib/*
echo.
echo.
:: launch MCreator with bundled java
"./jdk/bin/java.exe" --add-opens=java.base/java.lang=ALL-UNNAMED net.mcreator.Launcher
echo.
