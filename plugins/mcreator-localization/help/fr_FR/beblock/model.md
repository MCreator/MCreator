Sélectionnez le modèle à utiliser avec ce bloc. Le modèle définit uniquement l'aspect visuel et non le
boîte englobante du bloc.

- **Normal** - Bloc normal avec des textures sur chaque face
- Croix - Bloc avec les textures dans une forme de X comme les fleurs
  - Si vous avez utilisé ce modèle, il est recommandé d'utiliser soit `alpha_test_single_sided`, `blend` ou `opaque` comme méthode de rendu pour éviter le scintillement de textures.
- Texture unique - Bloc avec la même texture sur tous les côtés
- Personnalisé - vous pouvez également définir des modèles Bedrock personnalisés (fichiers `.geo.json`). Votre bloc est limité à une taille de 30 × 30 × 30 pixels.