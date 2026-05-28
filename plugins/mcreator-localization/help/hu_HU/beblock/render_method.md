A renderelési módszerek szabályozzák, hogyan jelenik meg egy blokk a világban.
Befolyásolják az átlátszóságot, a háttérből való láthatóságot, valamint a blokkok viselkedését nagy távolságból.

---

- Átlátszóság = teljesen láthatatlan képpontok (kivágások).
- Átlátszóság = félig átlátszó képpontok.
- Hátsó felületek kiszűrése = a felületek hátulról nézve láthatatlanok.
- Távoli elvágás = az arcok a renderelési távolság felénél eltűnnek.

**alpha_test**  
Átlátszóság: igen  
Átlátszóság: nem  
Háttérfelületek kiszűrése: nem  
Távoli objektumok kiszűrése: igen  
Példák: Létra, Szörnyteremtő, Indák

**alpha_test_single_sided**  
Átlátszóság: igen  
Átlátszóság: nem  
Háttérfelületek kiszűrése: igen  
Távoli objektumok kiszűrése: igen  
Példák: ajtók, csemeték, csapóajtók

**blend**  
Átlátszóság: igen  
Átlátszóság: igen  
Háttérfelületek kiszűrése: igen  
Távoli objektumok kiszűrése: nem  
Példák: Üveg, Jeladó, Mézblokk

**double_sided**  
Átlátszóság: nem  
Átlátszóság: nem  
Háttérfelületek kiszűrése: nem  
Távoli felületek kiszűrése: nem  
Példák: Powder Snow

**átlátszatlan (alapértelmezett)**  
Átlátszóság: nem  
Átlátszóság: nem  
Háttérfelületek kiszűrése: igen  
Távoli felületek kiszűrése: nem  
Példák: föld, kő, beton

---

**Távolságalapú renderelési módszerek**

- Közel = a renderelési távolság felének elérése előtt használatos
- Far = a renderelési távolság felének elérése után használatos

**alpha_test_to_opaque**  
Közel: alpha_test  
Távol: opaque  
Példák: Levelek

**alpha_test_single_sided_to_opaque**  
Közel: alpha_test_single_sided  
Távol: opaque  
Példák: tengeri moszat, cukornád

**blend_to_opaque**  
Közel: átmenet  
Távol: átlátszatlan