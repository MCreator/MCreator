#!/bin/bash
#
# ---------------------------------------------------------------------
# MCreator Launcher for Unix Like systems with Launcher creation
# ---------------------------------------------------------------------
#

# make launcher desktop file
if [[ ! -f "mctoolkit.desktop" ]]; then

pwd=$(pwd)

cat > mctoolkit.desktop <<EOL
[Desktop Entry]
Exec=/bin/bash -c 'cd "${pwd}" && ./mcreator.sh'
Type=Application
Terminal=false
Name=MCreator
Icon=${pwd}/icon.png
EOL

chmod +x mctoolkit.desktop
cp mctoolkit.desktop ~/Desktop/mctoolkit.desktop

fi

# set the classpath
export CLASSPATH='./lib/mcreator.jar:./lib/*'

# launch MCreator with bundled java
./jdk/bin/java net.mcreator.Launcher "$1"