package com.dlmol.collabgraph.service;

import com.dlmol.collabgraph.entity.Collaborator;
import com.dlmol.collabgraph.exception.CollabGraphException;
import com.dlmol.collabgraph.parser.CollabFileParser;
import com.dlmol.collabgraph.repositories.CollaboratorRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CollaboratorService {

    @Autowired
    private CollabFileParser fileParser;

    @Autowired
    @Getter
    @Setter
    private CollaboratorRepository repo;

    public void populateRepository(File file) throws CollabGraphException {
        List<Collaborator> collaborators = fileParser.getCollaboratorsFromFile(file);
        collaborators.forEach(c -> repo.addCollaborator(c));
    }

    public void populateRepository(InputStream is) throws IOException {
        List<Collaborator> collaborators = fileParser.getCollaboratorsFromStream(is);
        collaborators.forEach(c -> repo.addCollaborator(c));
    }

    public void addCollaborator(Collaborator c) {
        repo.addCollaborator(c);
    }

    public Collaborator getCollaborator(String name){
        return repo.getCollaborator(name);
    }

    @Override
    public String toString() {
        return repo.toString();
    }
}
