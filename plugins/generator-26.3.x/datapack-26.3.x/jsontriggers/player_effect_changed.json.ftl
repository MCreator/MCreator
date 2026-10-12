"${registryname}_${cbi}": {
  "trigger": "minecraft:effects_changed",
  "conditions": {
  	"effects": {
    <#list input_list$effect as effect>
    	${effect}<#sep>,
    </#list>
  	}
  }
},