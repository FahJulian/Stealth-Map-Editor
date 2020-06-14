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
        this.defaultTexture = new Texture2D("/home/julian/dev/java/Stealth/src/main/resources/textures/red.png");
        this.textures = new Texture2D[MAX_TEXTURES];
    }

    @Override
    protected void onInit()
    {
        addTexture(defaultTexture);
        add(new MapEditorLayer("/home/julian/dev/GeneratedMap.stealthMap.xml", this));
        add(new MapEditorGUILayer(this));
    }

    void addTexture(Texture2D texture)
    {
        if (!texture.loadedSuccesfully())
            return;

        for (Texture2D addedTexture : textures)
        {
            if (addedTexture == null)
                break;
            if (addedTexture.equals(texture))
                return;
        }

        for (int i = 0; i < MAX_TEXTURES; i++)
        {
            if (textures[i] == null)
            {
                textures[i] = texture;
                Renderer2D.registerTexture(texture);
                return;
            }
        }

        Log.warn("(MapEditorLayer) Maximum amount of textures reached.");
    }

    Texture2D nextTexture(Texture2D currentTexture)
    {
        int currentIndex = Arrays.asList(textures).indexOf(currentTexture);
        if (currentIndex == -1)
            return null;

        return textures[currentIndex == 16 ? 0 : currentIndex + 1];
    }

    Texture2D[] getTextures()
    {
        return textures;
    }

}
