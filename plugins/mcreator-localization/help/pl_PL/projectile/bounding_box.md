Ten parametr kontroluje rozmiar pola hitbox twojego pocisku w jednostkach bloków.
Pierwsza opcja ustawia szerokość i głębokość, natomiast druga opcja ustawia wysokość.

Pociski z szerokością/ głębokością lub wysokości powyżej 0,5 nie kolidują się z
ich strzelanki, aby zapobiec początkowym kolizjom ze względu na ich rozmiar. Jeśli strzelanka nie jest znana, to
zderzy się z najbliższą jednostką we wszystkich przypadkach, co oznacza strzelanie ich do np. komenda może
spowodować kolizję z nadawcą poleceń.

Bardzo duże rozmiary pocisków ograniczających rozmiar mogą również spowodować zderzenie się pocisku z blokami bardzo często i mogą uniemożliwić im pokonanie się z powodu wczesnych kolizji.