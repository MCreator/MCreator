Dieser Parameter steuert die Größe der Hitbox deines Projektils in Blockeinheiten.
Die erste Option legt die Breite und Tiefe fest, während die zweite Option die Höhe festlegt.

Projektile mit Breite/Tiefe oder Höhe von mehr als 0,5 werden nicht mit dem Schützen
kollidieren, um erste Kollisionen aufgrund ihrer Größe zu verhindern. Wenn der Schütze nicht bekannt ist, werden sie
in allen Fällen mit der nächstgelegenen Entität kollidieren, was bedeutet, sie mit dem Beispiel-Befehl zu schießen kann
dazu führen, dass sie mit dem Befehlsabsender kollidieren.

Sehr große Begrenzungsboxen für Projektile können dazu führen, dass diese sehr häufig mit den Blöcken kollidieren.
Dies kann ihre Flugbahn aufgrund frühzeitiger Kollisionen beeinträchtigen.