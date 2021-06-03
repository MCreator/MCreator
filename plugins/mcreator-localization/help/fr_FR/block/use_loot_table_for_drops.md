Quand activé, le bloc n'aura pas de drop définis dans son code (dans l'élément de mod), mais dans une table de butin.

Créez une table de butin avec le nom de registre `blocks/<this block registry name>`, avec le namespace _mod_, et le type _Block_.

Si ce paramètre n'est pas coché, la table de butin passera en premier sur les drops, mais ces derniers seront toujours définis dans l'élément de mod et seront utilisés si la table de butin ne retourne rien.

Quand ce paramètre est coché, les drops du bloc sont entièrement contrôlés par la table de butin.