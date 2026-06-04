This parameter controls how much light the block emits. 

It is a value between 0 and 15.

If set to 0, the block won't emit light. 
If set to 15, the block will emit as much light as glowstone.

If controlled by a procedure (supported by block mod elements), 
make sure to only get block state values
and not modify them, or the game will crash.

Additionally, keep in mind the value is cached, so you cannot use
random as a light level source.