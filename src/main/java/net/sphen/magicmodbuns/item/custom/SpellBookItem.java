package net.sphen.magicmodbuns.item.custom;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.sphen.magicmodbuns.animations.item.SpellBookRenderer;
import net.sphen.magicmodbuns.screen.spellbook.SpellBookItemStackHandler;
import net.sphen.magicmodbuns.screen.spellbook.SpellBookMenu;
import net.sphen.magicmodbuns.util.ModTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.function.Consumer;

public class SpellBookItem extends Item implements GeoItem {
    private static int maxStackSize = 1;
    int maxDamage = 2;
    public boolean isOpen = false;
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public SpellBookItem(Properties pProperties) {
        super(pProperties.stacksTo(maxStackSize));

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        if(!pLevel.isClientSide()) {
            ItemStack stack = pPlayer.getItemInHand(pUsedHand);

            this.triggerAnim(pPlayer, GeoItem.getId(stack), "controller", "open");
            isOpen = true;


            NetworkHooks.openScreen((ServerPlayer) pPlayer, new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer1) ->
                    new SpellBookMenu(pContainerId, pPlayerInventory, stack),
                    Component.literal("Spell Book")), buf -> buf.writeItem(stack));
        }
        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return maxStackSize;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return maxDamage;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            private final LazyOptional<IItemHandler> handler = LazyOptional.of(() ->
                    new SpellBookItemStackHandler(9, ModTags.Items.SPELL_BOOK_PAPER_TAG));

            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side){
                return cap == ForgeCapabilities.ITEM_HANDLER ? handler.cast() : LazyOptional.empty();
            }
        };
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        pStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            if (h instanceof ItemStackHandler handler) {
                SpellBookItemStackHandler.saveHandlerToStack(pStack, handler);
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (isOpen) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("open"));
            } else {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("close"));
            }
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final SpellBookRenderer renderer = new SpellBookRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
}
