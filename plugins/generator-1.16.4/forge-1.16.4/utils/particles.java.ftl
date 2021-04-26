<#include "tokens.ftl">
<#include "procedures.java.ftl">

<#macro particles type particleObj radious amount condition="">
    <#if type=="Spread">
        <@particlesSpread particleObj radious amount condition/>
    <#elseif type=="Top">
        <@particlesTop particleObj radious amount condition/>
    <#elseif type=="Tube">
        <@particlesTube particleObj radious amount condition/>
    <#elseif type=="Plane">
        <@particlesPlane particleObj radious amount condition/>
    </#if>
</#macro>

<#macro particlesPlane particleObj radious amount condition>
if(<@procedureOBJToConditionCode condition/>)
		for(int l=0;l< ${amount}; ++l) {
		double d0 = (x + 0.5) + (random.nextFloat() - 0.5) * ${radious}D * 20;
		double d1 = ((y + 0.7) + (random.nextFloat() - 0.5) * ${radious}D) + 0.5;
		double d2 = (z + 0.5) + (random.nextFloat() - 0.5) * ${radious}D * 20;
		world.addParticle(${particleObj.toString()}, d0, d1, d2, 0, 0, 0);
	}
</#macro>

<#macro particlesSpread particleObj radious amount condition>
if(<@procedureOBJToConditionCode condition/>)
	for (int l = 0; l < ${amount}; ++l) {
	    double d0 = (x + random.nextFloat());
	    double d1 = (y + random.nextFloat());
	    double d2 = (z + random.nextFloat());
	    int i1 = random.nextInt(2) * 2 - 1;
	    double d3 = (random.nextFloat() - 0.5D) * ${radious}D;
	    double d4 = (random.nextFloat() - 0.5D) * ${radious}D;
	    double d5 = (random.nextFloat() - 0.5D) * ${radious}D;
	    world.addParticle(${particleObj.toString()}, d0, d1, d2, d3, d4, d5);
	}
</#macro>

<#macro particlesTop particleObj radious amount condition>
if(<@procedureOBJToConditionCode condition/>)
    for (int l = 0; l < ${amount}; ++l) {
		double d0 = (double)((float)x + 0.5) + (double)(random.nextFloat() - 0.5) * ${radious}D;
		double d1 = ((double)((float)y + 0.7) + (double)(random.nextFloat() - 0.5) * ${radious}D)+0.5;
		double d2 = (double)((float)z + 0.5) + (double)(random.nextFloat() - 0.5) * ${radious}D;
		world.addParticle(${particleObj.toString()}, d0, d1, d2, 0, 0, 0);
    }
</#macro>

<#macro particlesTube particleObj radious amount condition>
if(<@procedureOBJToConditionCode condition/>)
    for (int l = 0; l < ${amount}; ++l){
		double d0=(x+0.5)+(random.nextFloat()-0.5)* ${radious}D;
		double d1=((y+0.7)+(random.nextFloat()-0.5)* ${radious}D*100)+0.5;
		double d2=(z+0.5)+(random.nextFloat()-0.5)* ${radious}D;
		world.addParticle(${particleObj.toString()},d0,d1,d2,0,0,0);
		}
</#macro>