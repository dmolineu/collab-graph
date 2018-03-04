package com.dlmol.collabgraph;

import com.dlmol.collabgraph.controller.GraphController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CollabGraphApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false"); //Allows graph UI to show even though this is technically a web app.
        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        SpringApplication.run(CollabGraphApplication.class, args);
    }
}
