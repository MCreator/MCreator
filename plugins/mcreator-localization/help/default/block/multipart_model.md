Here you can select the format used to export the block state definitions of this block:
variants (the default) or multipart.

In the multipart format, each block state entry defines an independent part of the block model.
All parts whose conditions match the current block state are rendered together.

Unlike with the variants format, part conditions can use any subset of the block state
properties, and a part with an empty condition is always rendered. Multiple parts can also
use the same condition to render multiple models at once.

The default block model of this block is not rendered in the world when the multipart
format is used; add a part with an empty condition if some geometry should always render.
The default model is still used for the block item and block previews.

If parts define custom bounding boxes, the shape of the block is the union of the bounding
boxes of all parts whose condition matches the block state.

If block rotation is enabled, MCreator will automatically rotate all parts together with the block.
