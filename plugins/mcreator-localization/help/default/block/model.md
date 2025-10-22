Select the model to be used with this block. Model only defines visual look and not the
bounding box of the block.

* **Normal** - Normal block with textures on each side
* **Single texture** - Block with same texture on all sides
* **Cross** - Model used by plants
* **Crop** - Model used by crop plants
* **Grass block** - Model used by grass blocks (top and side textures will be tinted)
* Custom - you can define custom JSON, JAVA and OBJ models too

When making custom models, JSON is recommended due to vanilla support for this model type.

Selecting JAVA model will force this block to have block entity enabled.
JAVA models also will also be much more resource heavy, so make sure to not use them in blocks
that commonly appear, such as world gen blocks.