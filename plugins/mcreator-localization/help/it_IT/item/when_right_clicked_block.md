La procedura viene eseguita quando il giocatore fa clic con il pulsante destro del mouse su un blocco con questo oggetto in mano.

La procedura dovrebbe restituire un risultato di azione di tipo SUCCESS/CONSUME se l'elemento ha interagito con il blocco, FAIL se l'interazione non è riuscita e PASS se non c'è stata alcuna interazione.

Se la procedura non restituisce alcun valore, il tipo di azione risultante sarà PASS.

Se vuoi che la procedura "${l10n.t("elementgui.common.event_right_clicked_air")}" venga chiamata solo quando l'entità fa clic con il tasto destro del mouse in aria con questo oggetto, questa procedura dovrebbe sempre restituire SUCCESS/CONSUME.