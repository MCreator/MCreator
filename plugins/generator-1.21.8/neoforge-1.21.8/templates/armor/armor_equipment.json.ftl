{
  "layers": {
  	<#if !(data.isHorseArmor!false)>
    	"humanoid": [
      	{
        	"texture": "${modid}:${registryname}"
      	}
    	],
    	"humanoid_leggings": [
      	{
        	"texture": "${modid}:${registryname}"
      	}
    	]
    <#else>
    	"horse_body": [
    		{
    			"texture": "${modid}:${registryname}",
    			"use_player_texture": true
    		}
    	]
    </#if>
  }
}