package com.github.fahjulian.stealthmapeditor;

import javax.swing.JOptionPane;

import com.github.fahjulian.stealth.core.Window;
import com.github.fahjulian.stealth.core.scene.AbstractLayer;
import com.github.fahjulian.stealth.events.application.RenderEvent;
import com.github.fahjulian.stealth.events.key.AKeyEvent.Key;
import com.github.fahjulian.stealth.events.key.KeyPressedEvent;
import com.github.fahjulian.stealth.events.mouse.AMouseEvent.Button;
import com.github.fahjulian.stealth.events.mouse.MouseButtonPressedEvent;
import com.github.fahjulian.stealth.graphics.renderer.Renderer2D;
import com.github.fahjulian.stealth.tilemap.Tile;
import com.github.fahjulian.stealth.tilemap.TileMap;

import org.joml.Vector2i;

public class MapEditorLayer extends AbstractLayer<MapEditorScene>
{
    private final TileMap map;
    private final Vector2i selectedTilePos;

    public MapEditorLayer(TileMap map, MapEditorScene scene)
    {
        super(scene);
        this.map = map;
        this.selectedTilePos = new Vector2i(0, 0);
    }

    @Override
    protected void onInit()
    {
        super.registerEventListener(RenderEvent.class, this::onRender);
        super.registerEventListener(SpriteSwitchEvent.class, this::onSpriteSwitch);
        super.registerEventListener(KeyPressedEvent.class, this::onKeyPressed);
        super.registerEventListener(MouseButtonPressedEvent.class, this::onMouseButtonPressed);
    }

    private void onRender(RenderEvent event)
    {
        Renderer2D.draw(map);
    }

    private void onSpriteSwitch(SpriteSwitchEvent event)
    {
        Tile selectedTile = map.getTile(selectedTilePos.x, selectedTilePos.y);
        if (selectedTile == null)
            return;

        selectedTile.setSprite(event.getSprite());
        map.setTile(selectedTilePos.x, selectedTilePos.y, selectedTile);
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
            this.selectedTilePos.set((int) (event.getX() / map.getTileSize()),
                    (int) (event.getY() / map.getTileSize()));
            super.blockEvent(MouseButtonPressedEvent.class);
        }
    }
}
