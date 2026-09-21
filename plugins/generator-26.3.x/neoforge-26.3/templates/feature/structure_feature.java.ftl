<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2024, Pylo, opensource contributors
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
package ${package}.world.features;

public record StructureFeature(Identifier structure, boolean randomRotation, boolean randomMirror, HolderSet<Block> ignoredBlocks, Vec3i offset) implements Feature {
	public static final DeferredRegister<MapCodec<? extends Feature>> REGISTRY = DeferredRegister.create(Registries.FEATURE_TYPE, ${JavaModName}.MODID);
	public static final DeferredHolder<MapCodec<? extends Feature>, MapCodec<? extends Feature>> STRUCTURE_FEATURE = REGISTRY.register("structure_feature", () -> StructureFeature.CODEC);

	public static final MapCodec<StructureFeature> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		Identifier.CODEC.fieldOf("structure").forGetter(StructureFeature::structure),
		Codec.BOOL.fieldOf("random_rotation").orElse(false).forGetter(StructureFeature::randomRotation),
		Codec.BOOL.fieldOf("random_mirror").orElse(false).forGetter(StructureFeature::randomMirror),
		RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("ignored_blocks").forGetter(StructureFeature::ignoredBlocks),
		Vec3i.offsetCodec(48).optionalFieldOf("offset", Vec3i.ZERO).forGetter(StructureFeature::offset)
	).apply(builder, StructureFeature::new));

	@Override
	public MapCodec<StructureFeature> codec() {
		return CODEC;
	}

	public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
		Rotation rotation = this.randomRotation() ? Rotation.getRandom(random) : Rotation.NONE;
		Mirror mirror = this.randomMirror() ? Mirror.values()[random.nextInt(2)] : Mirror.NONE;
		// Load the structure template
		StructureTemplateManager structureManager = worldGenLevel.getLevel().getServer().getStructureTemplateManager();
		StructureTemplate template = structureManager.getOrCreate(this.structure());
		StructurePlaceSettings placeSettings = new StructurePlaceSettings().setRotation(rotation).setMirror(mirror).setRandom(random).setIgnoreEntities(false)
				.addProcessor(new BlockIgnoreProcessor(this.ignoredBlocks().stream().map(Holder::value).toList()));
		BlockPos placePos = origin.offset(StructureTemplate.calculateRelativePosition(placeSettings, new BlockPos(this.offset().getX(), this.offset().getY(), this.offset().getZ())));
		template.placeInWorld(worldGenLevel, placePos, placePos, placeSettings, random, 2);
		return true;
	}
}
<#-- @formatter:on -->
