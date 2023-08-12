Die Prozedur wird ausgeführt, wenn der Spieler mit einem Rechtsklick auf einen Block mit diesem Gegenstand in seiner Hand klickt.

Das Verfahren sollte ein Aktionsergebnis des Typs SUCCESS/CONSUME zurückgeben, wenn das Element mit dem Block interagierte, FAIL wenn die Interaktion fehlgeschlagen ist, und PASS, wenn es keine Interaktion gab.

Wenn die Prozedur keinen Wert zurückgibt, wird der Ergebnis-Aktionstyp PASS sein.

Wenn Sie die "${l10n.t("elementgui.common. vent_right_clicked_air")}" Prozedur, die nur genannt wird, wenn Entität mit diesem Element mit Rechtsklicks in der Luft klickt, sollte diese Prozedur immer SUCCESS/CONSUME zurückgeben.