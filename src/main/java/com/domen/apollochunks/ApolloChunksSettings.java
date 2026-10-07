package com.domen.apollochunks;

import carpet.api.settings.Rule;

import static carpet.api.settings.RuleCategory.BUGFIX;

public class ApolloChunksSettings {
	private static final String APOLLO = "apollo";

	@Rule(categories = {APOLLO, BUGFIX})
	public static boolean fakePlayerChunkPriority = true;

	@Rule(categories = {APOLLO, BUGFIX})
	public static boolean fakePlayerChunkUpdateEveryTick = true;
}
