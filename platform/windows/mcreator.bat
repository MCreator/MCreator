@echo off
::
:: ---------------------------------------------------------------------
:: MCreator Launcher
:: ---------------------------------------------------------------------
::
:: set the classpath
set CLASSPATH=./lib/mcreator.jar;./lib/*

if exist "./jdk/bin/java.exe" (
"./jdk/bin/java.exe" --add-opens=java.base/java.lang=ALL-UNNAMED net.mcreator.Launcher
) else (
    java --add-opens=java.base/java.lang=ALL-UNNAMED net.mcreator.Launcher
)
