{
  "structures": [
    {
      "structure": "${modid}:${registryname}",
      "weight": 1
    }
  ],
  "placement": {
    "type": "minecraft:random_spread",
    "spacing": ${data.spacing},
    "separation": ${data.separation},
    "salt": ${thelper.randompositiveint(registryname)}
  }
}