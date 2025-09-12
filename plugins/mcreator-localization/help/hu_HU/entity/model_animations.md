Itt megadhatja, hogy ez az entitás milyen animációkat tud lejátszani, és milyen feltételek mellett történik az animáció.

**Győződjön meg arról, hogy az animációk csak a modellfájlban szereplő modellek részeit/csontjait hivatkozzák.
Ellenkező esetben a modell betöltése sikertelen lehet, vagy a játék összeomolhat.**

Javasoljuk a szinkronizált adatok használatát az animációs feltételekhez szükséges adatok biztosításához, mivel a feltétel csak a kliens oldalon található,
de az animációk váltásához szükséges adatok általában nem a kliens oldalon találhatók.

A modellfájlban vagy a modellfájl importálásakor megadott meglévő modellanimációk is
lejátszásra kerülnek az itt megadott animációk mellett.