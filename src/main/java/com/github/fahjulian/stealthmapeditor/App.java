package com.github.fahjulian.stealthmapeditor;

import static com.github.fahjulian.stealthmapeditor.Resources.defaultTileSet;

import javax.swing.JOptionPane;

import com.github.fahjulian.stealth.core.AbstractApp;
import com.github.fahjulian.stealth.core.resources.ResourcePool;
import com.github.fahjulian.stealth.core.scene.AbstractScene;
import com.github.fahjulian.stealth.core.util.Log;
import com.github.fahjulian.stealth.tilemap.TileMap;

public class App extends AbstractApp
{
    public static final String BASE_DIR, RESOURCE_DIR;

    static
    {
        BASE_DIR = System.getProperty("user.dir") + "/";
        RESOURCE_DIR = BASE_DIR + "src/main/resources/";
    }

    private App(String title, int width, int height)
    {
        super(title, width, height, BASE_DIR + ".log/", true);
    }

    @Override
    protected AbstractScene onInit()
    {
        String filePath = JOptionPane
                .showInputDialog("Enter the filepath to the map you want to edit. Leave blank for a new map.");

        TileMap map = null;
        if (filePath.equals(""))
        {
            int width, height;
            float tileSize, posZ;

            while (true)
            {
                try
                {
                    width = Integer.valueOf(JOptionPane.showInputDialog("Width:"));
                    height = Integer.valueOf(JOptionPane.showInputDialog("Height:"));
                    tileSize = Float.valueOf(JOptionPane.showInputDialog("Tile size:"));
                    posZ = Float.valueOf(JOptionPane.showInputDialog("PosZ:"));
                    map = TileMap.create(BASE_DIR + "export/new_map.xml", width, height, tileSize, posZ,
                            defaultTileSet(width, height));
                    map = ResourcePool.getOrLoadResource(map);
                    break;
                }
                catch (Exception e)
                {
                    Log.warn("Invalid inputs.");
                }
            }
        }
        else
        {
            map = ResourcePool.getOrLoadResource(TileMap.fromFile(filePath));
        }

        return new MapEditorScene(map);
    }

    public static void main(String... args)
    {
        new App("Stealth Map Editor", 1920, 1015).run();
    }
}
