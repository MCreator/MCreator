<#assign screens = w.getGElementsOfType("gui")/>
<#assign screens += w.getGElementsOfType("overlay")/>
<#assign sprites = []/>
<#list screens?filter(screen -> screen.getComponentsOfType("Sprite")?size != 0) as screen>
	<#list screen.getComponentsOfType("Sprite") as sprite>
		<#assign sprites += [sprite]/>
	</#list>
</#list>
{
 	"sources": [
	<#list thelper.removeDuplicates(sprites?map(component -> component.sprite)) as sprite>
  	{
  		"type": "single",
  		"resource": "${modid}:screens/${sprite?remove_ending(".png")}"
  	}<#sep>,
  </#list>
 	]
}