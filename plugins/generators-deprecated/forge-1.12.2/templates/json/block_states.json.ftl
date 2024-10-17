<#if data.rotationMode?? && (data.rotationMode == 1 || data.rotationMode == 3)>
{
  "variants": {
    "${var_variant}": {
      "model": "${modid}:${registryname}"
    },
    "facing=north": {
      "model": "${modid}:${registryname}"
    },
    "facing=east": {
      "model": "${modid}:${registryname}",
      "y": 90
    },
    "facing=south": {
      "model": "${modid}:${registryname}",
      "y": 180
    },
    "facing=west": {
      "model": "${modid}:${registryname}",
      "y": 270
    }
  }
}
<#elseif data.rotationMode?? && (data.rotationMode == 2 || data.rotationMode == 4)>
{
  "variants": {
    "${var_variant}": {
      "model": "${modid}:${registryname}"
    },
    "facing=north": {
      "model": "${modid}:${registryname}"
    },
    "facing=east": {
      "model": "${modid}:${registryname}",
      "y": 90
    },
    "facing=south": {
      "model": "${modid}:${registryname}",
      "y": 180
    },
    "facing=west": {
      "model": "${modid}:${registryname}",
      "y": 270
    },
    "facing=up": {
      "model": "${modid}:${registryname}",
      "x": 270
    },
    "facing=down": {
      "model": "${modid}:${registryname}",
      "x": 90
    }
  }
}
<#elseif data.rotationMode?? && data.rotationMode == 5>
{
  "variants": {
    "${var_variant}": {
      "model": "${modid}:${registryname}"
    },
    "facing=south": {
      "model": "${modid}:${registryname}"
    },
    "facing=north": {
      "model": "${modid}:${registryname}"
    },
    "facing=east": {
      "model": "${modid}:${registryname}",
      "x": 90
    },
    "facing=west": {
      "model": "${modid}:${registryname}",
      "x": 90
    },
    "facing=up": {
      "model": "${modid}:${registryname}",
      "x": 90,
      "y": 90
    },
    "facing=down": {
      "model": "${modid}:${registryname}",
      "x": 90,
      "y": 90
    }
  }
}
<#else>
{
  "variants": {
    "${var_variant}": {
      "model": "${modid}:${registryname}"
    }
  }
}
</#if>