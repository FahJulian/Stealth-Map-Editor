package com.github.fahjulian.stealthmapeditor;

import static com.github.fahjulian.stealthmapeditor.Resources.DEFAULT_TEXTURE;

import java.util.ArrayList;
import java.util.List;

import com.github.fahjulian.stealth.core.scene.AbstractScene;
import com.github.fahjulian.stealth.core.util.Log;
import com.github.fahjulian.stealth.graphics.Spritesheet;
import com.github.fahjulian.stealth.graphics.renderer.Renderer2D;
import com.github.fahjulian.stealth.tilemap.TileMap;

public class MapEditorScene extends AbstractScene
{
    private static final int MAX_TEXTURES;

    static
    {
        MAX_TEXTURES = 16;
    }

    private final TileMap map;
    private final List<Spritesheet> spritesheets;

    public MapEditorScene(TileMap map)
    {
        this.map = map;
        this.spritesheets = new ArrayList<>();

        this.addSpritesheet(DEFAULT_TEXTURE);
    }

    @Override
    protected void onInit()
    {
        for (int y = 0; y < this.map.getHeight(); y++)
            for (int x = 0; x < this.map.getWidth(); x++)
                addSpritesheet((Spritesheet) this.map.getTile(x, y).getSprite().getTexture()); // TODO: Change to sheet
                                                                                               // if its only a texture

        super.add(new MapEditorLayer(this.map, this));
        super.add(new MapEditorGUILayer(this, this.spritesheets));
    }

    private void addSpritesheet(Spritesheet spritesheet)
    {
        if (spritesheets.contains(spritesheet))
            return;

        if (spritesheets.size() >= 16)
        {
            Log.warn("(MapEditorScene) Can not add spritesheet %s: Maximum amount of textures is %d", spritesheet,
                    MAX_TEXTURES);
            return;
        }

        Renderer2D.registerTexture(spritesheet);
        spritesheets.add(spritesheet);
    }
}
