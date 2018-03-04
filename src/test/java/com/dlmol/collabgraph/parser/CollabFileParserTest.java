package com.dlmol.collabgraph.parser;

import com.dlmol.collabgraph.entity.Collaborator;
import com.dlmol.collabgraph.exception.CollabGraphException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CollabFileParserTest {

    List<Collaborator> collaborators;

    @Before
    public void populate() throws CollabGraphException {
        final File file = new File("src/main/resources/static/collab.tsv");
        final int expectedCount = 29;
        CollabFileParser parser = new CollabFileParser();
        collaborators = parser.getCollaboratorsFromFile(file);
        assertNotNull(collaborators);
        assertTrue("Expected " + expectedCount + " collaborators, but found: " + collaborators.size(), expectedCount == collaborators.size());
    }

    @Test
    public void getCollaboratorsFromFile() throws CollabGraphException {
        Collaborator c = getCollaborator(collaborators, "Maital Neta");
        assertEquals("Maital Neta", c.getName());

        List<String> expectedCollaborators = new ArrayList<>(c.getCollaborators().size());
        expectedCollaborators.add("Becca Brock");
        expectedCollaborators.add("Lisa Crockett");
        expectedCollaborators.add("David DiLillo");
        expectedCollaborators.add("Mike Dodd");
        expectedCollaborators.add("Deb Hope");
        expectedCollaborators.add("Matt Johnson");
        expectedCollaborators.add("Tim Nelson");
        expectedCollaborators.add("Cary Savage");
        expectedCollaborators.add("Jeff Stevens");
        expectedCollaborators.add("Scott Stoltenberg");
        assertEquals(expectedCollaborators, c.getCollaborators());

        List<String> expectedCenters = new ArrayList<>(c.getCenters().size());
        expectedCenters.add("CB3");
        assertEquals(expectedCenters, c.getCenters());

        List<String> expectedDepartments = new ArrayList<>(c.getDepartments().size());
        expectedDepartments.add("Athletics");
        expectedDepartments.add("English");
        expectedDepartments.add("Nutrition & Health Sciences");
        expectedDepartments.add("Political Science");
        assertEquals(expectedDepartments, c.getDepartments());

        List<String> expectedAreas = new ArrayList<>(c.getDepartments().size());
        expectedAreas.add("Neuroscience & Behavior");
        expectedAreas.add("Social & Cognitive");
        assertEquals(expectedAreas, c.getAreas());
    }

    private Collaborator getCollaborator(List<Collaborator> collaborators, String name) {
        return collaborators.stream()
                .filter(c -> name.equalsIgnoreCase(c.getName()))
                .findFirst().get();
    }
}