package net.mattwhyy.musichud.mixin;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.mattwhyy.musichud.client.MusicOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    private static final Map<String, String> MUSIC_NAME_MAP = Map.ofEntries(
            Map.entry("music/game/clark", "C418 - Clark"),
            Map.entry("music/game/oxygene", "C418 - Oxygène"),
            Map.entry("music/game/a_familiar_room", "Aaron Cherof - A Familiar Room"),
            Map.entry("music/game/comforting_memories", "Kumi Tanioka - Comforting Memories"),
            Map.entry("music/game/danny", "C418 - Danny"),
            Map.entry("music/game/dry_hands", "C418 - Dry Hands"),
            Map.entry("music/game/featherfall", "Aaron Cherof - Featherfall"),
            Map.entry("music/game/floating_dream", "Kumi Tanioka - Floating Dream"),
            Map.entry("music/game/haggstrom", "C418 - Haggstrom"),
            Map.entry("music/game/key", "C418 - Key"),
            Map.entry("music/game/komorebi", "Kumi Tanioka - komorebi"),
            Map.entry("music/game/left_to_bloom", "Lena Raine - Left to Bloom"),
            Map.entry("music/game/living_mice", "C418 - Living Mice"),
            Map.entry("music/game/mice_on_venus", "C418 - Mice on Venus"),
            Map.entry("music/game/minecraft", "C418 - Minecraft"),
            Map.entry("music/game/one_more_day", "Lena Raine - One More Day"),
            Map.entry("music/game/puzzlebox", "Aaron Cherof - Puzzlebox"),
            Map.entry("music/game/subwoofer_lullaby", "C418 - Subwoofer Lullaby"),
            Map.entry("music/game/sweden", "C418 - Sweden"),
            Map.entry("music/game/watcher", "Aaron Cherof - Watcher"),
            Map.entry("music/game/wet_hands", "C418 - Wet Hands"),
            Map.entry("music/game/yakusoku", "Kumi Tanioka - yakusoku"),
            Map.entry("music/game/creative/aria_math", "C418 - Aria Math"),
            Map.entry("music/game/creative/biome_fest", "C418 - Biome Fest"),
            Map.entry("music/game/creative/blind_spots", "C418 - Blind Spots"),
            Map.entry("music/game/creative/dreiton", "C418 - Dreiton"),
            Map.entry("music/game/creative/haunt_muskie", "C418 - Haunt Muskie"),
            Map.entry("music/game/creative/taswell", "C418 - Taswell"),
            Map.entry("music/game/end/alpha", "C418 - Alpha"),
            Map.entry("music/game/end/boss", "C418 - Boss"),
            Map.entry("music/game/end/the_end", "C418 - The End"),
            Map.entry("music/game/nether/ballad_of_the_cats", "Lena Raine - Ballad of the Cats"),
            Map.entry("music/game/nether/concrete_halls", "C418 - Concrete Halls"),
            Map.entry("music/game/nether/warmth", "C418 - Warmth"),
            Map.entry("music/game/nether/dead_voxel", "C418 - Dead Voxel"),
            Map.entry("music/game/nether/soulsand_valley/so_below", "Lena Raine - So Below"),
            Map.entry("music/game/nether/crimson_forest/chrysopoeia", "Lena Raine - Chrysopoeia"),
            Map.entry("music/game/nether/nether_wastes/rubedo", "Lena Raine - Rubedo"),
            Map.entry("music/game/endless", "Lena Raine - Endless"),
            Map.entry("music/game/eld_unknown", "Lena Raine - Eld Unknown"),
            Map.entry("music/game/wending", "Lena Raine - Wending"),
            Map.entry("music/game/infinite_amethyst", "Lena Raine - Infinite Amethyst"),
            Map.entry("music/game/pokopoko", "Kumi Tanioka - pokopoko"),
            Map.entry("music/game/stand_tall", "Lena Raine - Stand Tall"),
            Map.entry("music/game/bromeliad", "Aaron Cherof - Bromeliad"),
            Map.entry("music/game/echo_in_the_wind", "Aaron Cherof - Echo in the Wind"),
            Map.entry("music/game/crescent_dunes", "Aaron Cherof - Crescent Dunes"),
            Map.entry("music/game/swamp/labyrinthine", "Lena Raine - Labyrinthine"),
            Map.entry("music/game/aerie", "Lena Raine - Aerie"),
            Map.entry("music/game/an_ordinary_day", "Kumi Tanioka - An Ordinary Day"),
            Map.entry("music/game/firebugs", "Lena Raine - Firebugs"),
            Map.entry("music/game/ancestry", "Lena Raine - Ancestry"),
            Map.entry("music/game/deeper", "Lena Raine - Deeper"),
            Map.entry("music/game/water/axolotl", "C418 - Axolotl"),
            Map.entry("music/game/water/dragon_fish", "C418 - Dragon Fish"),
            Map.entry("music/game/water/shuniji", "C418 - Shuniji")


    );

    @Inject(
            method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/sounds/SoundInstance;getSound()Lnet/minecraft/client/resources/sounds/Sound;"
            )
    )
    private void onMusicTrackResolved(SoundInstance soundInstance, CallbackInfo ci) {
        if (soundInstance.getSource() != SoundSource.MUSIC) return;

        Sound sound = soundInstance.getSound();
        if (sound == null) return;

        ResourceLocation id = sound.getLocation();
        String path = id.toString().replace("minecraft:", "");

        String displayName = MUSIC_NAME_MAP.get(path);

        if (displayName == null) {
            String key = "subtitles." + path;
            Component translated = Component.translatable(key);

            if (!translated.getString().equals(key)) {
                displayName = translated.getString();
            } else {
                displayName = id.getNamespace() + " - " + (path);
            }
        }

        System.out.println("[MusicHUD] 🎵 Now playing: " + displayName);
        MusicOverlay.displayNowPlaying(displayName);
    }
}
