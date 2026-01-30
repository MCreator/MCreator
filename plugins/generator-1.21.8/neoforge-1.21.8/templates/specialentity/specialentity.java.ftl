<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2025, Pylo, opensource contributors
 # 
 # This program is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 3 of the License, or
 # (at your option) any later version.
 # 
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 # GNU General Public License for more details.
 # 
 # You should have received a copy of the GNU General Public License
 # along with this program.  If not, see <https://www.gnu.org/licenses/>.
 # 
 # Additional permission for code generator templates (*.ftl files)
 # 
 # As a special exception, you may create a larger work that contains part or 
 # all of the MCreator code generator templates (*.ftl files) and distribute 
 # that work under terms of your choice, so long as that work isn't itself a 
 # template for code generation. Alternatively, if you modify or redistribute 
 # the template itself, you may (at your option) remove this special exception, 
 # which will cause the template and the resulting code generator output files 
 # to be licensed under the GNU General Public License without this special 
 # exception.
-->

<#-- @formatter:off -->
<#include "../procedures.java.ftl">

package ${package}.entity;

public class ${name}Entity extends ${data.entityType} {

	public ${name}Entity(EntityType<${name}Entity> type, Level world) {
		super(type, world, ${JavaModName}Items.${REGISTRYNAME});
	}

	<#if hasProcedure(data.onTickUpdate)>
	@Override public void baseTick() {
		super.baseTick();
		<#if hasProcedure(data.onTickUpdate)>
			<@procedureCode data.onTickUpdate, {
				"x": "this.getX()",
				"y": "this.getY()",
				"z": "this.getZ()",
				"entity": "this",
				"world": "this.level()"
			}/>
		</#if>
	}
	</#if>

	<#if hasProcedure(data.onPlayerCollidesWith)>
	@Override public void playerTouch(Player sourceentity) {
		super.playerTouch(sourceentity);
		<@procedureCode data.onPlayerCollidesWith, {
			"x": "this.getX()",
			"y": "this.getY()",
			"z": "this.getZ()",
			"entity": "this",
			"sourceentity": "sourceentity",
			"world": "this.level()"
		}/>
	}
	</#if>
}
<#-- @formatter:off -->
