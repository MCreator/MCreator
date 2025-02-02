{
  "comparator_output": ${data.musicDiscAnalogOutput},
  "description": {
    "translate": "item.${modid}.${registryname}.desc"
  },
  "length_in_seconds": ${[data.musicDiscLengthInTicks, 1]?max / 20.0},
  "sound_event": "${data.musicDiscMusic}"
}