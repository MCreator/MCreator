Bounding boxes define the collision shape and the selection outline of the block. Each entry is one box
given in 1/16 of a block, measured from the bottom north-west corner of the block.

Bedrock Edition limits:

* Boxes must stay inside the block footprint: X and Z from 0 to 16, Y from 0 to 24
* Only one selection outline is supported, so the outline covers all boxes together and is at most 16 high
* Boxes cannot be subtracted from each other

When the block uses rotation, boxes are rotated together with the block model.