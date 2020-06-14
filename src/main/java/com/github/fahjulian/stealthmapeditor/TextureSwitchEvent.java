package com.github.fahjulian.stealthmapeditor;

import com.github.fahjulian.stealth.core.event.AbstractEvent;
import com.github.fahjulian.stealth.graphics.opengl.Texture2D;

public class TextureSwitchEvent extends AbstractEvent
{
    private final Texture2D newTexture;

    TextureSwitchEvent(Texture2D newTexture)
    {
        this.newTexture = newTexture;
        dispatch();
    }

    Texture2D getNewTexture()
    {
        return newTexture;
    }
}
