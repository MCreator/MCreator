This parameter controls the block that generates under the water. 
Most vanilla biomes use dirt or gravel block for this.

Avoid using complex blocks such as:

* transparent blocks
* blocks that are not full cube
* blocks with tile entity, NBT tags, or inventory
* blocks that are used as POI
* blocks that tick

If you use such blocks, the worldgen will be slow and the loaded world may lag severely.