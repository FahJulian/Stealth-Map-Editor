package com.github.fahjulian.stealthmapeditor;

import javax.swing.JOptionPane;

import com.github.fahjulian.stealth.core.Window;
import com.github.fahjulian.stealth.core.scene.AbstractLayer;
import com.github.fahjulian.stealth.events.application.RenderEvent;
import com.github.fahjulian.stealth.events.mouse.AMouseEvent.Button;
import com.github.fahjulian.stealth.events.mouse.MouseButtonPressedEvent;
import com.github.fahjulian.stealth.graphics.Color;
import com.github.fahjulian.stealth.graphics.Renderer2D;
import com.github.fahjulian.stealth.graphics.opengl.Texture2D;

public class MapEditorGUILayer extends AbstractLayer<MapEditorScene>
{
    private static final int COLS = 4;
    private static final float MARGIN = 50.0f;
    private static final float TEXTURE_PADDING = 10.0f;
    private static final float TEXTURE_SIZE = 50.0f;
    private static final float WIDTH = COLS * TEXTURE_SIZE + 2 * COLS * TEXTURE_PADDING;
    private static final float HEIGHT = Window.get().getHeight() - 2 * MARGIN;
    private static final float POS_X = Window.get().getWidth() - WIDTH - MARGIN;
    private static final float POS_Y = MARGIN;
    private static final float POS_Z = 2.0f;

    public MapEditorGUILayer(MapEditorScene scene)
    {
        super(scene);
    }

    @Override
    protected void onInit()
    {
        registerEventListener(RenderEvent.class, this::onRender);
        registerEventListener(MouseButtonPressedEvent.class, this::onMouseButonPressed);
    }

    private void onRender(RenderEvent event)
    {
        Renderer2D.drawStaticRectangle(POS_X, POS_Y, POS_Z, WIDTH, HEIGHT, Color.LIGHT_GREY);

        Texture2D[] textures = scene.getTextures();
        for (int i = 0; i < textures.length; i++)
        {
            Texture2D texture = textures[i];
            if (texture == null)
                break;

            float x = POS_X + TEXTURE_PADDING + (i % COLS) * (TEXTURE_SIZE + 2 * TEXTURE_PADDING);
            float y = Window.get().getHeight() - POS_Y - TEXTURE_PADDING - TEXTURE_SIZE
                    - (i / COLS) * (TEXTURE_SIZE + 2 * TEXTURE_PADDING);
            Renderer2D.drawStaticRectangle(x, y, POS_Z + 0.1f, TEXTURE_SIZE, TEXTURE_SIZE, texture);
        }
    }

    private void onMouseButonPressed(MouseButtonPressedEvent event)
    {
        if (!isOnPane(event.getX(), event.getY()))
            return;

        if (event.getButton() == Button.LEFT)
        {
            Texture2D texture = getTextureAt(event.getX(), event.getY());
            if (texture != null)
                new TextureSwitchEvent(texture);
        }
        else if (event.getButton() == Button.RIGHT)
        {
            String newTexture = JOptionPane.showInputDialog(null, "Enter (FULL!) path to new Texture");
            if (newTexture != null)
                scene.addTexture(new Texture2D(newTexture));
        }

        super.blockEvent(MouseButtonPressedEvent.class);
    }

    private boolean isOnPane(float x, float y)
    {
        return x > POS_X && x < POS_X + WIDTH && y > POS_Y && y < POS_Y + HEIGHT;
    }

    private Texture2D getTextureAt(float x, float y)
    {
        Texture2D[] textures = scene.getTextures();
        for (int i = 0; i < textures.length; i++)
        {
            float textureX = POS_X + TEXTURE_PADDING + (i % COLS) * (TEXTURE_SIZE + 2 * TEXTURE_PADDING);
            float textureY = Window.get().getHeight() - POS_Y - TEXTURE_PADDING - TEXTURE_SIZE
                    - (i / COLS) * (TEXTURE_SIZE + 2 * TEXTURE_PADDING);

            if (x > textureX && x < textureX + TEXTURE_SIZE && y > textureY && y < textureY + TEXTURE_SIZE)
                return textures[i];
        }

        return null;
    }
}
