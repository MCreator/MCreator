Cette procédure est exécutée quand un joueur fait un clic droit sur un bloc avec cet item dans sa main. Cette procédure devrait retourner une résultante d'action de type SUCCESS/CONSUME si l'item a interagi avec le bloc, FAIL si l'interaction n'a pas marché, et PASS si il n'y a pas eu d'interaction.

Si la procédure ne retourne aucune valeur, le type de la résultante d'action sera PASS.

Si vous voulez que la procédure "${l10n.t("elementgui.common.event_right_clicked_air")}" ne soit appelée que quand une entité fait un clic droit dans l'air avec cet item, cette procédure devrait toujours retourner SUCCESS/CONSUME

Si vous voulez le "${l10n.t("elementgui.common.event_right_clicked_air")}" doit être appelée seulement lorsque l'entité clique droit dans l'air avec cet objet, cette procédure devrait toujours renvoyer SUCCESS/CONSUME.