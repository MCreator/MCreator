Ici, vous pouvez répertorier les propriétés supplémentaires de l'item et spécifier comment sa texture va changer
en fonction d'une combinaison donnée de valeurs de propriétés qui forment un état.

En plus des propriétés personnalisées, vous pouvez également utiliser les propriétés d'item intégrées:

* `damaged`: Renvoie 1.0 si l'item est endommagé et 0.0 dans le cas contraire;
* `damage`: Renvoie les dommages de l'item (entre 0.0 et 1.0);
* `lefthanded`: Renvoie 1.0 si l'item est tenu dans la main gauche d'une entité et 0.0 sinon;
* `cooldown`: Renvoie le temps de recharge restant de l'item (entre 0.0 et 1.0);
* `trim_type` (1.19.4+): Renvoie l'"ID" fractionné du type d'ornement d'armure appliqué à l'item (entre 0.0 et 1.0).

REMARQUE: Les états en double ne sont pas autorisés. Si deux états ou plus ne diffèrent que par la valeur d'une seule
propriété, la suppression de cette propriété supprimera automatiquement les doublons du premier de ces états.