Continentalness controls how far the biome is from the coast.
Smaller values mean biomes closer to the coast and higher values mean biomes
further from the coast and with higher height (e.g. mountains).

Biomes with similar continentalness will generate closer together
and will compete for the same spot in the world when generating.
Too similar values will result in some biomes not generating.

While values from -2 to 2 are valid, vanilla biomes only use values in range
from -1 to 1.

Overworld vanilla biomes use these value ranges:

* deep ocean: -1.05 to -0.455
* ocean: -0.455 to -0.19
* coast: -0.19 to -0.11
* inland: -0.11 to 0.55
* near inland: -0.11 to 0.03
* mid inland: 0.03 to 0.3
* far inland: 0.3 to 1.0