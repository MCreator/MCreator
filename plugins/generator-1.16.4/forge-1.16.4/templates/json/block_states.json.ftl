<#if data.rotationMode?? && (data.rotationMode == 1 || data.rotationMode == 3)>
{
  "variants": {
    "facing=north": {
      "model": "${modid}:block/${registryname}"
    },
    "facing=east": {
      "model": "${modid}:block/${registryname}",
      "y": 90
    },
    "facing=south": {
      "model": "${modid}:block/${registryname}",
      "y": 180
    },
    "facing=west": {
      "model": "${modid}:block/${registryname}",
      "y": 270
    }
  }
}
<#elseif data.rotationMode?? && (data.rotationMode == 2 || data.rotationMode == 4)>
{
  "variants": {
    "facing=north": {
      "model": "${modid}:block/${registryname}"
    },
    "facing=east": {
      "model": "${modid}:block/${registryname}",
      "y": 90
    },
    "facing=south": {
      "model": "${modid}:block/${registryname}",
      "y": 180
    },
    "facing=west": {
      "model": "${modid}:block/${registryname}",
      "y": 270
    },
    "facing=up": {
      "model": "${modid}:block/${registryname}",
      "x": 270
    },
    "facing=down": {
      "model": "${modid}:block/${registryname}",
      "x": 90
    }
  }
}
<#elseif data.rotationMode?? && data.rotationMode == 5>
{
  "variants": {
    "facing=south": {
      "model": "${modid}:block/${registryname}"
    },
    "facing=north": {
      "model": "${modid}:block/${registryname}"
    },
    "facing=east": {
      "model": "${modid}:block/${registryname}",
      "x": 90
    },
    "facing=west": {
      "model": "${modid}:block/${registryname}",
      "x": 90
    },
    "facing=up": {
      "model": "${modid}:block/${registryname}",
      "x": 90,
      "y": 90
    },
    "facing=down": {
      "model": "${modid}:block/${registryname}",
      "x": 90,
      "y": 90
    }
  }
}
<#else>
{
  "variants": {
    "${var_variant}": {
      "model": "${modid}:block/${registryname}"
    }
  }
}
</#if>