package com.dlmol.collabgraph.parser;

import com.dlmol.collabgraph.entity.Collaborator;
import com.dlmol.collabgraph.exception.CollabGraphException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CollabFileParser {
    private static final Logger logger = LoggerFactory.getLogger(CollabFileParser.class);
    public static final Character DELIMITER = '\t';

    public List<Collaborator> getCollaboratorsFromFile(File file) throws CollabGraphException {
        if (file == null || file.isFile() == false) {
            logger.error("File: " + file == null ? null : file.getAbsolutePath() + " does NOT exist!");
            return new ArrayList<>(0);
        } else if (file.length() == 0) {
            logger.error("File: " + file.getAbsolutePath() + " is empty!");
            return new ArrayList<>(0);
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(file.getPath()));
        } catch (IOException e) {
            String msg = "Unable to parse lines from: " + file.getAbsolutePath();
            logger.error(msg, e);
            throw new CollabGraphException(msg);
        }
        List<Collaborator> collaborators = new ArrayList<>(lines.size());
        lines.stream()
                .filter(l -> !l.contains("Timestamp")) //Ignore header line that contains "Timestamp"
                .filter(l -> containsThreeDelimiters(l))
                .forEach(l -> addCollaborator(l, collaborators));
        logger.info("getCollaboratorsFromFile(): Found " + collaborators.size() + " collaborators.");
        return collaborators;
    }

    private boolean containsThreeDelimiters(String l) {
        if (l != null || l.chars().filter(ch -> ch == '\t').count() == 3)
            return true;
        logger.error("containsThreeDelimiters() Line \"" + l + "\" does NOT contain 3 tabs, so can't be properly parsed to a Collaborator entity!");
        return false;
    }

    private void addCollaborator(String l, List<Collaborator> collaborators) {
        if (l == null || collaborators == null)
            return;
        String[] pieces = l.split(DELIMITER.toString());
        if (pieces.length != 5) {
            logger.error("addCollaborator(): Skipping line. Expected 4 tabs in line, found " +
                    (pieces.length - 1) + " for line:\n\"" + l + "\"\n\"" + l.replace("\t", "{tab}") + "\"!");
            return;
        }
        int i=1;
        String name = pieces[i++].trim();
        List<String> collaboratorNames = removeNulls(Arrays.asList(pieces[i++].split(",")));
        List<String> departments = removeNulls(Arrays.asList(pieces[i++].split(",")));
        List<String> centers = removeNulls(Arrays.asList(pieces[i++].split(",")));
        Collaborator c = new Collaborator(name, collaboratorNames, departments, centers);

        logger.debug("addCollaborator(): For line \"" + l + "\":\nname: \"" + name + "\"\ncollaboratorNames: \"" + collaboratorNames
                + "\ndepartments: \"" + departments + "\"\ncenters: \"" + centers + "\"");
        collaborators.add(c);
    }

    private List<String> removeNulls(List<String> strings) {
        final String nullStr = "NULL";
        return strings.stream()
                .filter(s -> !nullStr.equalsIgnoreCase(s.trim().toUpperCase()))
                .map(s -> s.trim())
                .collect(Collectors.toList());
    }
}
