package net.atobaazul.spellslinger;

import com.sammy.malum.client.screen.codex.WidgetDesignType;
import com.sammy.malum.client.screen.codex.entries.VoidCodexEntries;
import com.sammy.malum.client.screen.codex.pages.recipe.SpiritInfusionPage;
import com.sammy.malum.client.screen.codex.pages.text.HeadlineTextPage;
import com.sammy.malum.client.screen.codex.pages.text.TextPage;
import com.sammy.malum.client.screen.codex.pages.text.WeepingWellTextPage;
import com.sammy.malum.client.screen.codex.screens.progression.AbstractProgressionCodexScreen;
import com.sammy.malum.client.screen.codex.screens.progression.ArcanaProgressionScreen;
import com.sammy.malum.client.screen.codex.screens.progression.VoidProgressionScreen;
import com.sammy.malum.core.systems.events.SetupMalumCodexEntriesEvent;
import com.sammy.malum.registry.common.item.MalumItems;
import com.sammy.malum.registry.common.magic.MalumGeasEffectTypes;
import net.atobaazul.spellslinger.registry.SpellslingerGeasEffectTypes;
import net.atobaazul.spellslinger.registry.SpellslingerItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import static com.sammy.malum.client.screen.codex.entries.GeasEntries.addGeasEntry;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Spellslinger.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Spellslinger.MODID, value = Dist.CLIENT)
public class SpellslingerClient {
    public SpellslingerClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
    }

    @SubscribeEvent
    public static void setupMalumBook(SetupMalumCodexEntriesEvent event) {
        AbstractProgressionCodexScreen screen = event.getScreen();

        if (screen instanceof ArcanaProgressionScreen arcanaScreen) {
            //soulshot modifier
            arcanaScreen.addEntry("modifier_soulshot", -7, 6, (b) -> {
                b.configureWidget((w) -> {
                    w.setIcon(SpellslingerItems.SOULSHOT_MODIFIER);
                }).addPage(new HeadlineTextPage("soulshot_modifier", "soulshot_modifier.1")).addPage(SpiritInfusionPage.fromOutput((Item)SpellslingerItems.SOULSHOT_MODIFIER.get()));;
            });

            //ammo belt
            arcanaScreen.addEntry("bullet_warp_belt", -9, 8, (b) -> {
                b.configureWidget((w) -> {
                    w.setIcon(SpellslingerItems.BULLET_WARP_BELT);
                }).addPage(new HeadlineTextPage("bullet_warp_belt", "bullet_warp_belt.1")).addPage(SpiritInfusionPage.fromOutput((Item)SpellslingerItems.BULLET_WARP_BELT.get()));;
            });

            //geas
            addGeasEntry(arcanaScreen, SpellslingerGeasEffectTypes.PACT_OF_THE_MARKSMAN, 12, 16);
        } else if (screen instanceof VoidProgressionScreen voidScreen) {
            voidScreen.addEntry("void.mystic_reverb_necklace", -5, 13, (b) -> {
                b.configureWidget((w) -> {
                    w.setIcon(SpellslingerItems.MYSTIC_REVERB_NECKLACE).setDesign(WidgetDesignType.DEFAULT, WidgetDesignType.FrameType.SOULWOOD, WidgetDesignType.FillingType.PAPER);
                }).addPage(new HeadlineTextPage("void.mystic_reverb_necklace", "void.mystic_reverb_necklace.1")).addPage(SpiritInfusionPage.fromOutput((Item)SpellslingerItems.MYSTIC_REVERB_NECKLACE.get())).afterUmbralCrystal();
            });
            screen.addEntry("void.modifier_spirit_shredder", 4, 6, (b) -> {
                b.configureWidget((w) -> {
                    w.setIcon(SpellslingerItems.SPIRIT_SHREDDER_MODIFIER).setDesign(WidgetDesignType.DEFAULT, WidgetDesignType.FrameType.SOULWOOD, WidgetDesignType.FillingType.PAPER);
                }).addPage(new WeepingWellTextPage("void.modifier_spirit_shredder", "void.modifier_spirit_shredder.1", (Item)SpellslingerItems.SPIRIT_SHREDDER_MODIFIER.get())).addPage(SpiritInfusionPage.fromOutput((Item)SpellslingerItems.SPIRIT_SHREDDER_MODIFIER.get()));
            });

            VoidCodexEntries.addGeasEntry(voidScreen, SpellslingerGeasEffectTypes.PACT_OF_THE_SPELLSLINGER, -8, 16);
        }
    }
}
