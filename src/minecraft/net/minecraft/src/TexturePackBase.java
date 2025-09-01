package net.minecraft.src;

import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.Minecraft;

public abstract class TexturePackBase {

    public String texturePackFileName;
    public String firstDescriptionLine;
    public String secondDescriptionLine;
    public String field_6488_d;

    public void closeTexturePackFile() {}

    public void cleanup() {}

    public void loadTexturePack(Minecraft minecraft) throws IOException {}

    public void onTexturePackUnloaded(Minecraft minecraft) {}

    public void bindThumbnailTexture(Minecraft minecraft) {}

    public InputStream getResourceAsStream(String resourcePath) {
        return TexturePackBase.class.getResourceAsStream(resourcePath);
    }
}
