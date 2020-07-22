package com.github.fahjulian.stealthmapeditor;

import com.github.fahjulian.stealth.core.event.AbstractEvent;
import com.github.fahjulian.stealth.graphics.Sprite;

public class SpriteSwitchEvent extends AbstractEvent
{
    private final Sprite sprite;

    SpriteSwitchEvent(Sprite sprite)
    {
        this.sprite = sprite;
        dispatch();
    }

    Sprite getSprite()
    {
        return sprite;
    }
}
