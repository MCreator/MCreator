Here you can specify animations that this entity can play and the conditions when animations happen.

**Make sure that animations you use only reference model parts/bones that are present in the model file.
Otherwise, the model may fail to load or the game may crash.**

We recommend using Synced data to provide data for animation conditions, as the condition is client-side
only, but data for toggling animations is usually not client-side.

Existing model animations specified in the model file or during model file import will also
play alongside animations specified here.