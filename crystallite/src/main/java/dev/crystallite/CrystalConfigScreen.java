package dev.crystallite;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class CrystalConfigScreen extends Screen {
	private final Screen parent;

	public CrystalConfigScreen(Screen parent) {
		super(Text.literal("CrystalLite Config"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		CrystalConfig c = CrystalConfig.get();
		int w = 220;
		int x = width / 2 - w / 2;
		int y = height / 6 + 10;
		int step = 24;

		addDrawableChild(new TextWidget(x, y - 26, w, 12, title, textRenderer).alignCenter());

		addDrawableChild(toggle(x, y, w, "Master Enabled", () -> c.enabled, v -> c.enabled = v));
		y += step + 6;

		addDrawableChild(toggle(x, y, w, "Instant Crystal Removal", () -> c.instantRemoval, v -> c.instantRemoval = v));
		y += step + 6;

		addDrawableChild(toggle(x, y, w, "Pace Crystal Placement", () -> c.pacePlacement, v -> c.pacePlacement = v));
		y += step;
		addDrawableChild(new IntSlider(x, y, w, "Placement Cooldown (ticks)", 0, 10,
			c.placementCooldownTicks, v -> c.placementCooldownTicks = v));
		y += step;
		addDrawableChild(new IntSlider(x, y, w, "Max Placements / Second", 1, 20,
			c.maxPlacementsPerSecond, v -> c.maxPlacementsPerSecond = v));

		addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
			.dimensions(x, height - 40, w, 20).build());
	}

	@Override
	public void close() {
		CrystalConfig.save();
		client.setScreen(parent);
	}

	private static ButtonWidget toggle(int x, int y, int w, String label, BooleanSupplier get, Consumer<Boolean> set) {
		return ButtonWidget.builder(toggleText(label, get.getAsBoolean()), b -> {
			set.accept(!get.getAsBoolean());
			b.setMessage(toggleText(label, get.getAsBoolean()));
		}).dimensions(x, y, w, 20).build();
	}

	private static Text toggleText(String label, boolean on) {
		return Text.literal(label + ": " + (on ? "ON" : "OFF"));
	}

	private static class IntSlider extends SliderWidget {
		private final String label;
		private final int min;
		private final int max;
		private final IntConsumer setter;

		IntSlider(int x, int y, int w, String label, int min, int max, int value, IntConsumer setter) {
			super(x, y, w, 20, Text.empty(), (value - min) / (double) (max - min));
			this.label = label;
			this.min = min;
			this.max = max;
			this.setter = setter;
			updateMessage();
		}

		private int current() {
			return min + (int) Math.round(value * (max - min));
		}

		@Override
		protected void updateMessage() {
			setMessage(Text.literal(label + ": " + current()));
		}

		@Override
		protected void applyValue() {
			setter.accept(current());
		}
	}
}
