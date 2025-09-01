package net.minecraft.src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class TexturePackList {

    private List<TexturePackBase> availableTexturePacks = new ArrayList<>();
    private TexturePackBase defaultTexturePack = new TexturePackDefault();
    public TexturePackBase selectedTexturePack;
    private Map<String, TexturePackBase> texturePackCache = new HashMap<>();
    private Minecraft mc;
    private File texturePacksDir;
    private String currentTexturePackName;

    public TexturePackList(Minecraft minecraft, File mcDataDir) {
        this.mc = minecraft;
        this.texturePacksDir = new File(mcDataDir, "texturepacks");
        if (!this.texturePacksDir.exists()) {
            this.texturePacksDir.mkdirs();
        }

        this.currentTexturePackName = minecraft.options.currentTexturePack;
        this.refreshTexturePacks();
        this.selectedTexturePack.closeTexturePackFile();
    }

    public boolean setTexturePack(TexturePackBase texturePack) {
        if (texturePack == this.selectedTexturePack) {
            return false;
        } else {
            this.selectedTexturePack.onTexturePackUnloaded(this.mc);
            this.currentTexturePackName = texturePack.texturePackFileName;
            this.selectedTexturePack = texturePack;
            this.mc.options.currentTexturePack = this.currentTexturePackName;
            this.mc.options.saveOptions();
            this.selectedTexturePack.closeTexturePackFile();
            return true;
        }
    }

    public void refreshTexturePacks() {
        ArrayList<TexturePackBase> newTexturePacks = new ArrayList<>();
        this.selectedTexturePack = null;
        newTexturePacks.add(this.defaultTexturePack);

        if (
            this.texturePacksDir.exists() && this.texturePacksDir.isDirectory()
        ) {
            File[] files = this.texturePacksDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (
                        file.isFile() &&
                        file.getName().toLowerCase().endsWith(".zip")
                    ) {
                        String fileKey =
                            file.getName() +
                            ":" +
                            file.length() +
                            ":" +
                            file.lastModified();

                        try {
                            if (!this.texturePackCache.containsKey(fileKey)) {
                                TexturePackCustom customPack =
                                    new TexturePackCustom(file);
                                customPack.field_6488_d = fileKey;
                                this.texturePackCache.put(fileKey, customPack);
                                customPack.loadTexturePack(this.mc);
                            }

                            TexturePackBase texturePack =
                                this.texturePackCache.get(fileKey);
                            if (
                                texturePack.texturePackFileName.equals(
                                    this.currentTexturePackName
                                )
                            ) {
                                this.selectedTexturePack = texturePack;
                            }

                            newTexturePacks.add(texturePack);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if (this.selectedTexturePack == null) {
            this.selectedTexturePack = this.defaultTexturePack;
        }

        this.availableTexturePacks.removeAll(newTexturePacks);
        Iterator<TexturePackBase> iterator =
            this.availableTexturePacks.iterator();

        while (iterator.hasNext()) {
            TexturePackBase oldPack = iterator.next();
            oldPack.onTexturePackUnloaded(this.mc);
            this.texturePackCache.remove(oldPack.field_6488_d);
        }

        this.availableTexturePacks = newTexturePacks;
    }

    public List<TexturePackBase> getTexturePacks() {
        return new ArrayList<>(this.availableTexturePacks);
    }
}
