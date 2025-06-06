Válaszd ki a blokknál használandó modellt. A modell csak a vizuális megjelenést határozza meg, nem a blokk bounding boxát.

* **Normal** - Normál blokk textúrákkal minden oldalon
* **Single texture** - Blokk ugyanazzal a textúrával minden oldalon
* **Cross** - Növények által használt modell
* **Crop** - Termesztnövények által használt modell
* **Grass block** - Füves blokkok által használt modell (felső és oldalsó textúrák színezettek lesznek)
* Custom - egyedi JSON, JAVA és OBJ modelleket is definiálhatsz

Egyedi modellek készítésekor a JSON ajánlott a vanilla támogatás miatt ehhez a modell típushoz.

A JAVA modell kiválasztása kényszeríteni fogja ezt a blokkot, hogy a block entity engedélyezve legyen. A JAVA modellek sokkal több erőforrást igényelnek, szóval ügyelj arra, hogy ne használd őket gyakran megjelenő blokkoknál, mint például a világgenerálási blokkok.