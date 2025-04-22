package net.mcreator.packloader;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Mod(PackLoaderMod.MODID) public class PackLoaderMod {

	public static final String MODID = "packloader";

	public PackLoaderMod(IEventBus modEventBus) {
	}

	@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent public static void onClientSetup(FMLClientSetupEvent event) {
			List<String> resourcePacks = new ArrayList<>();
			var resourcePacksPath = FMLPaths.getOrCreateGameRelativePath(Path.of("resourcepacks"));
			if (resourcePacksPath.toFile().exists()) {
				File[] files = resourcePacksPath.toFile().listFiles();
				if (files != null) {
					for (var file : files) {
						resourcePacks.add(file.getName());
					}
				}
			}

			boolean anyChanged = false;
			List<String> selectedPacks = new LinkedList<>(Minecraft.getInstance().getResourcePackRepository().getSelectedIds());
			var allPacks = Minecraft.getInstance().getResourcePackRepository().getAvailableIds();
			for (String pack : allPacks) {
				for (String resourcePack : resourcePacks) {
					if (pack.contains(resourcePack)) {
						anyChanged = true;
						selectedPacks.addLast(pack);
					}
				}
			}

			if (anyChanged) {
				Minecraft.getInstance().getResourcePackRepository().setSelected(selectedPacks);
				Minecraft.getInstance().options.updateResourcePacks(Minecraft.getInstance().getResourcePackRepository());
			}
		}
	}

}
