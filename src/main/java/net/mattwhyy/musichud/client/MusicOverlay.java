package net.mattwhyy.musichud.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jline.utils.Log;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MusicOverlay {
    private static String currentTrack = null;
    private static long displayStart = 0;
    private static long displayUntil = 0;
    private static ResourceLocation currentDiscTexture;
    private static float currentDiscRotation = 0.0f;

    private static final Map<String, ResourceLocation> SPECIAL_DISC_TEXTURES = Map.ofEntries(
            Map.entry("Lena Raine - Ballad of the Cats", ResourceLocation.fromNamespaceAndPath("musichud", "textures/discs/discspecialnether.png")),
            Map.entry("C418 - Dead Voxel", ResourceLocation.fromNamespaceAndPath("musichud", "textures/discs/discspecialnether.png")),
            Map.entry("C418 - Warmth", ResourceLocation.fromNamespaceAndPath("musichud", "textures/discs/discspecialnether.png")),
            Map.entry("C418 - Concrete Halls", ResourceLocation.fromNamespaceAndPath("musichud", "textures/discs/discspecialnether.png")),
            Map.entry("Lena Raine - So Below", ResourceLocation.fromNamespaceAndPath("musichud", "textures/discs/discspecialnether.png")),
            Map.entry("Lena Raine - Chrysopoeia", ResourceLocation.fromNamespaceAndPath("musichud", "textures/discs/discspecialnether.png")),
            Map.entry("Lena Raine - Rubedo", ResourceLocation.fromNamespaceAndPath("musichud", "textures/discs/discspecialnether.png")),
            Map.entry("Lena Raine - Ancestry", ResourceLocation.fromNamespaceAndPath("musichud", "textures/discs/discspecialdeep.png")),
            Map.entry("Lena Raine - Deeper", ResourceLocation.fromNamespaceAndPath("musichud", "textures/discs/discspecialdeep.png"))
    );


    private static final int DISPLAY_DURATION = 7500;
    private static final int ROLL_DURATION = 1000;
    private static final int DISC_SIZE = 16;

    private static float easeOutBack(float t) {
        float s = 1.70158f;
        return 1 + ((t - 1) * (t - 1) * ((s + 1) * (t - 1) + s));
    }

    public static void displayNowPlaying(String trackId) {
        System.out.println("Track ID: " + trackId);
        if (SPECIAL_DISC_TEXTURES.containsKey(trackId)) {
            currentDiscTexture = SPECIAL_DISC_TEXTURES.get(trackId);
        } else {
            int randomDisc = ThreadLocalRandom.current().nextInt(1, 12);
            currentDiscTexture = ResourceLocation.fromNamespaceAndPath("musichud", "textures/discs/disc" + randomDisc + ".png");
        }
        currentTrack = trackId;
        displayStart = System.currentTimeMillis();
        displayUntil = displayStart + DISPLAY_DURATION;
        currentDiscRotation = ThreadLocalRandom.current().nextFloat() * 360f;
    }

    public static void render(GuiGraphics guiGraphics, DeltaTracker unused) {
        if (currentTrack == null) return;

        long now = System.currentTimeMillis();
        long timeElapsed = now - displayStart;
        if (timeElapsed >= DISPLAY_DURATION) {
            currentTrack = null;
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        int finalIconX = 10;
        int startIconX = -DISC_SIZE;
        int iconX;
        float textAlpha = 1.0f;

        if (timeElapsed < ROLL_DURATION) {
            float progress = timeElapsed / (float) ROLL_DURATION;
            float easedProgress = easeOutBack(progress);
            iconX = (int)(startIconX + easedProgress * (finalIconX - startIconX));
        } else if (timeElapsed < DISPLAY_DURATION - ROLL_DURATION) {
            iconX = finalIconX;
        } else {
            float tOut = (timeElapsed - (DISPLAY_DURATION - ROLL_DURATION)) / (float) ROLL_DURATION;
            float easedOut = 1 - easeOutBack(1 - tOut);
            iconX = (int)(finalIconX + easedOut * (startIconX - finalIconX));
            textAlpha = 1 - tOut;
        }

        int iconY = 10;
        int lineHeight = mc.font.lineHeight;
        int discY = iconY + (lineHeight / 2) - (DISC_SIZE / 2);

        float timeElapsedSeconds = timeElapsed / 1000f;
        float rotationAngle = currentDiscRotation + (timeElapsedSeconds * 36f);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(iconX + DISC_SIZE / 2.0F, discY + DISC_SIZE / 2.0F, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(rotationAngle));
        guiGraphics.pose().translate(-DISC_SIZE / 2.0F, -DISC_SIZE / 2.0F, 0);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(currentDiscTexture, 0, 0, 0, 0, DISC_SIZE, DISC_SIZE, DISC_SIZE, DISC_SIZE);
        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();

        if (timeElapsed < DISPLAY_DURATION - ROLL_DURATION || textAlpha > 0.01f) {
            int argbColor = ((int)(textAlpha * 255) << 24) | 0xCCCCCC;
            Component text = Component.literal("Now Playing: " + currentTrack);
            guiGraphics.drawString(mc.font, text, finalIconX + DISC_SIZE + 4, iconY, argbColor, true);
        }
    }
}
