Cette option permet de déterminer sur quel(s) type(s) de serveur la commande peut être utilisée.
* **Both** : La commande peut être utilisée partout. C'est le comportement normal (exemple `/give`.
* **Multi players only** : La commande sera disponible uniquement sur les serveurs multi-joueurs (exemple, `/ban`)
* **Single player only** : La commande sera disponible uniquement sur les mondes solo (exemple, `/publish`)

Même si l'option est définie à `Single player only`, cela ne veut pas dire qu'elle est une commande côté client uniquement.
La commande sera quand même enregistré sur le server comme normalement.

Si la commande est marquée comme côté client uniquement, cette option sera désactivée.