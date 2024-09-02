{
  "comparator_output": ${data.analogOutput},
  "description": {
    "translate": "item.${modid}.${registryname}.desc"
  },
  "length_in_seconds": ${[data.lengthInTicks, 1]?max / 20.0},
  "sound_event": "${data.music}"
}