package dev.crystallite.mixin;

import dev.crystallite.CrystalConfig;
import dev.crystallite.CrystalLite;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

	/** After you hit an end crystal, drop it locally right away instead of waiting for the server. */
	@Inject(method = "attackEntity", at = @At("TAIL"))
	private void crystallite$removeCrystalLocally(PlayerEntity player, Entity target, CallbackInfo ci) {
		CrystalConfig cfg = CrystalConfig.get();
		if (!cfg.enabled || !cfg.instantRemoval) return;

		MinecraftClient mc = MinecraftClient.getInstance();
		if (player != mc.player || mc.world == null) return;

		if (target instanceof EndCrystalEntity) {
			mc.world.removeEntity(target.getId(), Entity.RemovalReason.DISCARDED);
		}
	}

	/** Optional: refuse crystal placements that come faster than the configured pace. */
	@Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
	private void crystallite$paceCrystalPlacement(ClientPlayerEntity player, Hand hand,
			BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		CrystalConfig cfg = CrystalConfig.get();
		if (!cfg.enabled || !cfg.pacePlacement) return;
		if (!player.getStackInHand(hand).isOf(Items.END_CRYSTAL)) return;

		long now = System.currentTimeMillis();
		if (now - CrystalLite.lastPlaceMs < cfg.minPlacementIntervalMs()) {
			cir.setReturnValue(ActionResult.FAIL);
			return;
		}
		CrystalLite.lastPlaceMs = now;
	}
}
