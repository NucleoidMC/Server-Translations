package fr.catcore.translated.server;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class TranslatedServerPreLaunch implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        TranslationGatherer.init();
    }
}
