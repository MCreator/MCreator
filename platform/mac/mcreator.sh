#!/bin/zsh

# ---------------------------------------------------------------------
# MCreator Launcher file for macOS
# zsh is now used due to bash being missing on newer macOS distributions
# ---------------------------------------------------------------------

# WARNING: WHEN CHANGING THIS FILE, MCREATOR BINARIES NEED TO BE RECOMPILED WITH SHC
# To compile the intel binary on an aarch64 machine, re-brew shc under rosetta
# Use the prefix /opt/homebrew/bin/shc for aarch64 and /usr/local/bin/shc for x86_64

setopt +o nomatch
DIR=$(cd "$(dirname "$0")"; pwd)
cd "$DIR"

./jdk/bin/java \
  --add-opens=java.base/java.lang=ALL-UNNAMED \
  -Xdock:name=MCreator \
  -Xdock:icon=../Resources/mcreatorapp.icns \
  -Dapple.awt.application.appearance=system \
  -cp .:./lib/mcreator.jar:./lib/* \
  net.mcreator.Launcher