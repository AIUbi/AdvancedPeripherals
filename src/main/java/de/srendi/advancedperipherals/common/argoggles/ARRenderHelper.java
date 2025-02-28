package de.srendi.advancedperipherals.common.argoggles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ARRenderHelper extends GuiComponent {
    private static ARRenderHelper instance = new ARRenderHelper();

    public static void drawRightboundString(PoseStack matrixStack, Font fontRenderer, String text, int x,
                                            int y, int color) {
        drawString(matrixStack, fontRenderer, text, x - fontRenderer.width(text), y, color);
    }

    public static ARRenderHelper getInstance() {
        return instance;
    }

    public static int fixAlpha(int color) {
        return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
    }

    @Override
    public void hLine(PoseStack matrixStack, int minX, int maxX, int y, int color) {
        color = ARRenderHelper.fixAlpha(color);
        super.hLine(matrixStack, minX, maxX, y, color);
    }

    @Override
    public void vLine(PoseStack matrixStack, int x, int minY, int maxY, int color) {
        color = ARRenderHelper.fixAlpha(color);
        super.vLine(matrixStack, x, minY, maxY, color);
    }

    @Override
    protected void fillGradient(PoseStack matrixStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
        colorFrom = ARRenderHelper.fixAlpha(colorFrom);
        colorTo = ARRenderHelper.fixAlpha(colorTo);
        super.fillGradient(matrixStack, x1, y1, x2, y2, colorFrom, colorTo);
    }

    public void drawCircle(PoseStack matrixStack, int centerX, int centerY, float radius, int color) {
        color = fixAlpha(color);

        final int n_segments = 360;
        Matrix4f matrix = matrixStack.last().pose();

        float z = this.getBlitOffset();
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < n_segments; i++) {
            double angle = 2 * Math.PI * i / n_segments;
            float xd = (float) (radius * Math.sin(angle));
            float yd = (float) (radius * Math.cos(angle));
            bufferbuilder.vertex(matrix, centerX + xd, centerY + yd, z).color(r, g, b, a).endVertex();
        }
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public void fillCircle(PoseStack matrixStack, int centerX, int centerY, float radius, int color) {
        color = fixAlpha(color);

        final float increment = 0.5f;
        Matrix4f matrix = matrixStack.last().pose();

        float z = this.getBlitOffset();
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
        for (float y = centerY - radius; y < centerY + radius; y += increment) {
            float yd = centerY - y;
            float xd = (float) Math.sqrt(Math.pow(radius, 2) - Math.pow(yd, 2));
            bufferbuilder.vertex(matrix, centerX - xd, y, z).color(r, g, b, a).endVertex();
            bufferbuilder.vertex(matrix, centerX + xd, y, z).color(r, g, b, a).endVertex();
        }
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public void drawItemIcon(PoseStack matrixStack, ItemRenderer itemRenderer, String item, int x, int y) {
        ItemStack stack = new ItemStack(ItemUtil.getRegistryEntry(item, ForgeRegistries.ITEMS));
        itemRenderer.renderGuiItem(stack, x, y);
    }
}
