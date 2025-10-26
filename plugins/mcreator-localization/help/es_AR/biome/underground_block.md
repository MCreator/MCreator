Este parámetro controla el bloque subterráneo debajo de la capa del bloque superficial.

Generalmente, (en vanilla o custom) se utiliza la tierra aquí.

Este bloque debe estar etiquetado en `minecraft:dirt` para que plantas y árboles aparezcan correctamente en él.

Evitar el uso de bloques complejos como:

* bloques transparentes
* bloques que no son cubos completos
* bloques con tile entity, etiquetas NBT o inventario
* bloques que se utilizan como POI
* bloques que dependen de ticks aleatorios

Si usas estos bloques, el mundo irá lento y la carga del mundo puede retrasarse severamente.