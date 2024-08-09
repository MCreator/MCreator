<#if data.rotationMode?? && (data.rotationMode == 1 || data.rotationMode == 3)>
<#if data.enablePitch>
{
  "variants": {
    "face=floor,facing=north": {
      "model": "${modid}:block/${registryname}"
    },
    "face=floor,facing=east": {
      "model": "${modid}:block/${registryname}",
      "y": 90
    },
    "face=floor,facing=south": {
      "model": "${modid}:block/${registryname}",
      "y": 180
    },
    "face=floor,facing=west": {
      "model": "${modid}:block/${registryname}",
      "y": 270
    },
    "face=wall,facing=north": {
      "model": "${modid}:block/${registryname}",
      "x": 90
    },
    "face=wall,facing=east": {
      "model": "${modid}:block/${registryname}",
      "x": 90,
      "y": 90
    },
    "face=wall,facing=south": {
      "model": "${modid}:block/${registryname}",
      "x": 90,
      "y": 180
    },
    "face=wall,facing=west": {
      "model": "${modid}:block/${registryname}",
      "x": 90,
      "y": 270
    },
    "face=ceiling,facing=north": {
      "model": "${modid}:block/${registryname}",
      "x": 180,
      "y": 180
    },
    "face=ceiling,facing=east": {
      "model": "${modid}:block/${registryname}",
      "x": 180,
      "y": 270
    },
    "face=ceiling,facing=south": {
      "model": "${modid}:block/${registryname}",
      "x": 180
    },
    "face=ceiling,facing=west": {
      "model": "${modid}:block/${registryname}",
      "x": 180,
      "y": 90
    }
  }
}
<#else>
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
</#if>
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
    "axis=x": {
      "model": "${modid}:block/${registryname}",
      "x": 90,
      "y": 90
    },
    "axis=y": {
      "model": "${modid}:block/${registryname}"
    },
    "axis=z": {
      "model": "${modid}:block/${registryname}",
      "x": 90
    }
  }
}
<#else>
{
  "variants": {
    "": {
      "model": "${modid}:block/${registryname}"
    }
  }
}
</#if>