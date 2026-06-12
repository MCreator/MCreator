Render-Methoden bestimmen, wie ein Block in der Welt aussieht.
Dies betrifft die Transparenz, die Sichtbarkeit von hinten und wie sich Blöcke auf längeren Entfernungen verhalten.

---

- Transparenz = vollständig unsichtbare Pixel (ausgeschnitten).
- Durchscheinend = halb-transparente Pixel.
- Rückseitige Ausmerzung = Flächen sind unsichtbar, wenn von hinten betrachtet.
- Distanzvernichtung = Flächen verschwinden nach der Hälfte der Render-Distanz.

**alpha_test**  
Transparenz: Ja  
Transluzenz: Nein  
Rückseiten-Keulung: Nein  
Entfernte Keulung: Ja  
Beispiele: Lader, Monster Spawner, Ranken

**alpha_test_single_sided**  
Transparenz: Ja  
Transluzenz: Nein  
Rückseiten-Keulung: Ja  
Entfernte Keulung: Ja  
Beispiele: Lader, Monster Spawner, Ranken

**blend**  
Transparenz: Ja  
Transluzenz: Ja  
Rückseiten-Keulung: Ja  
Entfernte Keulung: Nein  
Beispiele: Glas, Leuchtturm, Honigblock

**double_sided**  
Transparenz: Nein  
Transluzenz: Nein  
Rückseiten-Keulung: Nein  
Entfernte Keulung: Nein  
Beispiele: Pulverschnee

**undurchsichtig (Standard)**  
Transparenz: Nein  
Transluzenz: Nein  
Rückseiten-Keulung: ja  
entfernte Keulung: keine  
Beispiele: Erde, Stein, Beton

---

**Entfernungsbasierte Rendering-Methoden**

- In der Nähe = verwendet vor Erreichen der Hälfte der Renderdistanz
- Fern = Wird nach Erreichen der Hälfte der Renderdistanz verwendet

**alpha_test_to_opaque**  
Nähe: alpha_test  
Ferne: undurchsichtig  
Beispiele: Blätter

**alpha_test_single_sided_to_opaque**  
Nähe: alpha_test_single_sided  
Ferne: undurchsichtig  
Beispiele: Kelp, Zuckerrohr

**blend_to_opaque**  
Nähe: Blend  
Ferne: undurchsichtig