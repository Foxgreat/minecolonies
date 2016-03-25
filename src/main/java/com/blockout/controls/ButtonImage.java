package com.blockout.controls;

import com.blockout.Alignment;
import com.blockout.PaneParams;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Iterator;

public class ButtonImage extends Button
{
    private static final ResourceLocation soundClick = new ResourceLocation("gui.button.press");
    protected ResourceLocation image;
    protected ResourceLocation imageHighlight;
    protected ResourceLocation imageDisabled;

    protected int       imageOffsetX = 0, imageOffsetY = 0, imageWidth = 0, imageHeight = 0;
    protected int       highlightOffsetX = 0, highlightOffsetY = 0, highlightWidth = 0, highlightHeight = 0;
    protected int       disabledOffsetX = 0, disabledOffsetY = 0, disabledWidth = 0, disabledHeight = 0;
    protected float     textScale         = 1.0f;
    protected Alignment textAlignment     = Alignment.Middle;
    protected int       textColor         = 0xffffff;
    protected int       textHoverColor    = 0xffffff;
    protected int       textDisabledColor = 0xffffff;
    protected boolean   shadow            = false;
    protected int       textOffsetX = 0, textOffsetY = 0;

    public ButtonImage()
    {
        setSize(20, 20);
    }

    public ButtonImage(PaneParams params)
    {
        super(params);

        String path = params.getStringAttribute("source", null);
        if (path != null)
        {
            image = new ResourceLocation(path);
        }

        PaneParams.SizePair size = params.getSizePairAttribute("imageoffset", null, null);
        if (size != null)
        {
            imageOffsetX = size.x;
            imageOffsetY = size.y;
        }

        size = params.getSizePairAttribute("imagesize", null, null);
        if (size != null)
        {
            imageWidth = size.x;
            imageHeight = size.y;
        }

        path = params.getStringAttribute("highlight", null);
        if (path != null)
        {
            imageHighlight = new ResourceLocation(path);
        }

        size = params.getSizePairAttribute("highlightoffset", null, null);
        if (size != null)
        {
            highlightOffsetX = size.x;
            highlightOffsetY = size.y;
        }

        size = params.getSizePairAttribute("highlightsize", null, null);
        if (size != null)
        {
            highlightWidth = size.x;
            highlightHeight = size.y;
        }

        path = params.getStringAttribute("disabled", null);
        if (path != null)
        {
            imageDisabled = new ResourceLocation(path);
        }

        size = params.getSizePairAttribute("disabledoffset", null, null);
        if (size != null)
        {
            disabledOffsetX = size.x;
            disabledOffsetY = size.y;
        }

        size = params.getSizePairAttribute("disabledsize", null, null);
        if (size != null)
        {
            disabledWidth = size.x;
            disabledHeight = size.y;
        }

        textScale         = params.getFloatAttribute("scale", textScale);
        textAlignment     = params.getEnumAttribute("textalign", textAlignment);
        textColor = params.getColorAttribute("textcolor", textColor);
        textHoverColor    = params.getColorAttribute("texthovercolor", textColor); //  match textcolor by default
        textDisabledColor = params.getColorAttribute("textdisabledcolor", textColor); //  match textcolor by default
        shadow            = params.getBooleanAttribute("shadow", shadow);

        size = params.getSizePairAttribute("textoffset", null, null);
        if (size != null)
        {
            textOffsetX = size.x;
            textOffsetY = size.y;
        }
    }

    public void setImage(String source)
    {
        setImage(source, 0, 0, 0, 0);
    }

    public void setImage(String source, int offsetX, int offsetY, int w, int h)
    {
        setImage(source != null ? new ResourceLocation(source) : null, offsetX, offsetY, w, h);
    }

    public void setImage(ResourceLocation loc)
    {
        setImage(loc, 0, 0, 0, 0);
    }

    public void setImage(ResourceLocation loc, int offsetX, int offsetY, int w, int h)
    {
        image = loc;
        imageOffsetX = offsetX;
        imageOffsetY = offsetY;
        imageHeight = w;
        imageWidth = h;
    }

    public void setImageHighlight(String source)
    {
        setImageHighlight(source, 0, 0, 0, 0);
    }

    public void setImageHighlight(String source, int offsetX, int offsetY, int w, int h)
    {
        setImageHighlight(source != null ? new ResourceLocation(source) : null, offsetX, offsetY, w, h);
    }

    public void setImageHighlight(ResourceLocation loc)
    {
        setImageHighlight(loc, 0, 0, 0, 0);
    }

    public void setImageHighlight(ResourceLocation loc, int offsetX, int offsetY, int w, int h)
    {
        imageHighlight = loc;
        highlightOffsetX = offsetX;
        highlightOffsetY = offsetY;
        highlightHeight = w;
        highlightWidth = h;
    }

    public int getTextColor() { return textColor; }
    public int getTextHoverColor() { return textHoverColor; }
    public void setTextColor(int c) { setTextColor(c, c, c); }
    public void setTextColor(int c, int d, int h)
    {
        textColor = c;
        textDisabledColor = d;
        textHoverColor = h;
    }

    public boolean getShadow() { return shadow; }
    public void setShadow(boolean s) { shadow = s; }

    public Alignment getTextAlignment() { return textAlignment; }
    public void setTextAlignment(Alignment align) { textAlignment = align; }

    public float getTextScale() { return textScale; }
    public void setTextScale(float s) { textScale = s; }

    public int getTextHeight() { return (int)(mc.fontRenderer.FONT_HEIGHT * textScale); }
    public int getStringWidth() { return (int)(mc.fontRenderer.getStringWidth(label) * textScale); }

    @Override
    protected void drawSelf(int mx, int my)
    {
        ResourceLocation bind = image;
        int offsetX = imageOffsetX;
        int offsetY = imageOffsetY;
        int w = imageWidth;
        int h = imageHeight;

        boolean mouseOver = isPointInPane(mx, my);

        if (!enabled)
        {
            if (imageDisabled != null)
            {
                bind = imageDisabled;
                offsetX = disabledOffsetX;
                offsetY = disabledOffsetY;
                w = disabledWidth;
                h = disabledHeight;
            }
        }
        else if (mouseOver && imageHighlight != null)
        {
            bind = imageHighlight;
            offsetX = highlightOffsetX;
            offsetY = highlightOffsetY;
            w = highlightWidth;
            h = highlightHeight;
        }

        if (w == 0 || w > getWidth())   w = getWidth();
        if (h == 0 || h > getHeight())  h = getHeight();

        mc.renderEngine.bindTexture(bind);
        if (enabled || imageDisabled != null)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            GL11.glColor4f(0.5F, 0.5F, 0.5F, 1.0F);
        }

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        //Get file dimension
        int mapWidth = 256, mapHeight = 256;
        Iterator<ImageReader> it = ImageIO.getImageReadersBySuffix("png");
        if (it.hasNext()) {
            ImageReader reader = it.next();
            try (ImageInputStream stream = ImageIO.createImageInputStream(Minecraft.getMinecraft().getResourceManager().getResource(bind).getInputStream())) {
                reader.setInput(stream);
                mapWidth = reader.getWidth(reader.getMinIndex());
                mapHeight = reader.getHeight(reader.getMinIndex());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                reader.dispose();
            }
        }
        //Draw
        func_146110_a(x, y, offsetX, offsetY, w, h, mapWidth, mapHeight);

        //  Label, if any
        if (label != null)
        {
            int color = enabled ? (mouseOver ? textHoverColor : textColor) : textDisabledColor;

            offsetX = textOffsetX;
            offsetY = textOffsetY;

            if (textAlignment.rightAligned)
            {
                offsetX += (getWidth() - getStringWidth());
            }
            else if (textAlignment.horizontalCentered)
            {
                offsetX += (getWidth() - getStringWidth()) / 2;
            }

            if (textAlignment.bottomAligned)
            {
                offsetY += (getHeight() - getTextHeight());
            }
            else if (textAlignment.verticalCentered)
            {
                offsetY += (getHeight() - getTextHeight()) / 2;
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(textScale, textScale, textScale);
            mc.fontRenderer.drawString(label, getX() + offsetX, getY() + offsetY, color, shadow);
            GL11.glPopMatrix();
        }
    }

    @Override
    public void handleClick(int mx, int my)
    {
        mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(soundClick, 1.0F));
        super.handleClick(mx, my);
    }
}