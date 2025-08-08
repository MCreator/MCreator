Itt felsorolhatja az elem további tulajdonságait, és megadhatja, hogy textúrája/modellje hogyan változik az állapotot alkotó tulajdonságok értékeinek adott kombinációja függvényében.

Egy elem tulajdonsága bármilyen számot (egész vagy törtszámot) vehet fel értéknek, ezért a részletesség követésének elkerülése és a közeli értékek megadásának lehetővé tétele érdekében egy állapot akkor felel meg, ha az elemből kivont tényleges tulajdonságértékek _megegyeznek vagy nagyobbak_ a várt (itt megadott) értékeknél.

Ha több állam is rendelkezik egyező értékekkel, akkor az utolsó egyező állam lesz használva. Ha egyik állam sem egyezik, akkor az elem az alapértelmezett megjelenését fogja használni.

Az egyéni tulajdonságok mellett a Minecraft által minden elemre meghatározott beépített elem tulajdonságokat is használhatod.

MEGJEGYZÉS: Az ismétlődő állapotok nem megengedettek. Ha két vagy több állapot csak egyetlen tulajdonság értékében különbözik egymástól, akkor az adott tulajdonság eltávolításával automatikusan eltávolításra kerülnek az első állapot ismétlődő példányai.