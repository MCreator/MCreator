{
  "type": "neoforge:add_table",
  "condition": {
    "type": "neoforge:loot_table_id",
    "loot_table_id": "${w.resolveModNamespace(data.lootTableToModify)}"
  },
  "table": "${data.getResourceLocation()}"
}