package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class TexturePackCustom extends TexturePackBase {

    private ZipFile texturePackZipFile;
    private int thumbnailTextureId = -1;
    private BufferedImage thumbnailImage;
    private File texturePackFile;

    public TexturePackCustom(File texturePackFile) {
        this.texturePackFileName = texturePackFile.getName();
        this.texturePackFile = texturePackFile;
    }

    private String truncateString(String text) {
        if (text != null && text.length() > 34) {
            text = text.substring(0, 34);
        }

        return text;
    }

    public void loadTexturePack(Minecraft minecraft) throws IOException {
        ZipFile zipFile = null;
        InputStream inputStream = null;

        try {
            zipFile = new ZipFile(this.texturePackFile);

            try {
                inputStream = zipFile.getInputStream(
                    zipFile.getEntry("pack.txt")
                );
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream)
                );
                this.firstDescriptionLine = this.truncateString(
                    reader.readLine()
                );
                this.secondDescriptionLine = this.truncateString(
                    reader.readLine()
                );
                reader.close();
                inputStream.close();
            } catch (Exception e) {}

            try {
                inputStream = zipFile.getInputStream(
                    zipFile.getEntry("pack.png")
                );
                this.thumbnailImage = ImageIO.read(inputStream);
                inputStream.close();
            } catch (Exception e) {}

            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {}

            try {
                if (zipFile != null) zipFile.close();
            } catch (Exception e) {}
        }
    }

    public void onTexturePackUnloaded(Minecraft minecraft) {
        if (this.thumbnailImage != null) {
            minecraft.renderEngine.deleteTexture(this.thumbnailTextureId);
        }

        this.cleanup();
    }

    public void bindThumbnailTexture(Minecraft minecraft) {
        if (this.thumbnailImage != null && this.thumbnailTextureId < 0) {
            GL11.glBindTexture(
                GL11.GL_TEXTURE_2D,
                minecraft.renderEngine.getTexture("/gui/unknown_pack.png")
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

    public void closeTexturePackFile() {
        this.cleanup();
        try {
            this.texturePackZipFile = new ZipFile(this.texturePackFile);
        } catch (Exception e) {}
    }

    public void cleanup() {
        try {
            if (this.texturePackZipFile != null) {
                this.texturePackZipFile.close();
            }
        } catch (Exception e) {}
        this.texturePackZipFile = null;
    }

    public InputStream getResourceAsStream(String resourcePath) {
        try {
            if (this.texturePackZipFile == null) {
                this.texturePackZipFile = new ZipFile(this.texturePackFile);
            }
            String pathInZip = resourcePath.startsWith("/")
                ? resourcePath.substring(1)
                : resourcePath;
            ZipEntry entry = this.texturePackZipFile.getEntry(pathInZip);
            if (entry != null) {
                System.out.println("loading texture pack: " + pathInZip);
                return this.texturePackZipFile.getInputStream(entry);
            } else {
                System.out.println("not found on texture pack: " + pathInZip);
            }
        } catch (Exception e) {
            System.out.println(
                "error loading texture pack: " + e.getMessage()
            );
            this.cleanup();
        }
        System.out.println("using default resource: " + resourcePath);
        return TexturePackBase.class.getResourceAsStream(resourcePath);
    }
}
