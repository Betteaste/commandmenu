package com.commandmenu;

import com.commandmenu.client.ClientEvents;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Mod 100 % client : aucune donnée n'est ajoutée au monde, il fonctionne donc
 * aussi sur un serveur vanilla (si tu as les droits opérateur).
 */
@Mod(value = CommandMenuMod.MODID, dist = Dist.CLIENT)
public class CommandMenuMod {
    public static final String MODID = "commandmenu";

    public CommandMenuMod(IEventBus modEventBus) {
        modEventBus.addListener(ClientEvents::registerKeys);
        NeoForge.EVENT_BUS.addListener(ClientEvents::onClientTick);
    }
}
