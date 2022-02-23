package uk.studiolucia.isthishot;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("isthishot")
public class Isthishot {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public Isthishot() {
        // Letting forge know that we're client-side only
        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true
                )
        );

        if (FMLEnvironment.dist == Dist.CLIENT) {
            clientSide();
        }
        else {
            serverSide();
        }
    }

    private void clientSide() {
        LOGGER.debug("[IsThisHot]: Running On Client");

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void serverSide() {
        LOGGER.debug("[IsThisHot]: Running on Server");
        LOGGER.warn("[IsThisHot]: Warning: IsThisHot is client side only, remove it from your server's mod folder");
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("[IsThisHot]: Loaded Version "+net.minecraftforge.fml.ModList.get().getModFileById("isthishot").versionString());
    }

    @SubscribeEvent
    public void ItemTooltipEvent(final ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        List<Component> list = event.getToolTip();

        itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(fluidContainer -> {
            if (fluidContainer.getTanks() > 1) {
                for (int i = 1; i <= fluidContainer.getTanks(); i++) {
                    FluidStack fluid = fluidContainer.getFluidInTank(i);
                    if (!fluid.isEmpty()) {
                        int temperature = fluid.getFluid().getAttributes().getTemperature();

                        list.add(new TextComponent("Temperature of " + fluidContainer.getFluidInTank(i).getDisplayName() + ": " + temperature + "K"));
                    }
                }
            } else {
                FluidStack fluid = fluidContainer.getFluidInTank(1);
                if (!fluid.isEmpty()) {
                    int temperature = fluid.getFluid().getAttributes().getTemperature();

                    list.add(new TextComponent("Temperature: " + temperature + "K"));
                }
            }
        });
    }
}
