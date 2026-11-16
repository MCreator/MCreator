{
  "values": {
    <#list flammables as block>
    "${modid}:${block.getModElement().getRegistryName()}": {
      "burn_odds": ${block.flammability},
      "ignite_odds": ${block.fireSpreadSpeed}
    }<#if (block.getModElement().getTypeString() == "block") && block.isSign()>,
    "${modid}:${block.getWallRegistryName()}": {
      "burn_odds": ${block.flammability},
      "ignite_odds": ${block.fireSpreadSpeed}
    }</#if><#sep>,</#list>
  }
}
