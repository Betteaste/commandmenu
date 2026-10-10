package com.commandmenu.client;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public final class ClientEvents {
    public static final KeyMapping OPEN_MENU = new KeyMapping(
            "key.commandmenu.open", GLFW.GLFW_KEY_G, "key.categories.commandmenu");

    private ClientEvents() {}

    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MENU);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        while (OPEN_MENU.consumeClick()) {
            if (mc.player != null && mc.screen == null) {
                mc.setScreen(new CommandMenuScreen());
            }
        }
    }
}
