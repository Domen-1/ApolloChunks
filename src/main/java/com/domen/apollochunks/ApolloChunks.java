package com.domen.apollochunks;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.utils.Translations;
import net.fabricmc.api.ModInitializer;

import java.util.Map;

public class ApolloChunks implements ModInitializer, CarpetExtension {
	public static final String MOD_ID = "apollochunks";

	@Override
	public void onInitialize() {
		CarpetServer.manageExtension(this);
	}

	@Override
	public void onGameStarted() {
		CarpetServer.settingsManager.parseSettingsClass(ApolloChunksSettings.class);
	}

	@Override
	public Map<String, String> canHasTranslations(String lang) {
		// Carpet has no fallback language and rules without a description in the active language fail to load
		Map<String, String> translations = Translations.getTranslationFromResourcePath("assets/" + MOD_ID + "/lang/" + lang + ".json");
		return translations.isEmpty() ? Translations.getTranslationFromResourcePath("assets/" + MOD_ID + "/lang/en_us.json") : translations;
	}
}
