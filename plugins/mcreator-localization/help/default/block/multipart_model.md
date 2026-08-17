If enabled, the block state definitions of this block are exported using the multipart
format instead of the variants format.

In the multipart format, each block state entry defines an independent part of the block model.
All parts whose conditions match the current block state are rendered together.

Unlike with the variants format, part conditions can use any subset of the block state
properties, and a part with an empty condition is always rendered.

If block rotation is enabled, MCreator will automatically rotate all parts together with the block.
