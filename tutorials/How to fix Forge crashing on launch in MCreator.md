### Table of Contents

- Table of Contents - Your Here Now!
- [Starting](#starting)
- [Opening `build.gradle`](#opening-buildgradle)
- [Pasting the Line](#pasting-the-line)
- [Compiling](#compiling)
- [Info](#info)
- [It's Not Working :(](#its-not-working-)

If you're struggling with your Forge on MacOS/Windows when directly running a copy from MCreator, you may have to disable the early loading screen.

You CAN search up how to fix this... but that's for the MC launcher/other launchers that support JVM arguments.

Anyways, let's start.

# Starting

(Go back up to [Table of Contents](#table-of-contents))

First copy this line of code at the end of the sentence since it is VERY important! `jvmArgs "-Dfml.earlyprogresswindow=false"`

Now, open the MCreator Workspace File Explorer (or the MCWFE/WFE) which can be found by pressing the arrow to the left of the workspace button. It will then open up this window:

<img width="359" alt="Screenie 1" src="https://user-images.githubusercontent.com/69256931/112299742-00b56800-8c90-11eb-9b59-0cd6cb0a7dc1.png">

(Go back up to [Table of Contents](#table-of-contents))

# Opening `build.gradle`

(Go back up to [Table of Contents](#table-of-contents))

Now, double-click on `build.gradle`! You should see all this code:

<img width="1045" alt="Screenie 2" src="https://user-images.githubusercontent.com/69256931/112299961-3bb79b80-8c90-11eb-8326-26ce57c8c7ae.png">

You'll need to go down to Line 28, put in a space and then paste that line of code you copied.

(Go back up to [Table of Contents](#table-of-contents))

# Pasting the Line

(Go back up to [Table of Contents](#table-of-contents))

It should look like this:

<img width="550" alt="Last Screenie" src="https://user-images.githubusercontent.com/69256931/112300168-73264800-8c90-11eb-8254-edd11e51bdc2.png">

(Go back up to [Table of Contents](#table-of-contents))

# Compiling

(Go back up to [Table of Contents](#table-of-contents))

Now, close `build.gradle` and press CMD + B or CTRL + B or press the Hammer button to Build the mod/Save the files and compile.

Hit run and Forge should be working!

(Go back up to [Table of Xontents](#table-of-contents))

# Info

(Go back up to [Table of Contents](#table-of-contents))

**YOUR FORGE WILL TAKE TIME TO LOAD AFTER DOING THIS HOWEVER IT'LL TAKE ~20 SECONDS!**

If you want to add your own Tutorial, make a pull request :)

(Go back up to [Table of Contents](#table-of-contents))

## It's Not Working :(

(Go back up to [Table of Contents](#table-of-contents))

Again, if it's not working, wait around ~20-60 seconds.

Else, please DM me on Discord: MCreatorTutorialHelp#3043

**[END TUTORIAL](https://github.com/MCreator/MCreator/tutorials)**
