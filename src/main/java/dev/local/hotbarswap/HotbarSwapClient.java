package dev.local.hotbarswap;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;

public class HotbarSwapClient implements ClientModInitializer {
    private static final int SLOT_A = 4;
    private static final int SLOT_B = 7;

    private KeyMapping swapKey;
    private boolean wasDown;

    @Override
    public void onInitializeClient() {
        swapKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.hotbarswap.swap",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_LALT,
                KeyMapping.Category.INVENTORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean down = swapKey.isDown();
            if (down && !wasDown) {
                toggleSlot(client);
            }
            wasDown = down;
        });
    }

    private static void toggleSlot(Minecraft client) {
        if (client.player == null || client.gui.screen() != null) {
            return;
        }
        int current = client.player.getInventory().getSelectedSlot();
        int target = current == SLOT_B ? SLOT_A : SLOT_B;
        client.player.getInventory().setSelectedSlot(target);
        if (client.player.connection != null) {
            client.player.connection.send(new ServerboundSetCarriedItemPacket(target));
        }
    }
}
