<#assign screens = w.getGElementsOfType("gui")/>
<#assign screens += w.getGElementsOfType("overlay")/>
{
 	"sources": [
	<#list thelper.removeDuplicates(screens) as screen>
  <#list screen.getComponentsOfType("Sprite") as sprite>
  	{
  		"type": "single",
  		"resource": "screens/${sprite.sprite?remove_ending(".png")}"
  	}<#sep>,
  </#list><#sep>,
  </#list>
 	]
}