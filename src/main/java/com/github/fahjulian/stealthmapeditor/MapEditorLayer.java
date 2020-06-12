package com.github.fahjulian.stealthmapeditor;

import com.github.fahjulian.stealth.core.scene.AbstractLayer;
import com.github.fahjulian.stealth.core.scene.Camera;
import com.github.fahjulian.stealth.core.util.Log;
import com.github.fahjulian.stealth.events.application.RenderEvent;
import com.github.fahjulian.stealth.events.application.UpdateEvent;
import com.github.fahjulian.stealth.events.key.KeyPressedEvent;
import com.github.fahjulian.stealth.events.key.KeyReleasedEvent;
import com.github.fahjulian.stealth.events.mouse.AMouseEvent.Button;
import com.github.fahjulian.stealth.events.mouse.MouseButtonPressedEvent;
import com.github.fahjulian.stealth.graphics.Renderer2D;
import com.github.fahjulian.stealth.graphics.opengl.Texture2D;

import org.joml.Vector2f;

public class MapEditorLayer extends AbstractLayer
{
    private static final float CAMERA_SPEED = 250.0f;

    private final int width, height;
    private final float tileSize;
    private final TexturedRect[] tiles;
    private final Vector2f cameraVelocity;
    private final MapEditorScene scene;

    private TexturedRect selectedTile;

    MapEditorLayer(int width, int height, float tileSize, MapEditorScene scene)
    {
        this.scene = scene;
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.tiles = new TexturedRect[width * height];
        this.cameraVelocity = new Vector2f();
        this.selectedTile = null;
    }

    @Override
    protected void onInit()
    {
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                tiles[x + y * width] = new TexturedRect(x * tileSize, y * tileSize, tileSize, tileSize,
                        scene.defaultTexture);
            }
        }

        registerEventListener(RenderEvent.class, this::onRender);
        registerEventListener(KeyPressedEvent.class, this::onKeyPressed);
        registerEventListener(KeyReleasedEvent.class, this::onKeyReleased);
        registerEventListener(UpdateEvent.class, this::onUpdate);
        registerEventListener(MouseButtonPressedEvent.class, this::onMouseButtonPressed);
    }

    private void onUpdate(UpdateEvent event)
    {
        Camera camera = scene.getCamera();
        camera.setPosition(camera.getPositionX() + cameraVelocity.x * event.getDeltaSeconds(),
                camera.getPositionY() + cameraVelocity.y * event.getDeltaSeconds());
    }

    private void onRender(RenderEvent event)
    {
        for (TexturedRect tile : tiles)
            Renderer2D.drawRectangle(tile.x, tile.y, tile.width, tile.height, tile.texture);
    }

    private void onKeyPressed(KeyPressedEvent event)
    {
        switch (event.getKey())
        {
        case W:
            cameraVelocity.y += CAMERA_SPEED;
            break;
        case A:
            cameraVelocity.x -= CAMERA_SPEED;
            break;
        case S:
            cameraVelocity.y -= CAMERA_SPEED;
            break;
        case D:
            cameraVelocity.x += CAMERA_SPEED;
            break;
        case SPACE:
            if (selectedTile != null)
            {
                Texture2D nextTexture = scene.nextTexture(selectedTile.texture);
                if (nextTexture != null)
                    selectedTile.texture = nextTexture;
                else
                    selectedTile.texture = scene.defaultTexture;
                Log.info("Switched texture of some tile to %s", selectedTile.texture);
            }
        default:
            break;
        }
    }

    private void onKeyReleased(KeyReleasedEvent event)
    {
        switch (event.getKey())
        {
        case W:
            cameraVelocity.y -= CAMERA_SPEED;
            break;
        case A:
            cameraVelocity.x += CAMERA_SPEED;
            break;
        case S:
            cameraVelocity.y += CAMERA_SPEED;
            break;
        case D:
            cameraVelocity.x -= CAMERA_SPEED;
            break;
        default:
            break;
        }
    }

    private void onMouseButtonPressed(MouseButtonPressedEvent event)
    {
        if (event.getButton() == Button.LEFT)
            selectedTile = getTile(event.getX(), event.getY());

        Log.info("Selected tile at (%d, %d)", (int) (event.getX() / tileSize), (int) (event.getY() / tileSize));
    }

    private TexturedRect getTile(float x, float y)
    {
        if (x < 0 || y < 0 || x > (width * tileSize) || y > (height * tileSize))
            return null;

        return tiles[(int) (x / tileSize) + (int) (y / tileSize) * width];
    }
}

class TexturedRect
{
    final float x, y;
    final float width, height;
    Texture2D texture;

    TexturedRect(float x, float y, float width, float height, Texture2D texture)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }
}
