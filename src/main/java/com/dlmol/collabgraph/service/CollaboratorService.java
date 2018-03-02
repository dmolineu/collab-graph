package com.dlmol.collabgraph.service;

import com.dlmol.collabgraph.entity.Collaborator;
import com.dlmol.collabgraph.exception.CollabGraphException;
import com.dlmol.collabgraph.parser.CollabFileParser;
import com.dlmol.collabgraph.repositories.CollaboratorRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

public class CollaboratorService {

    @Autowired
    CollabFileParser csvParser;

    @Autowired
            @Getter
            @Setter
    CollaboratorRepository collaboratorRepository;

    public void populateRepositoryFromFile(File file) throws CollabGraphException {
        List<Collaborator> collaborators = csvParser.getCollaboratorsFromFile(file);
        collaborators.forEach(c -> collaboratorRepository.addCollaborator(c));
    }
}
