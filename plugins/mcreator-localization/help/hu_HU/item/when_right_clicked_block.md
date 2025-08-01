Az eljárás akkor hajtódik végre, amikor a játékos jobb gombbal rákattint egy blokkra, miközben ez az elem a kezében van.

Az eljárásnak SUCCESS/CONSUME típusú műveleti eredményt kell visszaadnia, ha az elem kölcsönhatásba lépett a blokkkal, FAIL-t, ha a kölcsönhatás sikertelen volt, és PASS-t, ha nem történt kölcsönhatás.

Ha az eljárás nem ad vissza értéket, az eredmény művelet típusa PASS lesz.

Ha azt szeretné, hogy a "${l10n.t("elementgui.common.event_right_clicked_air")}" eljárás csak akkor legyen hívva, amikor az entitás jobb gombbal kattint a levegőbe ezzel az elemmel, akkor ennek az eljárásnak mindig SUCCESS/CONSUME értéket kell visszaadnia.