package dev.crystallite;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrystalLite implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("crystallite");

	/** Timestamp (ms) of the last crystal placement attempt, used by the pacing option. */
	public static long lastPlaceMs = 0L;

	@Override
	public void onInitializeClient() {
		CrystalConfig.load();

		// /crystallite opens the config screen
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
			dispatcher.register(ClientCommandManager.literal("crystallite").executes(ctx -> {
				MinecraftClient mc = ctx.getSource().getClient();
				mc.send(() -> mc.setScreen(new CrystalConfigScreen(null)));
				return 1;
			}))
		);
	}
}
