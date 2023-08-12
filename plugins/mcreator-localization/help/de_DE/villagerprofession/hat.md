Dieser Parameter gibt die Art des Huts an, die durch die ausgewählte Berufstextur definiert wird, die kontrolliert, ob der Dorfbewohner nach dem Anspruch auf diesen Beruf noch den Hut tragen wird, der durch seinen Typ definiert ist:
* **Keine:** In allen Fällen;
* **Teilweise:** Falls es nicht den gesamten Kopf der Dorfbewohner deckt,;
* **Voll:** In keinem Fall.

Diese Bedingung kann mit der folgenden Tabelle dargestellt werden (wo TH ein typabhängiger Hut ist und Berufshut ist):

| TH Sichtbarkeit |  TH = Keine  | TH = Teilweise |  TH = Voll   |
| --------------- |:------------:|:--------------:|:------------:|
| PH = Keine      |   Sichtbar   |    Sichtbar    |   Sichtbar   |
| PH = Teilweise  |   Sichtbar   |    Sichtbar    | Ausgeblendet |
| PH = Voll       | Ausgeblendet |  Ausgeblendet  | Ausgeblendet |