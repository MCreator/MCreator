This parameter determines what jigsaw pool might be used by the current one as a fallback
in cases this one can not be generated in the specified position (not enough space or depth limit reached).

If set to `${modid}:${registryname}_<pool_name>`, the game will choose jigsaw parts from pool `<pool_name>` in this list.

Same pool may be used as well, but if it overlaps with other jigsaw parts of the same structure, it will be ignored.