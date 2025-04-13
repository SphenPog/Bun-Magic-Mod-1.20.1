package net.sphen.magicmodbuns.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.screen.elements.Dot;
import net.sphen.magicmodbuns.screen.elements.Line;
import net.sphen.magicmodbuns.screen.elements.PatternObject;
import net.sphen.magicmodbuns.util.PatternTextureGenerator;
import net.sphen.magicmodbuns.util.PatternTextureLoader;
import net.sphen.magicmodbuns.util.PlaceChalkPatternPacket;

import java.awt.image.BufferedImage;
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
    private boolean isDragging = false;
    private boolean isErasing = false;
    private int activeMouseButton = -1; // 0 = left, 1 = right, 2 = middle
    private Dot lastHoveredDot = null;
    private PatternObject patternObject = new PatternObject();

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

            drawLine(guiGraphics, startX, startY, endX, endY, 0xFFFF0000, line.curved);
        }
    }

    private void drawLine(GuiGraphics guiGraphics, int startX, int startY, int endX, int endY, int color, boolean curved) {
        int thickness = 2; // Adjust thickness if needed
        if (curved) {
            // Draw a curved line using the quadratic curve logic if the line is marked as curved
            drawCurvedLine(guiGraphics, startX, startY, endX, endY, color, thickness);
        } else {
            // Otherwise, draw a straight line
            if (startX == endX) {
                // Vertical line
                guiGraphics.fill(startX - thickness / 2, startY, startX + thickness / 2, endY, color);
            } else if (startY == endY) {
                // Horizontal line
                guiGraphics.fill(startX, startY - thickness / 2, endX, startY + thickness / 2, color);
            } else {
                int steps = Math.max(Math.abs(endX - startX), Math.abs(endY - startY));
                for (int i = 0; i <= steps; i++) {
                    int x = startX + i * (endX - startX) / steps;
                    int y = startY + i * (endY - startY) / steps;
                    guiGraphics.fill(x, y, x + thickness, y + thickness, color);
                }
            }
        }
    }

    private void drawCurvedLine(GuiGraphics guiGraphics, int startX, int startY, int endX, int endY, int color, int thickness) {
        float curveStrength = 1f; // <-- Adjust this to increase curve height

        // Midpoint
        int midX = (startX + endX) / 2;
        int midY = (startY + endY) / 2;

        // Perpendicular offset
        float dx = endY - startY;
        float dy = -(endX - startX);

        // Normalize the perpendicular vector
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        float offsetX = (dx / length) * curveStrength * length / 2;
        float offsetY = (dy / length) * curveStrength * length / 2;

        // Apply offset to midpoint
        float controlX = midX + offsetX;
        float controlY = midY + offsetY;

        for (float t = 0; t <= 1; t += 0.01f) {
            float x = (1 - t) * (1 - t) * startX + 2 * (1 - t) * t * controlX + t * t * endX;
            float y = (1 - t) * (1 - t) * startY + 2 * (1 - t) * t * controlY + t * t * endY;

            guiGraphics.fill((int) x, (int) y, (int) x + thickness, (int) y + thickness, color);
        }
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        isDragging = true;

        if (button == 0 || button == 2) { // Left click or middle click

            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    int x = GRID_X + i * (DOT_SIZE + SPACING);
                    int y = GRID_Y + j * (DOT_SIZE + SPACING);
                    int centerX = x + DOT_SIZE / 2;
                    int centerY = y + DOT_SIZE / 2;
                    int radius = DOT_SIZE / 2;

                    double distance = Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2));

                    if (distance <= radius) {
                        Dot hovered = new Dot(i, j);

                        if (selectedDot == null) {
                            selectedDot = hovered;
                            activeMouseButton = button;
                        } else if (!hovered.equals(selectedDot) && isAdjacent(selectedDot, hovered)) {
                            // Only add the line if not already connected
                            boolean isCurved = activeMouseButton == 2;
                            Line newLine = new Line(selectedDot, hovered, isCurved);

                            if (!lines.contains(newLine)) {
                                patternObject.addLine(selectedDot, hovered, isCurved);
                                lines.add(newLine);
                            }

                            selectedDot = hovered;
                        }
                        return true;
                    }
                }
            }
        } else if (button == 1) { // Right click - erasing
            isErasing = true;

            // Check if the mouse is near any existing line to erase
            for (int i = 0; i < lines.size(); i++) {
                Line line = lines.get(i);
                int startX = GRID_X + (line.start.gridX * (DOT_SIZE + SPACING)) + DOT_SIZE / 2;
                int startY = GRID_Y + (line.start.gridY * (DOT_SIZE + SPACING)) + DOT_SIZE / 2;
                int endX = GRID_X + (line.end.gridX * (DOT_SIZE + SPACING)) + DOT_SIZE / 2;
                int endY = GRID_Y + (line.end.gridY * (DOT_SIZE + SPACING)) + DOT_SIZE / 2;

                if (isMouseNearLine(mouseX, mouseY, startX, startY, endX, endY)) {
                    lines.remove(i);
                    patternObject.removeLine(line);
                    return true;
                }
            }
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 || button == 2) {
            isDragging = false;
            selectedDot = null;
            activeMouseButton = -1;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

        //Sets the dot clicked as hovered (for user experience)
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
                        activeMouseButton = pButton;

                        System.out.println("Selected Dot: (" + selectedDot.gridX + ", " + selectedDot.gridY + ")");
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
    

    private boolean isAdjacent(Dot dot1, Dot dot2) {
        int dx = Math.abs(dot1.gridX - dot2.gridX);
        int dy = Math.abs(dot1.gridY - dot2.gridY);
        return (dx <= 1 && dy <= 1) && (dx + dy > 0); // Allow adjacent and diagonal, but not same dot
    }

    private boolean isMouseNearLine(double mouseX, double mouseY, int startX, int startY, int endX, int endY) {
        double threshold = 5.0; // Set a distance threshold for "near" (tune as needed)

        // Check distance from line segment
        double distToLine = distanceFromLine(mouseX, mouseY, startX, startY, endX, endY);
        return distToLine < threshold;
    }

    private double distanceFromLine(double x, double y, int x1, int y1, int x2, int y2) {
        double A = x - x1;
        double B = y - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1.0;

        if (len_sq != 0) { // In case of a non-zero length line segment
            param = dot / len_sq;
        }

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = x - xx;
        double dy = y - yy;

        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void onClose() {
        super.onClose();

        if (minecraft != null && minecraft.player != null) {
            Player player = minecraft.player;

            //find block placement position
            BlockHitResult hitResult = (BlockHitResult) player.pick(5, 0, false);
            BlockPos placePos = hitResult.getBlockPos().relative(hitResult.getDirection());

            // Generate texture from pattern
            BufferedImage generatedImage = PatternTextureGenerator.generateBufferedImage(patternObject);
            String textureFileName = "pattern_" + placePos.getX() + "_" + placePos.getY() + "_" + placePos.getZ();

            PatternTextureGenerator.saveTextureToFile(generatedImage, textureFileName);
            PatternTextureLoader.loadGeneratedTexture(textureFileName);

            String patternData = patternObject.storeData();

            // Place the block with the stored pattern
            if (player.level().getBlockState(placePos).isAir()) {
                MagicMod.NETWORK.sendToServer(new PlaceChalkPatternPacket(placePos, patternData, "generated_textures/" + textureFileName + ".png"));
            }
        }
    }
}