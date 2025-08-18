{
  "parent": "item/generated",
  "textures": {
    <#if data.spawnEggTexture?has_content>
	"layer0": "${data.spawnEggTexture.format("%s:item/%s")}"
    <#else>
	"layer0": "${modid}:item/${registryname}_spawn_egg_generated"
    </#if>
  }
}