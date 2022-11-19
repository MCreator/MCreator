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

# Is xdg-user-dir available?
if ! command -v xdg-user-dir &> /dev/null
then
    # Use dynamic desktop directory for other languages (#3123)
    userdesktop=$(xdg-user-dir DESKTOP)
else
    # Fall back to hardcoded ~/Desktop
    userdesktop=~/Desktop
fi
cp mcreator.desktop "${userdesktop}"/mcreator.desktop

fi

# set the classpath
export CLASSPATH='./lib/mcreator.jar:./lib/*'

# launch MCreator with bundled java
./jdk/bin/java --add-opens=java.base/java.lang=ALL-UNNAMED net.mcreator.Launcher "$1"
