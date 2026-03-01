{
  "individual_named_sounds": {
    "sounds": {
      <#list sounds as sound>
        "${modid}:${sound.getName()}": {
          "pitch": 1,
          "sound": "${modid}:${sound.getName()}",
          "volume": 1
        }<#sep>,
      </#list>
    }
  }
}