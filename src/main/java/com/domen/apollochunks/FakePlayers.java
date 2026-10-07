package com.domen.apollochunks;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.server.level.ServerPlayer;

public final class FakePlayers {
	private FakePlayers() {
	}

	/** Bots from /player and shadows of real players left by /player ... shadow. */
	public static boolean isFake(ServerPlayer player) {
		return player instanceof EntityPlayerMPFake;
	}
}
