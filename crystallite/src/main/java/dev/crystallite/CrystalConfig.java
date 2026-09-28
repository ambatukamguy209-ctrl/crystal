package dev.crystallite;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CrystalConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("crystallite.json");
	private static CrystalConfig instance = new CrystalConfig();

	// Master switch
	public boolean enabled = true;

	// Crystal removal
	public boolean instantRemoval = true;

	// Crystal placement pacing (limits how fast YOU can place; never places for you)
	public boolean pacePlacement = false;
	public int placementCooldownTicks = 0;   // 0-10 ticks (1 tick = 50 ms)
	public int maxPlacementsPerSecond = 20;  // 1-20

	public static CrystalConfig get() {
		return instance;
	}

	public long minPlacementIntervalMs() {
		long byCooldown = placementCooldownTicks * 50L;
		long byRate = 1000L / Math.max(1, maxPlacementsPerSecond);
		return Math.max(byCooldown, byRate);
	}

	public static void load() {
		try {
			if (Files.exists(FILE)) {
				CrystalConfig loaded = GSON.fromJson(Files.readString(FILE), CrystalConfig.class);
				if (loaded != null) instance = loaded;
			}
		} catch (Exception e) {
			CrystalLite.LOGGER.warn("Could not read config, using defaults", e);
		}
		save();
	}

	public static void save() {
		try {
			Files.writeString(FILE, GSON.toJson(instance));
		} catch (IOException e) {
			CrystalLite.LOGGER.warn("Could not save config", e);
		}
	}
}
