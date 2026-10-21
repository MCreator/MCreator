{
  "format_version": "1.21.40"
  <#list beblocks as block>
    ,"${modid}:${block.getModElement().getRegistryName()}": {
      "sound": "${block.soundOnStep}"
    }
  </#list>

}