package com.dlmol.collabgraph.controller;

import com.dlmol.collabgraph.entity.Collaborator;
import com.dlmol.collabgraph.graph.GraphBuilder;
import com.dlmol.collabgraph.listener.LinkViewerListener;
import com.dlmol.collabgraph.service.CollaboratorService;
import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Controller
public class GraphController {
    private static final Logger logger = LoggerFactory.getLogger(GraphController.class);

    @Autowired
    CollaboratorService collaboratorService;

    @Autowired
    GraphBuilder graphBuilder;

    @Autowired
    LinkViewerListener linkViewerListener;

    @Value("${show.area.graph}")
    boolean showAreaGraph;

    @Value("${links.enabled}")
    boolean linksEnabled;

    public static String getString(List<Collaborator> collaborators) {
        if (collaborators == null || collaborators.size() == 0)
            return "";
        StringBuffer sb = new StringBuffer();
        collaborators.forEach(c -> {
            sb.append(c.getName());
        });
        return sb.toString();
    }

    @PostConstruct
    public void initRun() throws IOException {
        showGraph();
    }

    @RequestMapping(value = "/")
    @ResponseBody
    public String showGraph() throws IOException {
        InputStream is = new ClassPathResource("static/collab.tsv").getInputStream();
        collaboratorService.populateRepository(is);

        Map<String, Collaborator> collaboratorMap = collaboratorService.getRepo().getCollaborators();

        if (showAreaGraph) {
            Graph areaGraph = graphBuilder.buildAreaGraph(collaboratorMap);
            Viewer areaViewer = areaGraph.display();
            areaViewer.disableAutoLayout();
            areaViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        } else
            logger.info("'show.area.graph' property set to false, skipping Area Graph display.");

        Graph collaboratorGraph = graphBuilder.buildCollaboratorGraph(collaboratorMap);
        Viewer collabViewer = collaboratorGraph.display();
        collabViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        collabViewer.disableAutoLayout();
        if (linksEnabled) {
            ViewerPipe fromViewer = collabViewer.newViewerPipe();
            fromViewer.addViewerListener(linkViewerListener);
            fromViewer.addSink(collaboratorGraph);
            while (true) {
                try {
                    fromViewer.blockingPump();
                } catch (InterruptedException e) {
                    logger.error("showGraph(): " + e.getMessage(), e);
                }
            }
        }else
            logger.info("'links.enabled' property set to false, Nodes will not launch links when clicked now.");
        return collaboratorService.getRepo().toString();
    }
}
