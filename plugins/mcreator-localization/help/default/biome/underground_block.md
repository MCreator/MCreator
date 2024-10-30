This parameter controls the underground block below the ground block layer.

Generally, vanilla or custom dirt is used here for most biomes.

This block should be tagged in `minecraft:dirt` Blocks tags for plants and
trees to spawn properly on the surface.

Avoid using complex blocks such as:

* transparent blocks
* blocks that are not full cube
* blocks with tile entity, NBT tags, or inventory
* blocks that are used as POI
* blocks that tick

If you use such blocks, the worldgen will be slow and the loaded world may lag severely.