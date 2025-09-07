package net.sphen.magicmodbuns;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.sphen.magicmodbuns.animations.block.MortarAndPestleRenderer;
import net.sphen.magicmodbuns.block.ChalkPatternBlockRenderer;
import net.sphen.magicmodbuns.block.ModBlocks;
import net.sphen.magicmodbuns.block.entity.ModBlockEntities;
import net.sphen.magicmodbuns.events.ClientEvents;
import net.sphen.magicmodbuns.item.ModCreativeModeTabs;
import net.sphen.magicmodbuns.item.ModItems;
import net.sphen.magicmodbuns.screen.ModMenuTypes;
import net.sphen.magicmodbuns.screen.chalk.ChalkScreen;
import net.sphen.magicmodbuns.screen.mortarpestle.MortarPestleScreen;
import net.sphen.magicmodbuns.screen.spellbook.SpellBookScreen;
import net.sphen.magicmodbuns.spells.SpellLoader;
import net.sphen.magicmodbuns.spells.entities.ModSpellEntities;
import net.sphen.magicmodbuns.spells.logic.SpellLogicRegistry;
import net.sphen.magicmodbuns.spells.runes.RuneRegistry;
import net.sphen.magicmodbuns.spells.runes.RuneReloadListener;
import net.sphen.magicmodbuns.spells.runes.RuneType;
import net.sphen.magicmodbuns.util.Packets.*;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MagicMod.MODID)
public class MagicMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "magicmodbuns";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Register the networking channel
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "network_channel"),
            () -> "1.0", // protocol version
            s -> true,  // message validator
            s -> true   // message validator
    );

    public MagicMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        ModSpellEntities.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());

        MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent event) -> {
            event.addListener(new RuneReloadListener());
            event.addListener(new SpellLoader());
        });

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Register network packets
        NETWORK.registerMessage(0, PlaceChalkPatternPacket.class, PlaceChalkPatternPacket::encode, PlaceChalkPatternPacket::decode, PlaceChalkPatternPacket::handle);
        NETWORK.registerMessage(1, SyncChalkPatternPacket.class, SyncChalkPatternPacket::encode, SyncChalkPatternPacket::decode, SyncChalkPatternPacket::handle);
        NETWORK.registerMessage(2, RemoveChalkTexturePacket.class, RemoveChalkTexturePacket::encode, RemoveChalkTexturePacket::decode, RemoveChalkTexturePacket::handle);
        NETWORK.registerMessage(3, CloseBookPacket.class, CloseBookPacket::encode, CloseBookPacket::decode, CloseBookPacket::handle);
        NETWORK.registerMessage(4, CycleSpellPacket.class, CycleSpellPacket::encode, CycleSpellPacket::decode, CycleSpellPacket::handle);

        RuneRegistry.registerRuneId("magicmodbuns:light", RuneType.LIGHT);

        SpellLogicRegistry.init();
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.CHALK);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            System.out.println("ONCLIENTSETUP IS RUNNING");
            event.enqueueWork(() -> {
                MenuScreens.register(ModMenuTypes.MORTAR_PESTLE_MENU.get(), MortarPestleScreen::new);
                MenuScreens.register(ModMenuTypes.CHALK_MENU.get(), ChalkScreen::new);
                MenuScreens.register(ModMenuTypes.SPELL_BOOK_MENU.get(), SpellBookScreen::new);

                //custom block model initialization
                BlockEntityRenderers.register(ModBlockEntities.CHALK_PATTERN.get(), context -> new ChalkPatternBlockRenderer());
                BlockEntityRenderers.register(ModBlockEntities.MORTAR_PESTLE.get(), pContext -> new MortarAndPestleRenderer());

                ItemProperties.register(ModItems.SPELL_PAPER.get(), new ResourceLocation(MODID, "spell_variant"),
                        (stack, level, entity, seed) -> {
                            if (stack.hasTag()) {
                                int chalkId = stack.getTag().getInt("chalk_type");
                                return (float) chalkId;
                            } else {
                                return 0f;
                            }
                        });
            });
        }
    }
}

