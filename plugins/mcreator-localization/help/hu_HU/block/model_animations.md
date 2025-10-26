Itt megadhatod azokat az animációkat, amiket ez a block entity le tud játszani, és a feltételeket, amikor az animációk megtörténnek.

**Győződjön meg arról, hogy az animációk csak a modellfájlban szereplő modellek részeit/csontjait hivatkozzák.
Ellenkező esetben a modell betöltése sikertelen lehet, vagy a játék összeomolhat.**

Ajánljuk az NBT adatok használatát az animációs feltételek adatainak biztosításához, mivel a feltétel csak kliens-oldali, de az animációk váltásához szükséges adatok általában nem kliens-oldaliak.

A modellfájlban vagy a modellfájl importálásakor megadott meglévő modellanimációk is
lejátszásra kerülnek az itt megadott animációk mellett.