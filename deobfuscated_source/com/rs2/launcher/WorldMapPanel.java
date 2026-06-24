/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.launcher;

import com.rs2.launcher.TransparentColorFilter;
import com.rs2.model.Entity;
import com.rs2.model.World;
import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.CraftingHandler;
import com.rs2.util.FileUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import javax.swing.JPanel;

public final class WorldMapPanel
extends JPanel {
    public int mapScale = 1;
    public int mapWidthPixels = 2368 * this.mapScale;
    public int mapHeightPixels = 8000 * this.mapScale;
    private CraftingHandler mapIndex = new CraftingHandler();
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    private static Image[][] regionImages;
    private static int[] regionBaseXs;
    private static int[] regionBaseYs;
    private int j = 0;
    private int mapBaseX = 1536;
    public boolean showPlayerNames = true;

    public WorldMapPanel() {
        this.setFocusable(true);
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setSize(this.mapWidthPixels, this.mapHeightPixels);
        WorldMapPanel worldMapPanel = this;
        try {
            worldMapPanel.mapIndex.loadMapIndex();
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
        }
        regionImages = new Image[worldMapPanel.mapIndex.mapIndexEntryCount][4];
        regionBaseXs = new int[worldMapPanel.mapIndex.mapIndexEntryCount];
        regionBaseYs = new int[worldMapPanel.mapIndex.mapIndexEntryCount];
        worldMapPanel.loadMapRegionSprites();
    }

    private void loadMapRegionSprites() {
        int n = 0;
        while (n < this.mapIndex.mapIndexEntryCount) {
            if (FileUtil.exists("./data/launcher/Sprites/0" + "/" + n + " " + this.mapIndex.regionIds[n] + ".png")) {
                Image image = this.toolkit.getImage("./data/launcher/Sprites/0" + "/" + n + " " + this.mapIndex.regionIds[n] + ".png");
                Object object = new Color(0xFF00FF);
                object = new TransparentColorFilter((Color)object);
                FilteredImageSource filteredImageSource = new FilteredImageSource(image.getSource(), (ImageFilter)object);
                WorldMapPanel.regionImages[n][0] = image = Toolkit.getDefaultToolkit().createImage(filteredImageSource);
                int n2 = n;
                int n3 = this.mapIndex.regionIds[n];
                int n4 = n3 >> 8 << 6;
                n3 = (n3 & 0xFF) << 6;
                WorldMapPanel.regionBaseXs[n2] = n4;
                WorldMapPanel.regionBaseYs[n2] = n3;
            }
            ++n;
        }
    }

    @Override
    public final void paint(Graphics graphics) {
        int n = 0;
        while (n < this.mapIndex.mapIndexEntryCount) {
            graphics.drawImage(regionImages[n][0], (regionBaseXs[n] - this.mapBaseX) * this.mapScale - 1, (10432 - regionBaseYs[n]) * this.mapScale - 64 * this.mapScale + this.mapScale, null);
            ++n;
        }
        Player[] playerArray = World.getPlayers();
        int n2 = playerArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Object object = playerArray[n3];
            if (object != null) {
                int n4 = ((Entity)object).getPosition().getX();
                int n5 = ((Entity)object).getPosition().getY();
                int n6 = ((Entity)object).getPosition().getPlane();
                if (n6 == 0) {
                    graphics.setColor(Color.white);
                    graphics.fillRect((n4 - this.mapBaseX) * this.mapScale, (10432 - n5) * this.mapScale, this.mapScale, this.mapScale);
                    if (this.showPlayerNames) {
                        object = ((Player)object).getUsername();
                        Font font = new Font("Calibri", 0, 12);
                        FontMetrics fontMetrics = this.getFontMetrics(font);
                        graphics.setFont(font);
                        graphics.drawString((String)object, (n4 - this.mapBaseX) * this.mapScale - fontMetrics.stringWidth((String)object) / 2, (10432 - n5) * this.mapScale);
                    }
                }
            }
            ++n3;
        }
    }
}

