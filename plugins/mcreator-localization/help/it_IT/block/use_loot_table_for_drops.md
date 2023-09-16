Se abilitato, il blocco non definirà i drop dal codice (i drop definiti nell'elemento mod del blocco), invece, i drop devono essere definite con una loot table.

Crea un elemento mod loot table con il nome del registro `blocks/${registryname}`, namespace _mod_e tipo _Block_.

Se questo parametro non è selezionato, le tabelle del bottino sovrascriveranno ancora i drop del blocco, ma i drop del blocco definiti nell'elemento mod del blocco verranno utilizzata quando la loot table non restituirà alcuna voce.

Quando questo parametro è controllato, i drop di questo blocco sono completamente controllati dalle loot table.