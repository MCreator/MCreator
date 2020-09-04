[![MCreator](https://mcreator.net/image/brand/mcreator300s.png)](https://mcreator.net/)

[![License](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://github.com/MCreator/MCreator/blob/master/LICENSE)

# MCreator - Minecraft Mod Maker

<img align="right" width="300" src="https://mcreator.net/image/mainwindow.png">

https://mcreator.net/ - MCreator is a software used to make Minecraft Java Edition mods, Minecraft Bedrock Edition Add-Ons, and data packs using an intuitive easy-to-learn interface or with an integrated code editor. 

It is used worldwide by Minecraft players, aspiring mod developers, for education, online classes, and STEM workshops.

## Download and support

This repository page is for people looking to contribute to MCreator. Visit https://mcreator.net/ to download MCreator as a user and check https://mcreator.net/support to find the support, forums, knowledge base and more.

Download MCreator binary distributions on https://mcreator.net/download.

## Contributing

You are welcome to support this project by opening pull requests.

Before we can use your code, you must sign the [MCreator CLA](https://cla-assistant.io/MCreator/MCreator), which you can do online. The CLA is necessary mainly because you own the copyright to your changes, even after your contribution becomes part of our codebase, so we need your permission to use and distribute your code. We also need to be sure of various other things—for instance that you'll tell us if you know that your code infringes on other people's patents. You don't have to sign the CLA until after you've submitted your code for review and we approved it, but you must do it before we can put your code into our codebase.

Big thanks to [all the people](https://github.com/MCreator/MCreator/graphs/contributors) who already contributed to MCreator!

## Development

To clone this repository, run `git clone --recursive https://github.com/MCreator/MCreator.git`.

MCreator uses Gradle build system to manage the building and exporting. Use `runMCreator` task to run test MCreator.

It is recommended to use Intellij IDEA for development and testing. When used with IDEA, _use provided run configurations to test and export MCreator_. For the compilation to work properly, add new SDK called `mcreator_sdk` and point it to `<MCreator workspace root>\jdk\jdk8_win_64` (in case of 64-bit Windows environment).

## License and trademark

MCreator is licensed under the GPL-3.0 license (with exceptions implemented as specified in section 7 of GPL-3.0) if not otherwise stated in source files or other files of this project. Copyright 2020 Pylo and [contributors](https://github.com/MCreator/MCreator/graphs/contributors).

MCreator is a trademark of Pylo. Custom distributions of this software may not include Pylo or MCreator trademark (trademark name and logo) to not confuse the software with the official distribution of MCreator project.
MCreator and Pylo brand files in this repository are not covered by the GPL-3.0 license.

## Notice

NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG.
