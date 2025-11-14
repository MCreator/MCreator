Ce paramètre contrôle la taille de la boîte de collision de votre projectile en unités de blocs.
La première option définit la largeur et la profondeur, tandis que la seconde option définit la hauteur.

Les projectiles de largeur/profondeur ou de hauteur supérieure à 0,5 ne entrent pas en collision avec
leur tireur pour éviter les collisions initiales en raison de leur taille. Si le tireur n'est pas connu, ils
entreront en collision avec l'entité la plus proche dans tous les cas, ce qui signifie que la tirer par exemple avec une commande pourrait
les faire entrer en collision avec l'expéditeur de la commande.

Les très grandes tailles de boîte de collision de projectile peuvent également faire entrer le projectile en collision avec les blocs
très souvent et peuvent les empêcher d'aller loin en raison de collisions précoce.