<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2020 Pylo and contributors
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
<#include "../mcitems.ftl">

package ${package}.world.teleporter;

public class ${name}PortalShape ${mcc.getClassBody("net.minecraft.world.level.portal.PortalShape")
        .replace("class PortalShape", "class " + name + "PortalShape")
        .replace("public PortalShape", "public " + name + "PortalShape")
        .replace("new PortalShape(", "new " + name + "PortalShape(")
        .replace("Optional<PortalShape>", "Optional<" + name + "PortalShape>")
        .replace("Predicate<PortalShape>", "Predicate<" + name + "PortalShape>")
        .replace("blockstate, 18);", "blockstate, 18);\nif (this.level instanceof ServerLevel) ((ServerLevel) this.level).getPoiManager().add(p_77725_, " + name + "Teleporter.poi);")
        .replace("blockstate.is(Blocks.NETHER_PORTAL)", "blockstate.getBlock() == " + JavaModName + "Blocks." + registryname?upper_case + "_PORTAL")
        .replace("p_77718_.is(BlockTags.FIRE) || p_77718_.is(Blocks.NETHER_PORTAL)", "p_77718_.getBlock() == " + JavaModName + "Blocks." + registryname?upper_case + "_PORTAL")
        .replace("Blocks.NETHER_PORTAL.defaultBlockState()", JavaModName + "Blocks." + registryname?upper_case + "_PORTAL.defaultBlockState()")
        .replace("return p_77720_.isPortalFrame(p_77721_, p_77722_);", "return p_77720_.getBlock() ==" + mappedBlockToBlock(data.portalFrame) + ";")}

<#-- @formatter:on -->

