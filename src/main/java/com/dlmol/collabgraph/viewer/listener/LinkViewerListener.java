package com.dlmol.collabgraph.viewer.listener;

import com.dlmol.collabgraph.controller.GraphController;
import org.graphstream.ui.view.ViewerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LinkViewerListener implements ViewerListener {
    private static final Logger logger = LoggerFactory.getLogger(GraphController.class);

    @Override
    public void viewClosed(String viewName) {
        logger.debug("viewClosed: \"" + viewName + "\".");
    }

    @Override
    public void buttonPushed(String id) {
        logger.debug("Button \"" + id + "\" pushed!");
        try {
            Desktop.getDesktop().browse(new URI("https://www.google.com"));
        } catch (IOException | URISyntaxException e) {
            logger.error("buttonPushed(\"" + id + "\"): " + e.getMessage(), e);
        }
    }

    @Override
    public void buttonReleased(String id) {
        logger.trace("Button \"" + id + "\" released!");
    }
}
