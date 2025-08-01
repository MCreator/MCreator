Ez a trigger akkor indítja el az eljárást, amikor az entitás megsérül.

A `sourceentity` függőség ebben az esetben az a entitás, amely kárt okoz ennek az entitásnak, és null lehet, ha a kárt nem entitás forrás okozza.

Ha az eljárás `false` logikai értéket ad vissza, akkor az entitás nem szenved kárt.
