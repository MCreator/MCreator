<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2023, Pylo, opensource contributors
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

package ${package}.mixin;

import org.spongepowered.asm.mixin.Unique;

/*
TODO:
- test this out with terrablender and at least two MCreator mods that each adds one biome to overworld (CURRENTLY NOT WORKING - TWO MCREATOR MODS, ONLY ONE MODS SURFACE RULES ARE USED)
 */

@Mixin(NoiseGeneratorSettings.class) public class NoiseGeneratorSettingsMixin implements ${JavaModName}Biomes.${JavaModName}NoiseGeneratorSettings {

	@Unique private Holder<DimensionType> ${modid}_dimensionTypeReference;

	<#-- use order of 100000, to ensure our mixin runs after other mixins such as Terrablender,
	     so the getReturnValue already returns processed rule source. Terrablender uses
	     original field value, so our processing of return value would be ignored otherwise -->
	@Inject(method = "surfaceRule", at = @At("HEAD"), cancellable = true, order = 100000)
	private void surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
		if (this.${modid}_dimensionTypeReference != null) {
			cir.setReturnValue(${JavaModName}Biomes.adaptSurfaceRule(cir.getReturnValue(), this.${modid}_dimensionTypeReference));
		}
	}

	@Override public void set${modid}DimensionTypeReference(Holder<DimensionType> dimensionType) {
		this.${modid}_dimensionTypeReference = dimensionType;
	}

}
