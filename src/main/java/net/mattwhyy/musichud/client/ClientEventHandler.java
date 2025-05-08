package net.mattwhyy.musichud.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = "musichud", value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        MusicOverlay.render(event.getGuiGraphics(), event.getPartialTick());
    }
}
