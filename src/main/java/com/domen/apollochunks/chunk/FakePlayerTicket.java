package com.domen.apollochunks.chunk;

import com.domen.apollochunks.ApolloChunks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.TicketType;

/**
 * Loading ticket that fake players get on every chunk within view distance, without going through the vanilla
 * player ticket throttler. Loading only (simulation stays on the vanilla player_simulation ticket), no timeout,
 * not saved to disk, so removing the mod leaves nothing behind in the world.
 */
public final class FakePlayerTicket {
	public static TicketType TYPE;

	private FakePlayerTicket() {
	}

	/** Called from TicketTypeMixin while vanilla registers its own ticket types: mod entrypoints run after the registries are frozen. */
	public static void register() {
		TYPE = Registry.register(
			BuiltInRegistries.TICKET_TYPE,
			Identifier.fromNamespaceAndPath(ApolloChunks.MOD_ID, "fake_player"),
			new TicketType(TicketType.NO_TIMEOUT, TicketType.FLAG_LOADING)
		);
	}
}
