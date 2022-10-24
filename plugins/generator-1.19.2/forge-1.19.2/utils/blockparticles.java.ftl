<#include "tokens.ftl">

<#macro particles type particleObj radius amount>
    <#if type=="Spread">
        <@particlesSpread particleObj radius amount/>
    <#elseif type=="Top">
        <@particlesTop particleObj radius amount/>
    <#elseif type=="Tube">
        <@particlesTube particleObj radius amount/>
    <#elseif type=="Plane">
        <@particlesPlane particleObj radius amount/>
    </#if>
</#macro>

<#macro particlesPlane particleObj radius amount>
for(int l=0;l< ${amount}; ++l) {
	double x0 = x + 0.5 + (random.nextFloat() - 0.5) * ${radius}D * 20;
	double y0 = y + 1.2 + (random.nextFloat() - 0.5) * ${radius}D;
	double z0 = z + 0.5 + (random.nextFloat() - 0.5) * ${radius}D * 20;
	world.addParticle(${particleObj.toString()}, x0, y0, z0, 0, 0, 0);
}
</#macro>

<#macro particlesSpread particleObj radius amount>
for (int l = 0; l < ${amount}; ++l) {
	double x0 = x + random.nextFloat();
	double y0 = y + random.nextFloat();
	double z0 = z + random.nextFloat();
	double dx = (random.nextFloat() - 0.5D) * ${radius}D;
	double dy = (random.nextFloat() - 0.5D) * ${radius}D;
	double dz = (random.nextFloat() - 0.5D) * ${radius}D;
	world.addParticle(${particleObj.toString()}, x0, y0, z0, dx, dy, dz);
}
</#macro>

<#macro particlesTop particleObj radius amount>
for (int l = 0; l < ${amount}; ++l) {
	double x0 = x + 0.5 + (random.nextFloat() - 0.5) * ${radius}D;
	double y0 = y + 1.2 + (random.nextFloat() - 0.5) * ${radius}D;
	double z0 = z + 0.5 + (random.nextFloat() - 0.5) * ${radius}D;
	world.addParticle(${particleObj.toString()}, x0, y0, z0, 0, 0, 0);
}
</#macro>

<#macro particlesTube particleObj radius amount>
for (int l = 0; l < ${amount}; ++l){
	double x0 = x + 0.5 + (random.nextFloat()-0.5)* ${radius}D;
	double y0 = y + 1.2 + (random.nextFloat()-0.5)* ${radius}D*100;
	double z0 = z + 0.5 +(random.nextFloat()-0.5)* ${radius}D;
	world.addParticle(${particleObj.toString()},x0,y0,z0,0,0,0);
}
</#macro>