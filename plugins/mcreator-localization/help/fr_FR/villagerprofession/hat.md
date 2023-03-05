Ce paramètre spécifie le type de chapeau défini par la texture de métier sélectionnée, qui contrôle
si le villageois portera toujours le chapeau défini par son type après avoir revendiqué cette profession:
* **None:** Dans tous les cas;
* **Partial:** Dans les cas où il ne couvre pas toute la tête du villageois;
* **Full:** En aucun cas.

Cette condition peut être représentée par le tableau suivant (où TH est le chapeau dépendant du type et PH est le chapeau de la profession):

| Visibilité TH | TH = None | TH = Partial | TH = Full |
|---------------|:---------:|:------------:|:---------:|
| PH = None     |  Visible  |   Visible    |  Visible  |
| PH = Partial  |  Visible  |   Visible    |   Caché   |
| PH = Full     |   Caché   |    Caché     |   Caché   |