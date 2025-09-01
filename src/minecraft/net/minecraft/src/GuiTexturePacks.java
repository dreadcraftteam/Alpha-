package net.minecraft.src;

import java.io.File;
import java.util.List;
import java.util.Random;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class GuiTexturePacks extends GuiScreen {

    protected GuiScreen parentScreen;
    private int scrollPosition = 0;
    private int topMargin = 32;
    private int bottomMargin = this.height - 55 + 4;
    private int leftEdge = 0;
    private int rightEdge = this.width;
    private int dragY = -2;
    private int texturePackUpdateTimer = -1;
    private String texturePacksFolder = "";
    private static final Random rand = new Random();
    private float updateCounter = 0.0F;

    public GuiTexturePacks(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    public void updateScreen() {
        super.updateScreen();
        --this.texturePackUpdateTimer;
        ++this.updateCounter;
    }

    public void initGui() {
        this.controlList.add(
            new GuiSmallButton(
                5,
                this.width / 2 - 154,
                this.height - 48,
                "Open texture pack folder"
            )
        );
        this.controlList.add(
            new GuiSmallButton(6, this.width / 2 + 4, this.height - 48, "Done")
        );
        this.mc.texturePackList.refreshTexturePacks();
        this.texturePacksFolder =
            this.mc.options.texturePackDir.getAbsolutePath();
        this.topMargin = 32;
        this.bottomMargin = this.height - 58 + 4;
        this.leftEdge = 0;
        this.rightEdge = this.width;
    }

    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 5) {
                Sys.openURL("file://" + this.texturePacksFolder);
            }

            if (button.id == 6) {
                this.mc.renderEngine.refreshTextures();
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseMovedOrUp(int mouseX, int mouseY, int mouseButton) {
        super.mouseMovedOrUp(mouseX, mouseY, mouseButton);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(partialTicks);
        if (this.texturePackUpdateTimer <= 0) {
            this.mc.texturePackList.refreshTexturePacks();
            this.texturePackUpdateTimer += 20;
        }

        List<TexturePackBase> texturePacks =
            this.mc.texturePackList.getTexturePacks();
        int maxScroll;
        if (Mouse.isButtonDown(0)) {
            if (this.dragY == -1) {
                if (mouseY >= this.topMargin && mouseY <= this.bottomMargin) {
                    int listLeft = this.width / 2 - 110;
                    int listRight = this.width / 2 + 110;
                    int clickedIndex =
                        (mouseY - this.topMargin + this.scrollPosition - 2) /
                        36;
                    if (
                        mouseX >= listLeft &&
                        mouseX <= listRight &&
                        clickedIndex >= 0 &&
                        clickedIndex < texturePacks.size() &&
                        this.mc.texturePackList.setTexturePack(
                            (TexturePackBase) texturePacks.get(clickedIndex)
                        )
                    ) {
                        this.mc.renderEngine.refreshTextures();
                    }

                    this.dragY = mouseY;
                } else {
                    this.dragY = -2;
                }
            } else if (this.dragY >= 0) {
                this.scrollPosition -= mouseY - this.dragY;
                this.dragY = mouseY;
            }
        } else {
            this.dragY = -1;
        }

        maxScroll =
            texturePacks.size() * 36 - (this.bottomMargin - this.topMargin - 4);
        if (maxScroll < 0) {
            maxScroll /= 2;
        }

        if (this.scrollPosition < 0) {
            this.scrollPosition = 0;
        }

        if (this.scrollPosition > maxScroll) {
            this.scrollPosition = maxScroll;
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator tessellator = Tessellator.instance;
        GL11.glBindTexture(
            GL11.GL_TEXTURE_2D,
            this.mc.renderEngine.getTexture("/gui/background.png")
        );
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float textureSize = 32.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(2105376);
        tessellator.addVertexWithUV(
            (double) this.leftEdge,
            (double) this.bottomMargin,
            0.0D,
            (double) ((float) this.leftEdge / textureSize),
            (double) ((float) (this.bottomMargin + this.scrollPosition) /
                textureSize)
        );
        tessellator.addVertexWithUV(
            (double) this.rightEdge,
            (double) this.bottomMargin,
            0.0D,
            (double) ((float) this.rightEdge / textureSize),
            (double) ((float) (this.bottomMargin + this.scrollPosition) /
                textureSize)
        );
        tessellator.addVertexWithUV(
            (double) this.rightEdge,
            (double) this.topMargin,
            0.0D,
            (double) ((float) this.rightEdge / textureSize),
            (double) ((float) (this.topMargin + this.scrollPosition) /
                textureSize)
        );
        tessellator.addVertexWithUV(
            (double) this.leftEdge,
            (double) this.topMargin,
            0.0D,
            (double) ((float) this.leftEdge / textureSize),
            (double) ((float) (this.topMargin + this.scrollPosition) /
                textureSize)
        );
        tessellator.draw();

        for (int i = 0; i < texturePacks.size(); ++i) {
            TexturePackBase texturePack = (TexturePackBase) texturePacks.get(i);
            int iconX = this.width / 2 - 92 - 16;
            int entryY = 36 + i * 36 - this.scrollPosition;
            byte iconHeight = 32;
            byte iconWidth = 32;
            if (texturePack == this.mc.texturePackList.selectedTexturePack) {
                int highlightLeft = this.width / 2 - 110;
                int highlightRight = this.width / 2 + 110;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                tessellator.startDrawingQuads();
                tessellator.setColorOpaque_I(8421504);
                tessellator.addVertexWithUV(
                    (double) highlightLeft,
                    (double) (entryY + iconHeight + 2),
                    0.0D,
                    0.0D,
                    1.0D
                );
                tessellator.addVertexWithUV(
                    (double) highlightRight,
                    (double) (entryY + iconHeight + 2),
                    0.0D,
                    1.0D,
                    1.0D
                );
                tessellator.addVertexWithUV(
                    (double) highlightRight,
                    (double) (entryY - 2),
                    0.0D,
                    1.0D,
                    0.0D
                );
                tessellator.addVertexWithUV(
                    (double) highlightLeft,
                    (double) (entryY - 2),
                    0.0D,
                    0.0D,
                    0.0D
                );
                tessellator.setColorOpaque_I(0);
                tessellator.addVertexWithUV(
                    (double) (highlightLeft + 1),
                    (double) (entryY + iconHeight + 1),
                    0.0D,
                    0.0D,
                    1.0D
                );
                tessellator.addVertexWithUV(
                    (double) (highlightRight - 1),
                    (double) (entryY + iconHeight + 1),
                    0.0D,
                    1.0D,
                    1.0D
                );
                tessellator.addVertexWithUV(
                    (double) (highlightRight - 1),
                    (double) (entryY - 1),
                    0.0D,
                    1.0D,
                    0.0D
                );
                tessellator.addVertexWithUV(
                    (double) (highlightLeft + 1),
                    (double) (entryY - 1),
                    0.0D,
                    0.0D,
                    0.0D
                );
                tessellator.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            texturePack.bindThumbnailTexture(this.mc);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_I(16777215);
            tessellator.addVertexWithUV(
                (double) iconX,
                (double) (entryY + iconHeight),
                0.0D,
                0.0D,
                1.0D
            );
            tessellator.addVertexWithUV(
                (double) (iconX + iconWidth),
                (double) (entryY + iconHeight),
                0.0D,
                1.0D,
                1.0D
            );
            tessellator.addVertexWithUV(
                (double) (iconX + iconWidth),
                (double) entryY,
                0.0D,
                1.0D,
                0.0D
            );
            tessellator.addVertexWithUV(
                (double) iconX,
                (double) entryY,
                0.0D,
                0.0D,
                0.0D
            );
            tessellator.draw();
            this.drawString(
                this.fontRenderer,
                texturePack.texturePackFileName,
                iconX + iconWidth + 2,
                entryY + 1,
                16777215
            );
            this.drawString(
                this.fontRenderer,
                texturePack.firstDescriptionLine,
                iconX + iconWidth + 2,
                entryY + 12,
                8421504
            );
            this.drawString(
                this.fontRenderer,
                texturePack.secondDescriptionLine,
                iconX + iconWidth + 2,
                entryY + 12 + 10,
                8421504
            );
        }

        byte fadeHeight = 4;
        this.drawFadingRect(0, this.topMargin, 255, 255);
        this.drawFadingRect(this.bottomMargin, this.height, 255, 255);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(0, 0, 0, 0);
        tessellator.addVertexWithUV(
            (double) this.leftEdge,
            (double) (this.topMargin + fadeHeight),
            0.0D,
            0.0D,
            1.0D
        );
        tessellator.addVertexWithUV(
            (double) this.rightEdge,
            (double) (this.topMargin + fadeHeight),
            0.0D,
            1.0D,
            1.0D
        );
        tessellator.setColorRGBA(0, 0, 0, 255);
        tessellator.addVertexWithUV(
            (double) this.rightEdge,
            (double) this.topMargin,
            0.0D,
            1.0D,
            0.0D
        );
        tessellator.addVertexWithUV(
            (double) this.leftEdge,
            (double) this.topMargin,
            0.0D,
            0.0D,
            0.0D
        );
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(0, 0, 0, 255);
        tessellator.addVertexWithUV(
            (double) this.leftEdge,
            (double) this.bottomMargin,
            0.0D,
            0.0D,
            1.0D
        );
        tessellator.addVertexWithUV(
            (double) this.rightEdge,
            (double) this.bottomMargin,
            0.0D,
            1.0D,
            1.0D
        );
        tessellator.setColorRGBA(0, 0, 0, 0);
        tessellator.addVertexWithUV(
            (double) this.rightEdge,
            (double) (this.bottomMargin - fadeHeight),
            0.0D,
            1.0D,
            0.0D
        );
        tessellator.addVertexWithUV(
            (double) this.leftEdge,
            (double) (this.bottomMargin - fadeHeight),
            0.0D,
            0.0D,
            0.0D
        );
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        this.drawCenteredString(
            this.fontRenderer,
            "Select Texture Pack",
            this.width / 2,
            16,
            16777215
        );
        this.drawCenteredString(
            this.fontRenderer,
            "(Place texture pack files here)",
            this.width / 2 - 77,
            this.height - 26,
            8421504
        );
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawBackground(float partialTicks) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glBindTexture(
            GL11.GL_TEXTURE_2D,
            this.mc.renderEngine.getTexture("/gui/background.png")
        );
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        float textureSize = 32.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(4210752);
        tessellator.addVertexWithUV(
            0.0D,
            this.height,
            0.0D,
            0.0D,
            (double) ((float) this.height / textureSize)
        );
        tessellator.addVertexWithUV(
            this.width,
            this.height,
            0.0D,
            (double) ((float) this.width / textureSize),
            (double) ((float) this.height / textureSize)
        );
        tessellator.addVertexWithUV(
            this.width,
            0.0D,
            0.0D,
            (double) ((float) this.width / textureSize),
            0.0D
        );
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        tessellator.draw();

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void drawFadingRect(
        int top,
        int bottom,
        int topAlpha,
        int bottomAlpha
    ) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glBindTexture(
            GL11.GL_TEXTURE_2D,
            this.mc.renderEngine.getTexture("/gui/background.png")
        );
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float textureSize = 32.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(64, 64, 64, bottomAlpha);
        tessellator.addVertexWithUV(
            0.0D,
            (double) bottom,
            0.0D,
            0.0D,
            (double) ((float) bottom / textureSize)
        );
        tessellator.addVertexWithUV(
            (double) this.width,
            (double) bottom,
            0.0D,
            (double) ((float) this.width / textureSize),
            (double) ((float) bottom / textureSize)
        );
        tessellator.setColorRGBA(64, 64, 64, topAlpha);
        tessellator.addVertexWithUV(
            (double) this.width,
            (double) top,
            0.0D,
            (double) ((float) this.width / textureSize),
            (double) ((float) top / textureSize)
        );
        tessellator.addVertexWithUV(
            0.0D,
            (double) top,
            0.0D,
            0.0D,
            (double) ((float) top / textureSize)
        );
        tessellator.draw();
    }
}
