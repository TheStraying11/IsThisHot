package uk.studiolucia.isthishot;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
}
