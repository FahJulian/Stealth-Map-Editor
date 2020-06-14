package com.github.fahjulian.stealthmapeditor;

import javax.swing.JOptionPane;

import com.github.fahjulian.stealth.core.scene.AbstractLayer;
import com.github.fahjulian.stealth.core.scene.Camera;
import com.github.fahjulian.stealth.core.util.Maths;
import com.github.fahjulian.stealth.events.application.RenderEvent;
import com.github.fahjulian.stealth.events.application.UpdateEvent;
import com.github.fahjulian.stealth.events.key.AKeyEvent.Key;
import com.github.fahjulian.stealth.events.key.KeyPressedEvent;
import com.github.fahjulian.stealth.events.key.KeyReleasedEvent;
import com.github.fahjulian.stealth.events.mouse.AMouseEvent.Button;
import com.github.fahjulian.stealth.events.mouse.MouseButtonPressedEvent;
import com.github.fahjulian.stealth.graphics.Renderer2D;
import com.github.fahjulian.stealth.graphics.TileMap;
import com.github.fahjulian.stealth.graphics.TileMapModel.MapData;
import com.github.fahjulian.stealth.graphics.opengl.Texture2D;

import org.joml.Vector2f;

public class MapEditorLayer extends AbstractLayer<MapEditorScene>
{
    private static final float CAMERA_SPEED = 250.0f;

    private final int width, height;
    private final float tileSize;
    private final TexturedRect[] tiles;
    private final Vector2f cameraVelocity;

    private TexturedRect selectedTile;

    MapEditorLayer(int width, int height, float tileSize, MapEditorScene scene)
    {
        super(scene);

        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.tiles = new TexturedRect[width * height];
        this.cameraVelocity = new Vector2f();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                tiles[x + y * width] = new TexturedRect(x * tileSize, y * tileSize, tileSize, tileSize,
                        scene.defaultTexture);
            }
        }
    }

    MapEditorLayer(String filePath, MapEditorScene scene)
    {
        super(scene);
        MapData data = new TileMap(filePath).getData();

        this.width = data.getWidth();
        this.height = data.getHeight();
        this.tileSize = data.getTileSize();
        this.tiles = new TexturedRect[width * height];

        Texture2D[] textures = data.getTextures();
        int[] textureIndices = data.getTextureIndices();
        for (int i = 0; i < textureIndices.length; i++)
        {
            int x = i % width, y = i / width;
            int textureIndex = textureIndices[i];
            tiles[i] = new TexturedRect(x * tileSize, y * tileSize, tileSize, tileSize, textures[textureIndex]);
        }

        this.cameraVelocity = new Vector2f();
    }

    @Override
    protected void onInit()
    {
        for (TexturedRect tile : tiles)
            scene.addTexture(tile.texture);

        registerEventListener(RenderEvent.class, this::onRender);
        registerEventListener(KeyPressedEvent.class, this::onKeyPressed);
        registerEventListener(KeyReleasedEvent.class, this::onKeyReleased);
        registerEventListener(UpdateEvent.class, this::onUpdate);
        registerEventListener(MouseButtonPressedEvent.class, this::onMouseButtonPressed);
        registerEventListener(TextureSwitchEvent.class, this::onTextureSwitch);
    }

    private void onTextureSwitch(TextureSwitchEvent event)
    {
        if (selectedTile == null)
            return;

        selectedTile.texture = event.getNewTexture();
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
        Key key = event.getKey();
        cameraVelocity.x += CAMERA_SPEED * (key == Key.D ? 1 : key == Key.A ? -1 : 0);
        cameraVelocity.y += CAMERA_SPEED * (key == Key.W ? 1 : key == Key.S ? -1 : 0);

        if (key == Key.SPACE && selectedTile != null)
        {
            Texture2D nextTexture = scene.nextTexture(selectedTile.texture);
            selectedTile.texture = nextTexture != null ? nextTexture : scene.defaultTexture;
        }
    }

    private void onKeyReleased(KeyReleasedEvent event)
    {
        Key key = event.getKey();
        cameraVelocity.x += CAMERA_SPEED * (key == Key.A ? 1 : key == Key.D ? -1 : 0);
        cameraVelocity.y += CAMERA_SPEED * (key == Key.S ? 1 : key == Key.W ? -1 : 0);
    }

    private void onMouseButtonPressed(MouseButtonPressedEvent event)
    {
        if (event.getButton() == Button.LEFT)
            selectedTile = getTile(event.getX(), event.getY());

        if (event.getButton() == Button.MIDDLE)
        {
            String folderPath = JOptionPane.showInputDialog(null, "Save to file (Enter folder path):");
            if (folderPath != null)
                saveToFile(folderPath);
        }
    }

    private TexturedRect getTile(float x, float y)
    {
        Vector2f worldCoordinates = Maths.mouseRay2D(x, y, scene.getCamera());

        if (worldCoordinates.x < 0 || worldCoordinates.y < 0 || worldCoordinates.x > (width * tileSize)
                || worldCoordinates.y > (height * tileSize))
            return null;

        return tiles[(int) (worldCoordinates.x / tileSize) + (int) (worldCoordinates.y / tileSize) * width];
    }

    private void saveToFile(String folderPath)
    {
        Texture2D[] textures = new Texture2D[width * height];
        for (int i = 0; i < tiles.length; i++)
            textures[i] = tiles[i].texture;

        TileMap tileset = new TileMap("Generated Map", width, height, tileSize, 0.0f, textures);
        tileset.saveToFile(folderPath);
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
