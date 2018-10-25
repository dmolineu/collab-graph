package com.dlmol.collabgraph.viewer.listener;

import org.graphstream.ui.view.ViewerListener;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LinkViewerListener implements ViewerListener {
    protected boolean loop = true;

    @Override
    public void viewClosed(String viewName) {
        System.out.println("viewClosed: \"" + viewName + "\".");
    }

    @Override
    public void buttonPushed(String id) {
        System.out.println("Button \"" + id + "\" pushed!");
        try {
            Desktop.getDesktop().browse(new URI("https://www.google.com"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buttonReleased(String id) {
        System.out.println("Button \"" + id + "\" released!");
    }
}
