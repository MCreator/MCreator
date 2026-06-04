Select the model to be used with this block. Model only defines visual look and not the
bounding box of the block.

* **Normal** - Normal block with textures on each side
* Cross - Block with textures in an X shape like flowers.
  * If you used this model, it is recommended to use either `alpha_test_single_sided`, `blend` or `opaque` as the rendering method to avoid texture flickering.
* Single texture - Normal block with the same texture on each side
* Custom - you can define custom Bedrock (`.geo.json` files) models too. Your block is limited to 30×30×30 pixels in size.