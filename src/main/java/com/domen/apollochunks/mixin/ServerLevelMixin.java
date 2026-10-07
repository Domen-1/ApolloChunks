package com.domen.apollochunks.mixin;

import com.domen.apollochunks.ApolloChunksSettings;
import com.domen.apollochunks.FakePlayers;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
	// Real players move their loaded area with every movement packet, Carpet bots only every 10 ticks.
	// After the entities have ticked, so the tickets get processed in the idle time before the next tick.
	@Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At("TAIL"))
	private void apollochunks$moveFakePlayerChunks(BooleanSupplier haveTime, CallbackInfo ci) {
		if (!ApolloChunksSettings.fakePlayerChunkUpdateEveryTick) {
			return;
		}

		ServerLevel level = (ServerLevel) (Object) this;
		List<ServerPlayer> players = level.players();
		for (int i = 0; i < players.size(); i++) {
			ServerPlayer player = players.get(i);
			SectionPos lastPos = player.getLastSectionPos();
			if (FakePlayers.isFake(player) && lastPos != null && SectionPos.of(player).asLong() != lastPos.asLong()) {
				level.getChunkSource().move(player);
			}
		}
	}
}
