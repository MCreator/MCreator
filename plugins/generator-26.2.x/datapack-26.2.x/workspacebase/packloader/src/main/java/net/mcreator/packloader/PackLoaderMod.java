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
		modEventBus.addListener(this::addPacks);
	}

	public void addPacks(AddPackFindersEvent event) {
		event.addRepositorySource(new FolderRepositorySource(FMLPaths.getOrCreateGameRelativePath(Path.of("datapacks")),
				PackType.SERVER_DATA, PackSource.DEFAULT, new DirectoryValidator(path -> false)));
	}

}
