package com.github.fahjulian.stealthmapeditor;

import com.github.fahjulian.stealth.core.AbstractApp;
import com.github.fahjulian.stealth.core.scene.AbstractScene;

public class App extends AbstractApp
{
    private App(String title, int width, int height)
    {
        super(title, width, height, "/home/julian/dev/java/StealthMapEditor/.log/", true);
    }

    @Override
    protected AbstractScene onInit()
    {
        return new MapEditorScene();
    }

    public static void main(String... args)
    {
        new App("Stealth Map Editor", 1024, 512).run();
    }
}
