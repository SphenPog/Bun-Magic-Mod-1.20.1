package net.sphen.magicmodbuns.item.custom;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.sphen.magicmodbuns.animations.item.spellbook.SpellBookRenderer;
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
    public static int maxPages = 9;
    private static int indexNum = 0;

    public SpellBookItem(Properties pProperties) {
        super(pProperties.stacksTo(maxStackSize));

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public Component getName(ItemStack pStack) {
        IItemHandler bookInventory = pStack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        if (bookInventory == null) {
            return super.getName(pStack);
        }

        int currentPage = getPageIndex(pStack);
        ItemStack selectedSpell = bookInventory.getStackInSlot(currentPage);

        if (selectedSpell.isEmpty()) {
            return Component.literal(super.getName(pStack).getString() + " [Empty]");
        } else {
            return Component.literal(super.getName(pStack).getString() + " [" + selectedSpell.getHoverName().getString() + "]");
        }
    }

    public static int getPageIndex(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("Index")) {
                return tag.getInt("Index");
            }
        }
        return 0; //default index (first page)
    }

    public static void setPageIndex(ItemStack stack, int indexNum) {
        int newIndex = Math.floorMod(indexNum, maxPages);
        stack.getOrCreateTag().putInt("Index", newIndex);
    }

    public static void adjustPageIndex(ItemStack stack, int amount) {
        int currentIndex = getPageIndex(stack);
        setPageIndex(stack, currentIndex + amount);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && !player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(hand);

            this.triggerAnim(player, GeoItem.getId(stack), "controller", "open");
            this.isOpen = true;

            NetworkHooks.openScreen((ServerPlayer) player,
                    new SimpleMenuProvider(
                            (id, inv, p) -> new SpellBookMenu(id, inv, stack),
                            Component.literal("Spell Book")
                    ),
                    buf -> buf.writeItem(stack)
            );
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand)); // no swing animation
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();
        Level level = pContext.getLevel();
        ItemStack stack = pContext.getItemInHand();

        if (!level.isClientSide && player != null && player.isShiftKeyDown()) {
            IItemHandler bookInventory = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
            if (bookInventory == null) {
                return InteractionResult.FAIL;
            }

            int currentPage = getPageIndex(stack);
            ItemStack pageToUse = bookInventory.getStackInSlot(currentPage);

            if (!pageToUse.isEmpty()){
                BlockHitResult hitResult = new BlockHitResult(pContext.getClickLocation(), pContext.getClickedFace(), pContext.getClickedPos(), pContext.isInside());
                UseOnContext pageContext = new UseOnContext(level, player, pContext.getHand(), pageToUse, hitResult);

                InteractionResult result = pageToUse.getItem().useOn(pageContext);

                if (!level.isClientSide && result.consumesAction()) {
                    if (bookInventory instanceof ItemStackHandler handler) {
                        stack.getOrCreateTag().put("Inventory", handler.serializeNBT());
                    }
                }


                return result;
            }
        }
        return super.useOn(pContext);
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

        SpellBookItemStackHandler itemHandler = new SpellBookItemStackHandler(this.maxPages, ModTags.Items.SPELL_BOOK_PAPER_TAG);

        if (nbt != null && nbt.contains("Inventory")) {
            itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
        } else if (stack.hasTag() && stack.getTag().contains("Inventory")) {
            itemHandler.deserializeNBT(stack.getTag().getCompound("Inventory"));
        }


        return new ICapabilityProvider() {
            private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side){
                return cap == ForgeCapabilities.ITEM_HANDLER ? handler.cast() : LazyOptional.empty();
            }
        };
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

    @Override
    public @Nullable CompoundTag getShareTag(ItemStack stack) {
        CompoundTag nbt = super.getShareTag(stack);
        if(nbt == null) {
            nbt = new CompoundTag();
        }

            IItemHandler handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
            if (handler instanceof INBTSerializable<?> serializable) {
                INBTSerializable<CompoundTag> nbtSerializable = (INBTSerializable<CompoundTag>) serializable;
                nbt.put("Inventory", nbtSerializable.serializeNBT());
            }

        return nbt;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        super.readShareTag(stack, nbt);
        if (nbt != null && nbt.contains("Inventory")) {
            stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                if(handler instanceof INBTSerializable<?> serializable) {
                    INBTSerializable<CompoundTag> nbtSerializable = (INBTSerializable<CompoundTag>) serializable;
                    nbtSerializable.deserializeNBT(nbt.getCompound("Inventory"));
                }
            });
        }
    }
}
