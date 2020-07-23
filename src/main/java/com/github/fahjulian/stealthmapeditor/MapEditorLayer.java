package com.github.fahjulian.stealthmapeditor;

import static com.github.fahjulian.stealthmapeditor.Resources.CAMERA_CONTROLLER;

import javax.swing.JOptionPane;

import com.github.fahjulian.stealth.core.Window;
import com.github.fahjulian.stealth.core.entity.Transform;
import com.github.fahjulian.stealth.core.scene.AbstractLayer;
import com.github.fahjulian.stealth.events.application.RenderEvent;
import com.github.fahjulian.stealth.events.key.AKeyEvent.Key;
import com.github.fahjulian.stealth.events.key.KeyPressedEvent;
import com.github.fahjulian.stealth.events.mouse.AMouseEvent.Button;
import com.github.fahjulian.stealth.events.mouse.MouseButtonPressedEvent;
import com.github.fahjulian.stealth.events.mouse.MouseDraggedEvent;
import com.github.fahjulian.stealth.graphics.Sprite;
import com.github.fahjulian.stealth.graphics.renderer.Renderer2D;
import com.github.fahjulian.stealth.tilemap.Tile;
import com.github.fahjulian.stealth.tilemap.TileMap;

public class MapEditorLayer extends AbstractLayer<MapEditorScene>
{
    private final TileMap map;
    private Sprite sprite;
    private Tool tool;

    public MapEditorLayer(TileMap map, MapEditorScene scene)
    {
        super(scene);
        this.map = map;
        this.tool = Tool.PENCIL;
    }

    @Override
    protected void onInit()
    {
        add(CAMERA_CONTROLLER.create("Main camera controller",
                new Transform(Window.get().getWidth() / 2, Window.get().getHeight() / 2)));

        super.registerEventListener(RenderEvent.class, this::onRender);
        super.registerEventListener(SpriteSwitchEvent.class, this::onSpriteSwitch);
        super.registerEventListener(KeyPressedEvent.class, this::onKeyPressed);
        super.registerEventListener(MouseButtonPressedEvent.class, this::onMouseButtonPressed);
        super.registerEventListener(ToolSwitchEvent.class, this::onToolSwitch);
        super.registerEventListener(MouseDraggedEvent.class, this::onMouseDragged);
    }

    private void onRender(RenderEvent event)
    {
        Renderer2D.draw(map);
    }

    private void onSpriteSwitch(SpriteSwitchEvent event)
    {
        sprite = event.getSprite();
    }

    private void onToolSwitch(ToolSwitchEvent event)
    {
        this.tool = event.getTool();
    }

    private void onKeyPressed(KeyPressedEvent event)
    {
        if (event.getKey() == Key.S && Window.get().isKeyPressed(Key.CONTROL))
        {
            String filePath = JOptionPane.showInputDialog("Where do you want to save the map?", map.getFilePath());
            if (filePath != null)
            {
                map.setFilePath(filePath);
                map.save();
            }
        }
    }

    private void onMouseButtonPressed(MouseButtonPressedEvent event)
    {
        if (event.getButton() == Button.LEFT)
        {
            if (tool == Tool.BRUSH || tool == Tool.PENCIL)
            {
                int tileX = (int) (event.getX() / map.getTileSize()), tileY = (int) (event.getY() / map.getTileSize());
                switchSpriteOfTileAt(tileX, tileY);
            }

            super.blockEvent(MouseButtonPressedEvent.class);
        }
    }

    private void onMouseDragged(MouseDraggedEvent event)
    {
        if (event.getButton() == Button.LEFT)
        {
            if (tool == Tool.BRUSH)
            {
                int tileX = (int) (event.getX() / map.getTileSize()), tileY = (int) (event.getY() / map.getTileSize());
                switchSpriteOfTileAt(tileX, tileY);
            }

            super.blockEvent(MouseDraggedEvent.class);
        }
    }

    private void switchSpriteOfTileAt(int tileX, int tileY)
    {
        if (sprite != null)
        {
            Tile tile = map.getTile(tileX, tileY);
            if (tile != null)
            {
                tile.setSprite(sprite);
                map.setTile(tileX, tileY, tile);
            }
        }
    }
}
