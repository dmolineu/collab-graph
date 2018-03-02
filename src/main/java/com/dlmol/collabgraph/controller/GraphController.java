package com.dlmol.collabgraph.controller;

import com.dlmol.collabgraph.entity.Collaborator;
import com.dlmol.collabgraph.exception.CollabGraphException;
import com.dlmol.collabgraph.repositories.CollaboratorRepository;
import com.dlmol.collabgraph.service.CollaboratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.List;

@Controller
public class GraphController {
    private static final Logger logger = LoggerFactory.getLogger(GraphController.class);

    @Autowired
    CollaboratorService collaboratorService;

    @Autowired
    CollaboratorRepository repository;

    @RequestMapping(value = "/")
    @ResponseBody
    public String showGraph() {
        File csv = new File("src/main/resources/static/collab.tsv");
        try {
            collaboratorService.populateRepositoryFromFile(csv);
        } catch (CollabGraphException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return collaboratorService.getCollaboratorRepository().toString();
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
