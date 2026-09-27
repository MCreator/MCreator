<#if w.getGElementsOfType("biome")?filter(e -> e.spawnBiome || e.spawnInCaves || e.spawnBiomeNether)?size != 0>
public net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList$Preset$SourceProvider
</#if>

<#if w.getGElementsOfType("biome")?filter(e -> e.hasVines() || e.hasFruits())?size != 0>
public net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType <init>(Lcom/mojang/serialization/MapCodec;)V
</#if>

<#if w.hasElementsOfType("armor")>
public-f net.minecraft.client.model.Model renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V
public net.minecraft.client.renderer.feature.FeatureRenderDispatcher featureRenderers
public net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer currentGroup
public net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer getVertexBuilder(Lnet/minecraft/client/renderer/rendertype/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;
</#if>

<#if w.hasElementsOfType("fluid")>
public net.minecraft.world.entity.LivingEntity travelInWater(Lnet/minecraft/world/phys/Vec3;DZD)V
public net.minecraft.world.entity.LivingEntity travelInLava(Lnet/minecraft/world/phys/Vec3;DZD)V
public net.minecraft.world.entity.animal.axolotl.Axolotl travelInWater(Lnet/minecraft/world/phys/Vec3;DZD)V
public net.minecraft.world.entity.animal.dolphin.Dolphin travelInWater(Lnet/minecraft/world/phys/Vec3;DZD)V
public net.minecraft.world.entity.animal.fish.AbstractFish travelInWater(Lnet/minecraft/world/phys/Vec3;DZD)V
public net.minecraft.world.entity.animal.frog.Frog travelInWater(Lnet/minecraft/world/phys/Vec3;DZD)V
public net.minecraft.world.entity.animal.nautilus.AbstractNautilus travelInWater(Lnet/minecraft/world/phys/Vec3;DZD)V
public net.minecraft.world.entity.animal.turtle.Turtle travelInWater(Lnet/minecraft/world/phys/Vec3;DZD)V
public net.minecraft.world.entity.monster.Guardian travelInWater(Lnet/minecraft/world/phys/Vec3;DZD)V
public net.minecraft.world.entity.monster.zombie.Drowned travelInWater(Lnet/minecraft/world/phys/Vec3;DZD)V
</#if>

# Start of user code block custom ATs
# End of user code block custom ATs
