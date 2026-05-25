Les méthodes de rendu contrôlent la façon dont un bloc est affiché dans le monde.
Elles influencent la transparence, la visibilité depuis l'arrière et le comportement des blocs à de longues distances.

---

- Transparence = pixels totalement invisibles (découpés).
- Translucidité = pixels semi-transparents.
- Masquage des faces arrière = les faces deviennent invisibles lorsqu'elles sont vues de derrière.
- Masquage à distance = les faces disparaissent après la moitié de la distance de rendu.

**alpha_test**  
Transparence : oui  
Translucidité : non  
Masquage des faces arrière : non  
Masquage à distance : oui  
Exemples : Échelle, Générateur de créatures, Lianes

**alpha_test_single_sided**  
Transparence : oui  
Translucidité : non  
Masquage des faces arrière : oui  
Masquage à distance : oui  
Exemples : Portes, Pousses, Trappes

**blend**  
Transparence : oui  
Translucidité : oui  
Masquage des faces arrière : oui  
Masquage à distance : non  
Exemples : Verre, Balise, Bloc de miel

**double_sided**  
Transparence : non  
Translucidité : non  
Masquage des faces arrière : non  
Masquage à distance : non  
Exemples : Neige poudreuse

**opaque (par défaut)**  
Transparence : non  
Translucidité : non  
Masquage des faces arrière : oui  
Masquage à distance : non  
Exemples : Terre, Roche, Béton

---

**Méthodes de rendu basées sur la distance**

- Proche = utilisé avant d'atteindre la moitié de la distance de rendu
- Lointain = utilisé après avoir atteint la moitié de la distance de rendu

**alpha_test_to_opaque**  
Proche : alpha_test  
Lointain : opaque  
Exemples : Feuillage

**alpha_test_single_sided_to_opaque**  
Proche : alpha_test_single_sided  
Lointain : opaque  
Exemples : Algue, Canne à sucre

**blend_to_opaque**  
Proche : blend  
Lointain : opaque