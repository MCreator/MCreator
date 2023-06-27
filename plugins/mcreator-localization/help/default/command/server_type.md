This option allow you to determine on which type(s) of server the command can be used.
* **Both**: The command can be used everywhere. THis is the normal behaviour (e.g. `/give`.
* **Multi players only**: The command will be available on multi players servers only (e.g. `/ban`)
* **Single player only**: The command will be available on single player worlds only (e.g. `/publish`)

Even if the option is set to `Single player only`, this does not mean the command is a client side command only.
The command is still registered on the server as usual.

If the command is marked as client-side only, this option will be disabled.