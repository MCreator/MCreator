{
  "args0": [
    {
      "type": "input_value",
      "name": "entity",
      "check": "Entity"
    },
    {
      "type": "input_value",
      "name": "ticksfrozen",
      "check": "Number"
    }
  ],
  "inputsInline": true,
  "previousStatement": null,
  "nextStatement": null,
  "colour": 195,
  "mcreator": {
    "toolbox_id": "entitymanagement",
    "toolbox_init": [
      "<value name=\"entity\"><block type=\"entity_from_deps\"></block></value>",
      "<value name=\"ticksfrozen\"><block type=\"math_number\"><field name=\"NUM\">0</field></block></value>"
    ],
    "inputs": [
      "entity",
      "ticksfrozen"
    ]
  }
}