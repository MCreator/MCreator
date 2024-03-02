Ez a paraméter szabályozza, hogy az eszköz milyen gyorsan használható.

Ez az attribútum szabályozza a lehűlési idő hosszát, az eltelt idő T = 1 / attackSpeed * 20ticks.

A sebzés szorzója ekkor 0,2 + ((t + 0,5) / T) ^ 2 * 0,8, a 0,2 - 1 tartományra korlátozva, ahol t a következő szám a legutóbbi támadás vagy tárgyváltás óta eltelt tikkek száma.