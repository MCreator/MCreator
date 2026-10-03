{
  <#list sounds?filter(e -> e.getBECategory() == "music") as sound>
  	"${modid}:${sound.getName()}": {
	  "event_name": "${modid}:${sound.getName()}",
	  "min_delay": 60,
	  "max_delay": 180
  	}<#sep>,
  </#list>
}