<#if data.spawnEggTexture?has_content>
{
  "parent": "item/generated",
  "textures": {
    "layer0": "${data.spawnEggTexture.format("%s:item/%s")}"
  }
}
<#else>
{
    "parent": "item/template_spawn_egg"
}
</#if>