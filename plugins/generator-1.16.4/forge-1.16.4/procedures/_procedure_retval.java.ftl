<#include "procedures.java.ftl">
<#if type == "ITEMSTACK">/*@ItemStack*/
<#elseif type == "BLOCKSTATE">/*@BlockState*/
</#if><@procedureToRetvalCode name=procedure dependencies=dependencies type=java_type />