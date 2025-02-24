Dieser Parameter legt fest, wie schnell Ihr Werkzeug verwendet werden kann.

Dieses Attribut steuert die Länge der Abklingzeit, mit der Zeit T = 1 / Angriffsgeschwindigkeit * 20Ticks.

Der Schadenmultiplikator ist dann 0,2 + (t + 0,5) / T ^ 2 * 0,8, auf den Bereich 0 beschränkt. – 1, wobei t die Nummer der Ticks seit dem letzten Angriff oder dem Gegenstandswechsel ist.