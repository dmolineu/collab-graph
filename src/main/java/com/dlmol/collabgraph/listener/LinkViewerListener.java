package com.dlmol.collabgraph.listener;

import com.dlmol.collabgraph.controller.GraphController;
import com.dlmol.collabgraph.entity.Collaborator;
import com.dlmol.collabgraph.repositories.CollaboratorRepository;
import org.graphstream.ui.view.ViewerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class LinkViewerListener implements ViewerListener {
    private static final Logger logger = LoggerFactory.getLogger(GraphController.class);

    @Autowired
    CollaboratorRepository collaboratorRepository;

    @Override
    public void viewClosed(String viewName) {
        logger.info("viewClosed: \"" + viewName + "\".");
    }

    @Override
    public void buttonPushed(String id) {
        logger.debug("Button \"" + id + "\" pushed!");
        if (collaboratorRepository == null) {
            logger.error("buttonPushed(): collaboratorRepository is null!");
            return;
        }
        Collaborator collaborator = collaboratorRepository.getCollaborator(id);
        String url = collaborator == null ? "https://psychology.unl.edu/" : collaborator.getUrl();
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            logger.error("buttonPushed(\"" + id + "\"): " + e.getMessage(), e);
        }
    }

    @Override
    public void buttonReleased(String id) {
//        logger.trace("Button \"" + id + "\" released!");
    }
}
