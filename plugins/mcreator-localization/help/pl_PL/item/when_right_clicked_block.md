Procedura jest wykonywana, gdy gracz kliknie prawym przyciskiem myszy na blok w dłoni.

Procedura powinna zwracać wynik działania typu SUCCESS/CONSUME, jeżeli przedmiot wchodził w interakcję z blokiem, nie powiodło się, jeśli interakcja nie powiodła się i PASS, jeśli nie wystąpiła interakcja.

Jeśli procedura nie zwraca żadnej wartości, typem akcji wyniku będzie PRZEJŚCIE.

Jeśli chcesz "${l10n.t("elementgui.common. Procedura vent_right_clicked_air")}" nazywana jest tylko gdy podmiot kliknie prawym przyciskiem myszy na powietrze z tą pozycją, procedura ta powinna zawsze zwracać SUCCESS/CONSUME.