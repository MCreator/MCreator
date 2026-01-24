#!/bin/bash
#
# ---------------------------------------------------------------------
# MCreator Launcher for Unix Like systems with Launcher creation
# ---------------------------------------------------------------------
#

CDIR="$(cd "$(dirname "$0")" && pwd)"
cd "$CDIR"

# make launcher desktop file
if [[ ! -f "mcreator.desktop" ]]; then

cat > mcreator.desktop <<EOL
[Desktop Entry]
Exec=/bin/bash -c 'cd "${CDIR}" && ./mcreator.sh'
Type=Application
Terminal=false
Name=MCreator
Icon=${CDIR}/icon.png
EOL

chmod +x mcreator.desktop

if command -v xdg-user-dir &> /dev/null; then
    userdesktop=$(xdg-user-dir DESKTOP)
else
    # Fall back to hardcoded ~/Desktop
    userdesktop=~/Desktop
fi

if [ -d "$userdesktop" ]; then
    cp mcreator.desktop "${userdesktop}"/mcreator.desktop
fi

fi

# set the classpath
export CLASSPATH='./lib/mcreator.jar:./lib/*'

if [ -f "./jdk/bin/java" ]; then
    chmod +x ./jdk/bin/java
    # launch MCreator with bundled java
    ./jdk/bin/java --add-opens=java.base/java.lang=ALL-UNNAMED net.mcreator.Launcher "$1"
else
    echo "Bundled JRE not found at ./jdk/bin/java!"
    echo "Using system java instead"
    java --add-opens=java.base/java.lang=ALL-UNNAMED net.mcreator.Launcher "$1"
fi