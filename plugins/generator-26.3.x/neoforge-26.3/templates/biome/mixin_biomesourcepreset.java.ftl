<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2026, Pylo, opensource contributors
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

import org.spongepowered.asm.mixin.Mutable;

@Mixin(MultiNoiseBiomeSourceParameterList.Preset.class) public class BiomeSourcePresetMixin {

	@Mutable @Shadow @Final private MultiNoiseBiomeSourceParameterList.Preset.SourceProvider provider;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void daisyChainProvider(Identifier idArg, MultiNoiseBiomeSourceParameterList.Preset.SourceProvider ignored, CallbackInfo ci) {
		if (idArg.equals(${JavaModName}Biomes.OVERWORLD_BIOMESOURCE_PRESET_ID) || idArg.equals(${JavaModName}Biomes.NETHER_BIOMESOURCE_PRESET_ID)) {
			<#-- Capture the current state of the field, which includes any previous mod's wrappers, calling this.provider directly in apply is not safe -->
			MultiNoiseBiomeSourceParameterList.Preset.SourceProvider existingProvider = this.provider;
			this.provider = new MultiNoiseBiomeSourceParameterList.Preset.SourceProvider() {
				@Override public <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> lookup) {
					<#-- Call the chain. If another mod ran before us, this safely calls their logic first. -->
					Climate.ParameterList<T> originalList = existingProvider.apply(lookup);
					return ${JavaModName}Biomes.adaptPresetParameterList(idArg, originalList, lookup);
				}
			};
		}
	}

}
