#!/bin/zsh

name="$1"
folderPath="$2"
echo "name: $name"
echo "folderPath: $folderPath"

create-dmg \
  --volname "MCreator" \
  --volicon "platform/mac/disk/volume.icns" \
  --background "platform/mac/disk/bg.tiff" \
  --window-pos 200 120 \
  --window-size 720 480 \
  --text-size 12 \
  --icon-size 150 \
  --icon "MCreator.app" 230 208 \
  --hide-extension "MCreator.app" \
  --app-drop-link 492 208 \
  --eula "LICENSE.txt" \
  --format "ULMO" \
  "$name" \
  "$folderPath"
