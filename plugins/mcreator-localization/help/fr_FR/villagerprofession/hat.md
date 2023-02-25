Ce paramètre spécifie le type de chapeau défini par la texture de métier sélectionnée, qui contrôle
si le villageois portera toujours le chapeau défini par son type après avoir revendiqué cette profession :
* **AUCUN :** Dans tous les cas ;
* **PARTIEL :** Dans les cas où il ne couvre pas toute la tête du villageois ;
* **COMPLET :** En aucun cas.

Cette condition peut être représentée par le tableau suivant (où TH est le chapeau dépendant du type et PH est le chapeau de la profession) :

| Visibilité TH | TH = AUCUN | TH = PARTIEL | TH = PLEIN |
|---------------|:-----------:|:-----------:|:--------- :|
| PH = AUCUN    |   Visible   |   Visible   |   Visible  |
| PH = PARTIEL  |   Visible   |   Visible   |    Caché   |
| PH = PLEIN    |    Caché    |    Caché    |    Caché   |