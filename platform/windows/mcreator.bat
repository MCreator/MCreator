@echo off
::
:: ---------------------------------------------------------------------
:: MCreator Launcher
:: ---------------------------------------------------------------------
::

:: set the classpath
set CLASSPATH=./lib/mcreator.jar;./lib/*

:: launch MCreator with bundled java
"./jdk/bin/java.exe" net.mcreator.Launcher