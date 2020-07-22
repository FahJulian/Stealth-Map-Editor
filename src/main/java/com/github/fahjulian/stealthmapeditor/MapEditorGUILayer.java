package com.github.fahjulian.stealthmapeditor;

import java.util.List;

import com.github.fahjulian.stealth.core.Window;
import com.github.fahjulian.stealth.core.scene.AbstractLayer;
import com.github.fahjulian.stealth.events.application.RenderEvent;
import com.github.fahjulian.stealth.events.mouse.AMouseEvent.Button;
import com.github.fahjulian.stealth.events.mouse.MouseButtonPressedEvent;
import com.github.fahjulian.stealth.events.mouse.MouseScrolledEvent;
import com.github.fahjulian.stealth.graphics.Color;
import com.github.fahjulian.stealth.graphics.Sprite;
import com.github.fahjulian.stealth.graphics.Spritesheet;
import com.github.fahjulian.stealth.graphics.renderer.Renderer2D;

public class MapEditorGUILayer extends AbstractLayer<MapEditorScene>
{
    private enum State
    {
        SPRITESHEET_SELECTION, SPRITE_SELECTION;
    }

    private static final int COLS = 4;
    private static final float MARGIN = 50.0f;
    private static final float TEXTURE_PADDING = 10.0f;
    private static final float TEXTURE_SIZE = 50.0f;
    private static final float WIDTH = COLS * TEXTURE_SIZE + 2 * COLS * TEXTURE_PADDING;
    private static final float HEIGHT = Window.get().getHeight() - 2 * MARGIN;
    private static final float POS_X = Window.get().getWidth() - WIDTH - MARGIN;
    private static final float POS_Y = MARGIN;
    private static final float POS_Z = 2.0f;

    private final List<Spritesheet> spritesheets;
    private Spritesheet currentSpritesheet;
    private State state;
    private int scroll;

    public MapEditorGUILayer(MapEditorScene scene, List<Spritesheet> spritesheets)
    {
        super(scene);

        this.spritesheets = spritesheets;
        this.state = State.SPRITESHEET_SELECTION;
    }

    @Override
    protected void onInit()
    {
        super.registerEventListener(RenderEvent.class, this::onRender);
        super.registerEventListener(MouseButtonPressedEvent.class, this::onMouseButonPressed);
        super.registerEventListener(MouseScrolledEvent.class, this::onMouseScrolled);
    }

    private void onRender(RenderEvent event)
    {
        Renderer2D.drawStaticRectangle(POS_X, POS_Y, POS_Z, WIDTH, HEIGHT, Color.LIGHT_GREY);

        if (state == State.SPRITESHEET_SELECTION)
        {
            for (int i = 0; i < spritesheets.size(); i++)
            {
                Sprite sprite = new Sprite(spritesheets.get(i));

                float x = POS_X + TEXTURE_PADDING + (i % COLS) * (TEXTURE_SIZE + 2 * TEXTURE_PADDING);
                float y = Window.get().getHeight() - POS_Y - TEXTURE_PADDING - TEXTURE_SIZE
                        - (i / COLS) * (TEXTURE_SIZE + 2 * TEXTURE_PADDING);
                Renderer2D.drawStaticRectangle(x, y, POS_Z + 0.1f, TEXTURE_SIZE, TEXTURE_SIZE, sprite);
            }
        }
        else
        {
            int maxRows = (int) ((HEIGHT - TEXTURE_PADDING) / (TEXTURE_SIZE + 2 * TEXTURE_PADDING)) + 1;
            int maxSprites = COLS * maxRows;
            for (int i = 0; i < maxSprites
                    && i + scroll * COLS < currentSpritesheet.getHeight() * currentSpritesheet.getWidth(); i++)
            {
                int spriteNumber = i + scroll * COLS;
                Sprite sprite = currentSpritesheet.getSpriteAt(spriteNumber % currentSpritesheet.getWidth(),
                        spriteNumber / currentSpritesheet.getWidth());

                float posX = POS_X + TEXTURE_PADDING + (i % COLS) * (TEXTURE_SIZE + 2 * TEXTURE_PADDING);
                float posY = Window.get().getHeight() - POS_Y - TEXTURE_PADDING - TEXTURE_SIZE
                        - (i / COLS) * (TEXTURE_SIZE + 2 * TEXTURE_PADDING);

                Renderer2D.drawStaticRectangle(posX, posY, POS_Z + 0.1f, TEXTURE_SIZE, TEXTURE_SIZE, sprite);
            }
        }
    }

    private void onMouseButonPressed(MouseButtonPressedEvent event)
    {
        if (!isOnPane(event.getX(), event.getY()))
            return;

        if (event.getButton() == Button.LEFT)
        {
            if (state == State.SPRITESHEET_SELECTION)
            {
                currentSpritesheet = getSheetAt(event.getX(), event.getY());
                if (currentSpritesheet != null)
                    state = State.SPRITE_SELECTION;
            }
            else
            {
                Sprite sprite = getSpriteAt(event.getX(), event.getY());
                if (sprite != null)
                    new SpriteSwitchEvent(sprite);

                state = State.SPRITESHEET_SELECTION;
                currentSpritesheet = null;
            }

            super.blockEvent(MouseButtonPressedEvent.class);
        }
    }

    private boolean isOnPane(float x, float y)
    {
        return x > POS_X && x < POS_X + WIDTH && y > POS_Y && y < POS_Y + HEIGHT;
    }

    private Spritesheet getSheetAt(float x, float y)
    {
        for (int i = 0; i < spritesheets.size(); i++)
        {
            float textureX = POS_X + TEXTURE_PADDING + (i % COLS) * (TEXTURE_SIZE + 2 * TEXTURE_PADDING);
            float textureY = Window.get().getHeight() - POS_Y - TEXTURE_PADDING - TEXTURE_SIZE
                    - (i / COLS) * (TEXTURE_SIZE + 2 * TEXTURE_PADDING);

            if (x > textureX && x < textureX + TEXTURE_SIZE && y > textureY && y < textureY + TEXTURE_SIZE)
                return spritesheets.get(i);
        }

        return null;
    }

    private Sprite getSpriteAt(float x, float y)
    {
        int maxRows = (int) ((HEIGHT - TEXTURE_PADDING) / (TEXTURE_SIZE + 2 * TEXTURE_PADDING));
        int maxSprites = COLS * maxRows;
        for (int i = 0; i < maxSprites
                && i + scroll * COLS < currentSpritesheet.getHeight() * currentSpritesheet.getWidth(); i++)
        {
            int spriteNumber = i + scroll * COLS;
            Sprite sprite = currentSpritesheet.getSpriteAt(spriteNumber % currentSpritesheet.getWidth(),
                    spriteNumber / currentSpritesheet.getWidth());

            float posX = POS_X + TEXTURE_PADDING + (i % COLS) * (TEXTURE_SIZE + 2 * TEXTURE_PADDING);
            float posY = Window.get().getHeight() - POS_Y - TEXTURE_PADDING - TEXTURE_SIZE
                    - (i / COLS) * (TEXTURE_SIZE + 2 * TEXTURE_PADDING);

            if (x >= posX && x < posX + TEXTURE_SIZE && y >= posY && y < posY + TEXTURE_SIZE)
                return sprite;
        }

        return null;
    }

    private void onMouseScrolled(MouseScrolledEvent event)
    {
        scroll -= event.getScrollY();
        if (scroll < 0)
            scroll = 0;
    }
}
