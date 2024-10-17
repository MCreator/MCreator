<#-- @formatter:off -->
<#include "tokens.ftl">

package ${package}.gui.overlay;

@Elements${JavaModName}.ModElement.Tag public class Overlay${name} extends Elements${JavaModName}.ModElement{

	public Overlay${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override @SideOnly(Side.CLIENT) public void init(FMLInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(new GUIRenderEventClass());
	}

	public static class GUIRenderEventClass {

		@SubscribeEvent(priority = EventPriority.${data.priority}) @SideOnly(Side.CLIENT)
		public void eventHandler(RenderGameOverlayEvent event) {

			if (!event.isCancelable() && event.getType() == RenderGameOverlayEvent.ElementType.HELMET) {

				int posX = (event.getResolution().getScaledWidth()) / 2;
				int posY = (event.getResolution().getScaledHeight()) / 2;

				EntityPlayer entity = Minecraft.getMinecraft().player;
				World world = entity.world;
				int x = (int) entity.posX;
				int y = (int) entity.posY;
				int z = (int) entity.posZ;

				if (${conditioncode}) {
					<#if data.baseTexture?has_content>
						GlStateManager.disableDepth();
        				GlStateManager.depthMask(false);
        				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        				GlStateManager.disableAlpha();

						Minecraft.getMinecraft().renderEngine
									.bindTexture(new ResourceLocation("${modid}:textures/${data.baseTexture}"));
						Minecraft.getMinecraft().ingameGUI.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, ${data.baseTextureW}, ${data.baseTextureH},
								event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight());

						GlStateManager.depthMask(true);
        				GlStateManager.enableDepth();
        				GlStateManager.enableAlpha();
        				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					</#if>

					<#list data.components as component>
                        <#assign x = (component.x/2 - 213)?round>
                        <#assign y = (component.y/2 - 120)?round>
                        <#if component.getClass().getSimpleName() == "Label">
							Minecraft.getMinecraft().fontRenderer
								.drawString("${translateTokens(JavaConventions.escapeStringForJava(component.text))}",
										posX + ${x}, posY + ${y}, ${component.color.getRGB()});
                        <#elseif component.getClass().getSimpleName() == "Image">
							GlStateManager.disableDepth();
							GlStateManager.depthMask(false);
							GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
							GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
							GlStateManager.disableAlpha();

							Minecraft.getMinecraft().renderEngine
									.bindTexture(new ResourceLocation("${modid}:textures/${component.image}"));
							Minecraft.getMinecraft().ingameGUI
								.drawTexturedModalRect(posX + ${x}, posY + ${y}, 0, 0, 256, 256);

							GlStateManager.depthMask(true);
        					GlStateManager.enableDepth();
        					GlStateManager.enableAlpha();
        					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        </#if>
                    </#list>
				}
			}

		}
	}

}
<#-- @formatter:on -->