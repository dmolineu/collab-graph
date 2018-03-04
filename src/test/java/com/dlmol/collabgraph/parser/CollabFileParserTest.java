package com.dlmol.collabgraph.parser;

import com.dlmol.collabgraph.entity.Collaborator;
import com.dlmol.collabgraph.exception.CollabGraphException;
import com.dlmol.collabgraph.service.CollaboratorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CollabFileParserTest {
    final File file = new File("src/main/resources/static/collab.tsv");

    @Autowired
    CollaboratorService collaboratorService;

    @Before
    public void populate() throws CollabGraphException {
        collaboratorService.populateRepositoryFromFile(file);
        assertNotNull(collaboratorService.getRepo());
        assertTrue(25 == collaboratorService.getRepo().getCollaborators().values().size());
    }

    @Test
    public void getCollaboratorsFromFile() throws CollabGraphException {
        Collaborator c = collaboratorService.getCollaborator("Maital Neta");
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
        expectedDepartments.add("English");
        expectedDepartments.add("Nutrition Health Sciences");
        expectedDepartments.add("Political Science");
        assertEquals(expectedDepartments, c.getCollaborators());


    }
}