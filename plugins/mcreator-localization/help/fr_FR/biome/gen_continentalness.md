La continentalité contrôle la distance du biome avec les côtes.
Des valeurs plus petites signifient que les biomes sont plus proches de la côte
et des valeurs plus élevées signifient que les biomes seront plus loin de la côte
avec des hauteurs plus hautes (ex. les montagnes).

Les biomes avec une petite valeur de continentalité se généreront plus proches
les uns des autres et compétitionneront pour le même emplacement de génération dans le monde.
Trop de valeurs similaires résultera en quelques biomes ne se générant pas.

Bien que les valeurs entre -2 et 2 sont valides, les biomes vanilla utilisent uniquement
des valeurs dans un intervalle de -1 à 1.

Les biomes vanilla de l'Overworld utilisent ces intervalles de valeurs :

* Océan profond : -1.05 to -0.455
* Océan: -0.455 to -0.19
* Côte: -0.19 to -0.11
* Intérieur des terres : -0.11 to 0.55
* Près de l'intérieur des terres : -0.11 to 0.03
* Au milieu des terres : 0.03 to 0.3
* Loin à l'intérieur des terres : 0.3 to 1.0