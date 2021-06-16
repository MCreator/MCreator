If emit redstone parameter is checked, this parameter will allow you to modify the emitted redstone power.

When this a conditional procedure is used and returns a number, 
the redstone power emitted by this block will be set by the returned number of the procedure.

NOTE: The block can still emit redstone even if redstone doesn't connect to it.

WARNING: When using custom number provider procedure, you will need to notify neighbouring 
blocks of redstone value change for them to register the change.