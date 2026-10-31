Hier kannst du das Format für den Export der Blockzustandsdefinitionen dieses Blocks auswählen:
Varianten (der Standard) oder mehrteilig.

Im mehrteiligen Format definiert jeder Blockzustandseintrag einen eigenständigen Teil des Blockmodells.
Alle Teile, deren Bedingungen mit dem aktuellen Blockzustand übereinstimmen, werden zusammen dargestellt.

Im Gegensatz zum Varianten-Format können Modellteilbedingungen eine beliebige Teilmenge der Blockzustandseigenschaften verwenden und ein Teil mit einem leeren Zustand wird immer gerendert. Mehrere Teile können auch mit der gleichen Bedingung mehrere Modelle gleichzeitig rendern.

Das Standard-Blockmodell dieses Blocks wird nicht in der Welt gerendert, wenn das
mehrteilige Format verwendet wird; fügen Sie ein Teil mit einer leeren Bedingung hinzu, wenn eine Geometrie immer gerendert werden soll.
Das Standardmodell wird weiterhin für Blockgegenstands- und Blockvorschau verwendet. Teile verwenden immer
die Standard-Partikeltextur des Blocks; Partikeltexturen einzelner Zustandseinträge
gelten nur für das Variantenformat.

Wenn Teile benutzerdefinierte Begrenzungsboxen definieren, ist die Form des Blocks die Vereinigung der Begrenzungsboxen aller Teile, deren Bedingung dem Blockzustand entspricht. In diesem Fall wird die Standard-
Begrenzungsbox des Blocks nicht verwendet und Blockzustände übereinstimmend mit keinem solchen Teil haben eine
leere Form; Fügen Sie ein Teil mit einer leeren Bedingung hinzu, wenn der Block immer eine Grundform haben soll.

Wenn die Blockrotation aktiviert ist, dreht MCreator automatisch alle Teile zusammen mit dem Block.
