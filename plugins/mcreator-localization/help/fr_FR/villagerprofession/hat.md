Ce paramètre spécifie le type de chapeau défini par la texture de métier sélectionnée, qui contrôle
si le villageois portera toujours le chapeau défini par son type après avoir revendiqué cette profession:
* **NONE:** Dans tous les cas;
* **PARTIAL:** Dans les cas où il ne couvre pas toute la tête du villageois;
* **FULL:** En aucun cas.

Cette condition peut être représentée par le tableau suivant (où TH est le chapeau dépendant du type et PH est le chapeau de la profession):

| Visibilité TH | TH = NONE | TH = PARTIAL | TH = FULL |
|---------------|:---------:|:------------:|:---------:|
| PH = NONE     |  Visible  |   Visible    |  Visible  |
| PH = PARTIAL  |  Visible  |   Visible    |   Caché   |
| PH = FULL     |   Caché   |    Caché     |   Caché   |