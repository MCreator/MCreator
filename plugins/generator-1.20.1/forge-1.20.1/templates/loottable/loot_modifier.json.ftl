{
  "type": "${modid}:add_table",
  "conditions": [
    {
      "condition": "forge:loot_table_id",
      "loot_table_id": "${data.lootTableModified}"
    }
  ],
  "table": "${data.getResourceLocation()}"
}