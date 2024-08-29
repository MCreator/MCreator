{
  "type": "neoforge:add_table",
  "conditions": [
    {
      "condition": "neoforge:loot_table_id",
      "loot_table_id": "${data.lootTableModified}"
    }
  ],
  "table": "${data.getResourceLocation()}"
}