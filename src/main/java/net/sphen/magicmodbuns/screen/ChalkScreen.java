package net.sphen.magicmodbuns.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.screen.elements.Dot;
import net.sphen.magicmodbuns.screen.elements.Line;

import java.util.ArrayList;
import java.util.List;

public class ChalkScreen extends AbstractContainerScreen<ChalkMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(MagicMod.MODID, "textures/gui/chalk_screen.png");
    private static final int GRID_SIZE = 5;
    private static final int DOT_SIZE = 10;
    private static final int SPACING = 28; //28 for chalk_screen.png
    private int GRID_X;
    private int GRID_Y;

    private List<Line> lines = new ArrayList<>();
    private Dot selectedDot = null;
    private Dot hoveredDot = null;

    public ChalkScreen(ChalkMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle != null ? pTitle : Component.literal("Chalk Menu"));
        System.out.println("chalkScreen opened!");
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        int gridWidth = GRID_SIZE * (DOT_SIZE + SPACING) - SPACING;
        int gridHeight = GRID_SIZE * (DOT_SIZE + SPACING) - SPACING;

        GRID_X = (this.width - gridWidth) / 2;
        GRID_Y = (this.height - gridHeight) / 2;

        GRID_X -= 1;
        GRID_Y += 4;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        // Draw the texture (adjust the coordinates)
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight + 9);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderBg(guiGraphics, pPartialTick, pMouseX, pMouseY);

        drawLines(guiGraphics);

        // Draw hover effect
        if (hoveredDot != null) {
            int x = GRID_X + hoveredDot.gridX * (DOT_SIZE + SPACING);
            int y = GRID_Y + hoveredDot.gridY * (DOT_SIZE + SPACING);
            drawCircle(guiGraphics, x + DOT_SIZE / 2, y + DOT_SIZE / 2, DOT_SIZE / 2 + 2, 0x3b3b3b3b); // outline
        }

        // Draw highlight if a dot is selected
        if (selectedDot != null) {
            int x = GRID_X + selectedDot.gridX * (DOT_SIZE + SPACING);
            int y = GRID_Y + selectedDot.gridY * (DOT_SIZE + SPACING);
            drawCircle(guiGraphics, x + DOT_SIZE / 2, y + DOT_SIZE / 2, DOT_SIZE / 2 + 2, 0x3b3b3b3b); // outline
        }

        drawGrid(guiGraphics);
    }

    private void drawGrid(GuiGraphics guiGraphics) {
        //generating grid pattern (h=horizontal, v=vertical)
        for (int h = 0; h < GRID_SIZE; h++) {
            for (int v = 0; v < GRID_SIZE; v++) {
                int x = GRID_X + h * (DOT_SIZE + SPACING);
                int y = GRID_Y + v * (DOT_SIZE + SPACING);

                //System.out.println("Dot (" + i + "," + j + ") at screen pos: (" + x + "," + y + ")");

                drawCircle(guiGraphics, x + DOT_SIZE / 2, y + DOT_SIZE / 2, DOT_SIZE / 2, 0xFFFFFFFF);
            }
        }
    }

    private void drawCircle(GuiGraphics guiGraphics, int centerX, int centerY, int radius, int color) {
        // Draw a circle by approximating it with filled pixels
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (x * x + y * y <= radius * radius) {  // Check if the point (x, y) is within the circle's radius
                    guiGraphics.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);  // Draw a small square for each point within the circle
                }
            }
        }
    }

    private void drawLines(GuiGraphics guiGraphics) {
        for (Line line : lines) {
            int startX = GRID_X + (line.start.gridX * (DOT_SIZE + SPACING)) + DOT_SIZE / 2;
            int startY = GRID_Y + (line.start.gridY * (DOT_SIZE + SPACING)) + DOT_SIZE / 2;
            int endX = GRID_X + (line.end.gridX * (DOT_SIZE + SPACING)) + DOT_SIZE / 2;
            int endY = GRID_Y + (line.end.gridY * (DOT_SIZE + SPACING)) + DOT_SIZE / 2;

            //System.out.println("Drawing line from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ")");

            drawLine(guiGraphics, startX, startY, endX, endY, 0xFFFF0000);
        }
    }


    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int x = GRID_X + i * (DOT_SIZE + SPACING);
                int y = GRID_Y + j * (DOT_SIZE + SPACING);
                int centerX = x + DOT_SIZE / 2;
                int centerY = y + DOT_SIZE / 2;
                int radius = DOT_SIZE / 2;

                double distance = Math.sqrt(Math.pow(pMouseX - centerX, 2) + Math.pow(pMouseY - centerY, 2));

                if (distance <= radius) {
                    Dot clickedDot = new Dot(i, j);
                    System.out.println("Clicked Dot: (" + i + ", " + j + ")");

                    if (selectedDot == null) {

                        selectedDot = clickedDot;

                        System.out.println("Selected Dot: (" + selectedDot.gridX + ", " + selectedDot.gridY + ")");
                    } else {

                        System.out.println("Trying to connect: (" + selectedDot.gridX + ", " + selectedDot.gridY + ") to (" + clickedDot.gridX + ", " + clickedDot.gridY + ")");

                        // Check: Only allow connections to adjacent dots
                        if (isAdjacent(selectedDot, clickedDot)) {
                            lines.add(new Line(selectedDot, clickedDot));

                            System.out.println("Line stored! Total lines: " + lines.size());
                        } else {
                            System.out.println("Invalid connection. Dots are too far.");
                        }

                        selectedDot = null;
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        hoveredDot = null;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int x = GRID_X + i * (DOT_SIZE + SPACING);
                int y = GRID_Y + j * (DOT_SIZE + SPACING);
                int centerX = x + DOT_SIZE / 2;
                int centerY = y + DOT_SIZE / 2;
                int radius = DOT_SIZE / 2;

                double distance = Math.sqrt(Math.pow(pMouseX - centerX, 2) + Math.pow(pMouseY - centerY, 2));

                if (distance <= radius) {
                    hoveredDot = new Dot(i, j);
                    break;
                }
            }
        }
    }

    private void drawLine(GuiGraphics guiGraphics, int startX, int startY, int endX, int endY, int color) {
        int thickness = 2; // Adjust thickness if needed
        if (startX == endX) {
            // Vertical line
            guiGraphics.fill(startX - thickness / 2, startY, startX + thickness / 2, endY, color);
        } else if (startY == endY) {
            // Horizontal line
            guiGraphics.fill(startX, startY - thickness / 2, endX, startY + thickness / 2, color);
        } else {
            // Diagonal or general case
            int steps = Math.max(Math.abs(endX - startX), Math.abs(endY - startY));
            for (int i = 0; i <= steps; i++) {
                int x = startX + i * (endX - startX) / steps;
                int y = startY + i * (endY - startY) / steps;
                guiGraphics.fill(x, y, x + thickness, y + thickness, color);
            }
        }
    }

    private boolean isAdjacent(Dot dot1, Dot dot2) {
        int dx = Math.abs(dot1.gridX - dot2.gridX);
        int dy = Math.abs(dot1.gridY - dot2.gridY);
        return (dx <= 1 && dy <= 1) && (dx + dy > 0); // Allow adjacent and diagonal, but not same dot
    }
}