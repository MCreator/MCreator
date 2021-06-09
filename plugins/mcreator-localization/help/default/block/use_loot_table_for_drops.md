When enabled, block will not define drops in code (drops defined in block mod element), 
instead, block drops need to be defined with a loot table.

Create loot table mod element with registry name `blocks/<this block registry name>`, namespace _mod_, and type _Block_.

If this parameter is not checked, loot tables will still override block drops, but block drop defined in block mod element 
will be used when loot table will not return any entry.

When this parameter is check, drops of this block are entirelly controlled by the loot tables.