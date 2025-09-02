package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class TexturePackDefault extends TexturePackBase {

    private int thumbnailTextureId = -1;
    private BufferedImage thumbnailImage;

    public TexturePackDefault() {
        this.texturePackFileName = "Default";
        this.firstDescriptionLine = "The default look of Minecraft";

        try {
            this.thumbnailImage = ImageIO.read(
                TexturePackDefault.class.getResourceAsStream("/pack.png")
            );
        } catch (IOException e) {
            System.out.println("Failed to load default texture pack icon: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Default texture pack icon not found");
        }
    }

    public void onTexturePackUnloaded(Minecraft minecraft) {
        if (this.thumbnailImage != null) {
            minecraft.renderEngine.deleteTexture(this.thumbnailTextureId);
        }
    }

    public void bindThumbnailTexture(Minecraft minecraft) {
        if (this.thumbnailImage != null && this.thumbnailTextureId < 0) {
            this.thumbnailTextureId =
                minecraft.renderEngine.allocateAndSetupTexture(
                    this.thumbnailImage
                );
        }

        if (this.thumbnailImage != null) {
            minecraft.renderEngine.bindTexture(this.thumbnailTextureId);
        } else {
            GL11.glBindTexture(
                GL11.GL_TEXTURE_2D,
                minecraft.renderEngine.getTexture("/gui/unknown_pack.png")
            );
        }
    }
}
