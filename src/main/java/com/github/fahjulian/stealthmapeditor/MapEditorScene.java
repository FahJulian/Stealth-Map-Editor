package com.github.fahjulian.stealthmapeditor;

import java.util.Arrays;

import com.github.fahjulian.stealth.core.scene.AbstractScene;
import com.github.fahjulian.stealth.core.util.Log;
import com.github.fahjulian.stealth.graphics.Renderer2D;
import com.github.fahjulian.stealth.graphics.opengl.Texture2D;

public class MapEditorScene extends AbstractScene
{
    private static final int MAX_TEXTURES = 16;
    final Texture2D defaultTexture;
    private final Texture2D[] textures;

    public MapEditorScene()
    {
        this.defaultTexture = new Texture2D(
                "/home/julian/dev/java/Stealth/src/main/resources/textures/default_tile.png");
        this.textures = new Texture2D[MAX_TEXTURES];
    }

    @Override
    protected void onInit()
    {
        addTexture(defaultTexture);
        addTexture(new Texture2D("/home/julian/dev/java/Stealth/src/main/resources/textures/player.png"));
        add(new MapEditorLayer(10, 10, 100.0f, this));
    }

    void addTexture(Texture2D texture)
    {
        int nextIndex = -1;
        for (int i = 0; i < MAX_TEXTURES; i++)
        {
            if (textures[i] == null)
            {
                nextIndex = i;
                break;
            }
        }

        if (nextIndex == -1)
        {
            Log.warn("(MapEditorLayer) Maximum amount of textures reached.");
            return;
        }

        textures[nextIndex] = texture;
        Renderer2D.registerTexture(texture);
    }

    Texture2D nextTexture(Texture2D currentTexture)
    {
        int currentIndex = Arrays.asList(textures).indexOf(currentTexture);
        if (currentIndex == -1)
            return null;

        return textures[currentIndex == 16 ? 0 : currentIndex + 1];
    }

}
