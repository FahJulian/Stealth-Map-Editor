package com.github.fahjulian.stealthmapeditor;

import static com.github.fahjulian.stealthmapeditor.Resources.ICONS_SHEET;

import java.util.ArrayList;
import java.util.List;

import com.github.fahjulian.stealth.graphics.Sprite;
import com.github.fahjulian.stealth.graphics.renderer.Renderer2D;

public enum Tool
{
    BRUSH(ICONS_SHEET.getSpriteAt(0, 0)), PENCIL(ICONS_SHEET.getSpriteAt(1, 0));

    private static List<Tool> allTools;

    static
    {
        Renderer2D.registerTexture(ICONS_SHEET);
    }

    private final Sprite sprite;

    private Tool(Sprite sprite)
    {
        this.sprite = sprite;

        register(this);
    }

    public Sprite getSprite()
    {
        return sprite;
    }

    private static void register(Tool tool)
    {
        if (Tool.allTools == null)
            Tool.allTools = new ArrayList<>();

        allTools.add(tool);
    }

    public static List<Tool> getAll()
    {
        return allTools;
    }
}
