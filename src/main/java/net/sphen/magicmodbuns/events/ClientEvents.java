package net.sphen.magicmodbuns.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.effect.ModEffects;
import net.sphen.magicmodbuns.item.custom.SpellBookItem;
import net.sphen.magicmodbuns.util.Packets.CycleSpellPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MagicMod.MODID)
public class ClientEvents {
    private static final EntityDimensions FISH_DIMENSIONS = EntityDimensions.scalable(0.6F, 0.6F);
    private static final Map<UUID, Entity> dummyFishCache = new HashMap<>();

    //checks mouse scroll for Spellbook page switching
    @SubscribeEvent
    public void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Player player = Minecraft.getInstance().player;

        if (player != null && Screen.hasShiftDown()) {
            ItemStack mainHandStack = player.getMainHandItem();
            if (mainHandStack.getItem() instanceof SpellBookItem){
                int scrollDirection = event.getScrollDelta() > 0 ? 1 : -1;

                MagicMod.NETWORK.sendToServer(new CycleSpellPacket(scrollDirection));

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }

        Player player = event.player;
        boolean shouldBeFish = player.hasEffect(ModEffects.TRANSFORMATION.get());

        // Get the player's current pose (standing, swimming)
        EntityDimensions currentDimensions = player.getDimensions(player.getPose());

        if (shouldBeFish && currentDimensions != FISH_DIMENSIONS) {
            player.refreshDimensions();
        } else if (!shouldBeFish && currentDimensions == FISH_DIMENSIONS) {
            player.refreshDimensions();
        }
    }

    @SubscribeEvent
    public static void onEntitySize(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player player) {

            try {

                if (player.hasEffect(ModEffects.TRANSFORMATION.get())) {
                    event.setNewSize(FISH_DIMENSIONS, true);
                    event.setNewEyeHeight(FISH_DIMENSIONS.height * 0.85F);
                }

            } catch (NullPointerException e) {}
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();

        if (player.hasEffect(ModEffects.TRANSFORMATION.get())) {
            MobEffectInstance effect = player.getEffect(ModEffects.TRANSFORMATION.get());

            if(effect == null) return;

            int fishType = effect.getAmplifier();
            EntityType<?> entityToRenderType = getEntityTypeForAmplifier(fishType);
            if (entityToRenderType == null) return;


            Entity dummyEntity = dummyFishCache.computeIfAbsent(player.getUUID(), uuid -> entityToRenderType.create(player.level()));

            if (dummyEntity.getType() != entityToRenderType) {
                dummyEntity = entityToRenderType.create(player.level());
                dummyFishCache.put(player.getUUID(), dummyEntity);
            }

            if (dummyEntity == null) {return;}
            dummyEntity.tickCount = player.tickCount;

            dummyEntity.xo = player.xo;
            dummyEntity.yo = player.yo;
            dummyEntity.zo = player.zo;
            dummyEntity.setPos(player.getX(), player.getY(), player.getZ());
            dummyEntity.setYRot(player.getYRot());
            dummyEntity.setXRot(player.getXRot());
            dummyEntity.setYHeadRot(player.yHeadRot);

            EntityRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(dummyEntity);
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();

            poseStack.translate(0, player.getEyeHeight(), 0);

            poseStack.mulPose(Axis.YP.rotationDegrees(-player.getYRot()));
            if (player.isInWater()) {
                poseStack.mulPose(Axis.XP.rotationDegrees(player.getXRot()));
            }

            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));

            if (!player.isInWater()) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90F));
                poseStack.translate(0, 0.25, 0);
            }

            ((EntityRenderer<Entity>)renderer).render(
                    dummyEntity,
                    0,
                    event.getPartialTick(),
                    event.getPoseStack(),
                    event.getMultiBufferSource(),
                    event.getPackedLight()
            );
            poseStack.popPose();

            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(ModEffects.TRANSFORMATION.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderHotbar(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.hasEffect(ModEffects.TRANSFORMATION.get())) {
                event.setCanceled(true);
            }
        }
    }

    private static EntityType<?> getEntityTypeForAmplifier(int amplifier) {
        return switch (amplifier) {
            case 0 -> EntityType.COD;
            case 1 -> EntityType.SALMON;
            case 2 -> EntityType.TROPICAL_FISH;
            default -> null;
        };
    }

}
