package com.dlmol.collabgraph.controller;

import com.dlmol.collabgraph.entity.Collaborator;
import com.dlmol.collabgraph.exception.CollabGraphException;
import com.dlmol.collabgraph.graph.GraphBuilder;
import com.dlmol.collabgraph.repositories.CollaboratorRepository;
import com.dlmol.collabgraph.service.CollaboratorService;
import org.graphstream.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;

@Controller
public class GraphController {
    private static final Logger logger = LoggerFactory.getLogger(GraphController.class);

    @Autowired
    CollaboratorService collaboratorService;

    @Autowired
    GraphBuilder graphBuilder;

    @PostConstruct
    public void initRun(){
        showGraph();
    }

    @RequestMapping(value = "/")
    @ResponseBody
    public String showGraph() {
        File file = new File("src/main/resources/static/collab.tsv");
        try {
            collaboratorService.populateRepositoryFromFile(file);
        } catch (CollabGraphException e) {
            logger.error("Error populated repository!", e);
            return null;
        }

        Map<String, Collaborator> collaboratorMap = collaboratorService.getRepo().getCollaborators();
        Graph graph = graphBuilder.buildGraph(collaboratorMap);
        graph.display();

        return collaboratorService.getRepo().toString();
    }

    public static String getString(List<Collaborator> collaborators) {
        if (collaborators == null || collaborators.size() == 0)
            return "";
        StringBuffer sb = new StringBuffer();
        collaborators.forEach(c -> {
            sb.append(c.getName());
        });
        return sb.toString();
    }
}
