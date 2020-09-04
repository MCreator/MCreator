#!/bin/bash
#
# ---------------------------------------------------------------------
# MCreator Launcher for Unix Like systems with Launcher creation
# ---------------------------------------------------------------------
#

# make launcher desktop file
if [[ ! -f "mcreator.desktop" ]]; then

pwd=$(pwd)

cat > mcreator.desktop <<EOL
[Desktop Entry]
Exec=/bin/bash -c 'cd "${pwd}" && ./mcreator.sh'
Type=Application
Terminal=false
Name=MCreator
Icon=${pwd}/icon.png
EOL

chmod +x mcreator.desktop
cp mcreator.desktop ~/Desktop/mcreator.desktop

fi

# set the classpath
export CLASSPATH='./lib/mcreator.jar:./lib/*'

# launch MCreator with bundled java
./jdk/bin/java net.mcreator.Launcher "$1"