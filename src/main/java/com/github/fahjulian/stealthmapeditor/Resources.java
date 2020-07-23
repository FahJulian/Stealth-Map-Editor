package com.github.fahjulian.stealthmapeditor;

import static com.github.fahjulian.stealthmapeditor.App.RESOURCE_DIR;

import java.util.Random;

import com.github.fahjulian.stealth.components.FirstPersonCameraComponent;
import com.github.fahjulian.stealth.components.KeyboardControlledMovementComponent;
import com.github.fahjulian.stealth.core.entity.EntityBlueprint;
import com.github.fahjulian.stealth.core.resources.ResourcePool;
import com.github.fahjulian.stealth.graphics.Sprite;
import com.github.fahjulian.stealth.graphics.Spritesheet;
import com.github.fahjulian.stealth.tilemap.Tile;
import com.github.fahjulian.stealth.tilemap.TileMap;

public class Resources
{
    public static final Spritesheet DEFAULT_TEXTURE;
    public static final Spritesheet TILES_SHEET;
    public static final Spritesheet ICONS_SHEET;

    public static final Sprite DEFAULT_SPRITE;

    public static final TileMap DEFAULT_MAP;
    public static final TileMap OTHER_MAP;

    public static final EntityBlueprint CAMERA_CONTROLLER;

    static
    {
        DEFAULT_TEXTURE = ResourcePool
                .getOrLoadResource(new Spritesheet(RESOURCE_DIR + "textures/default.png", 1, 1, 16, 16, 0));
        DEFAULT_SPRITE = new Sprite(DEFAULT_TEXTURE);

        TILES_SHEET = ResourcePool
                .getOrLoadResource(new Spritesheet(RESOURCE_DIR + "textures/tiles.png", 33, 8, 16, 16, 0));

        ICONS_SHEET = ResourcePool
                .getOrLoadResource(new Spritesheet(RESOURCE_DIR + "textures/icons.png", 4, 4, 64, 64, 0));

        DEFAULT_MAP = ResourcePool.getOrLoadResource(
                TileMap.create(RESOURCE_DIR + "maps/default.xml", 100, 100, 100.0f, -1.0f, defaultTileSet(100, 100)));

        OTHER_MAP = ResourcePool.getOrLoadResource(TileMap.fromFile(RESOURCE_DIR + "maps/example_map.xml"));

        CAMERA_CONTROLLER = new EntityBlueprint(new KeyboardControlledMovementComponent.Blueprint(250.0f),
                new FirstPersonCameraComponent.Blueprint());
    }

    public static Tile[] defaultTileSet(int width, int height)
    {
        Random r = new Random();
        Sprite[] sprites = new Sprite[] {
                DEFAULT_SPRITE, TILES_SHEET.getSpriteAt(0, 0), TILES_SHEET.getSpriteAt(1, 1)
        };

        Tile[] tiles = new Tile[width * height];
        for (int i = 0; i < tiles.length; i++)
            tiles[i] = new Tile(sprites[r.nextInt(3)]);

        return tiles;
    }
}
