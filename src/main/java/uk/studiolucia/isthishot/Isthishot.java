package uk.studiolucia.isthishot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

import java.util.Collection;
import java.util.Map;
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
    }

    private void clientSide() {
        LOGGER.debug("[IsThisHot]: Running On Client");

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        final Collection<Fluid> values = ForgeRegistries.FLUIDS.getValues();

        for (Fluid fluid : values) {
            String name = Objects.requireNonNull(fluid.getRegistryName()).toString();
            if (!name.equals("minecraft:empty")) {
                int temperature = fluid.getAttributes().getTemperature();
                LOGGER.info("Added temperature data to fluid: " + name + " (" + temperature + "K)");
                // TODO: figure out how to actually get an instance of TooltipLines (need Player and TooltipFlags Instances)
                for (Object tag : fluid.getBucket().getTags().toArray()) {
                    LOGGER.info("[IsThisHot]: "+tag);
                }
                // fluid.getBucket().getDefaultInstance().getTooltipLines(Player, TooltipFlags).add("Temperature: "+temperature+"K");
            }
        }
    }

    private void serverSide() {
        LOGGER.debug("[IsThisHot]: Running on Server");
        LOGGER.warn("[IsThisHot]: Warning: IsThisHot is client side only, remove it from your server's mod folder");
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("[IsThisHot]: Loaded Version "+net.minecraftforge.fml.ModList.get().getModFileById("isthishot").versionString());
        if (FMLEnvironment.dist == Dist.CLIENT) {
            clientSide();
        }
        else {
            serverSide();
        }
    }
}
