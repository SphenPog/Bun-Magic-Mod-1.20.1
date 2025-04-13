package net.sphen.magicmodbuns.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PatternTextureLoader {

    public static ResourceLocation loadGeneratedTexture(String filename) {
        File file = new File(FMLPaths.GAMEDIR.get().resolve("generated_textures").toFile(), filename + ".png");

        if (!file.exists()) {
            System.err.println("Texture file does not exist: " + file.getAbsolutePath() + ".png");
            return null;
        } else {
            System.out.println("passed first check in loadGeneratedTexture");
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            NativeImage nativeImage = NativeImage.read(inputStream);
            DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);

            ResourceLocation textureLocation = new ResourceLocation("magicmodbuns", "generated_textures/" + filename);
            Minecraft.getInstance().getTextureManager().register(textureLocation, dynamicTexture);

            return textureLocation;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
