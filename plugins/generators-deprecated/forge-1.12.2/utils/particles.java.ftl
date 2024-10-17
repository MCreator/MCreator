<#include "tokens.ftl">

<#macro particles type particleObj radious amount condition>
    <#local conditionProcessed = (condition == "ALWAYS")?then("true", translateGlobalVarName(condition.replace("VAR:", "")))>
    <#if type=="Spread" >
        <@particlesSpread particleObj radious amount conditionProcessed/>
    <#elseif type=="Top" >
        <@particlesTop particleObj radious amount conditionProcessed/>
    <#elseif type=="Tube" >
        <@particlesTube particleObj radious amount conditionProcessed/>
    <#elseif type=="Plane" >
        <@particlesPlane particleObj radious amount conditionProcessed/>
    </#if>
</#macro>

<#macro particlesPlane particleObj radious amount condition>
if(${condition})
	for(int l=0;l< ${amount}; ++l) {
		double d0 = (i + 0.5) + (random.nextFloat() - 0.5) * ${radious}D * 20;
		double d1 = ((j + 0.7) + (random.nextFloat() - 0.5) * ${radious}D)+0.5;
		double d2 = (k + 0.5) + (random.nextFloat() - 0.5) * ${radious}D * 20;
		world.spawnParticle(EnumParticleTypes.${particleObj.toString()}, d0, d1, d2, 0, 0, 0);
	}
</#macro>

<#macro particlesSpread particleObj radious amount condition>
if(${condition})
	for (int l = 0; l < ${amount}; ++l) {
	    double d0 = (i + random.nextFloat());
	    double d1 = (j + random.nextFloat());
	    double d2 = (k + random.nextFloat());
	    int i1 = random.nextInt(2) * 2 - 1;
	    double d3 = (random.nextFloat() - 0.5D) * ${radious}D;
	    double d4 = (random.nextFloat() - 0.5D) * ${radious}D;
	    double d5 = (random.nextFloat() - 0.5D) * ${radious}D;
	    world.spawnParticle(EnumParticleTypes.${particleObj.toString()}, d0, d1, d2, d3, d4, d5);
	}
</#macro>

<#macro particlesTop particleObj radious amount condition>
if(${condition})
    for (int l = 0; l < ${amount}; ++l) {
		double d0 = (double)((float)i + 0.5) + (double)(random.nextFloat() - 0.5) * ${radious}D;
		double d1 = ((double)((float)j + 0.7) + (double)(random.nextFloat() - 0.5) * ${radious}D)+0.5;
		double d2 = (double)((float)k + 0.5) + (double)(random.nextFloat() - 0.5) * ${radious}D;
		world.spawnParticle(EnumParticleTypes.${particleObj.toString()}, d0, d1, d2, 0, 0, 0);
    }
</#macro>

<#macro particlesTube particleObj radious amount condition>
if(${condition})
    for (int l = 0; l < ${amount}; ++l){
		double d0=(i+0.5)+(random.nextFloat()-0.5)* ${radious}D;
		double d1=((j+0.7)+(random.nextFloat()-0.5)* ${radious}D*100)+0.5D;
		double d2=(k+0.5)+(random.nextFloat()-0.5)* ${radious}D;
		world.spawnParticle(EnumParticleTypes.${particleObj.toString()}, d0, d1, d2, 0, 0, 0);
	}
</#macro>