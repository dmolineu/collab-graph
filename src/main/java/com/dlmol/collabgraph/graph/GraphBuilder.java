package com.dlmol.collabgraph.graph;

import com.dlmol.collabgraph.entity.Collaborator;
import com.dlmol.collabgraph.properties.PropertyUtil;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GraphBuilder {
    private static final Logger logger = LoggerFactory.getLogger(GraphBuilder.class);

    @Autowired
    PropertyUtil propertyUtil;

    @Value("#{propertyUtil.getMappingList('${node.area.class.mapping}', ';', '=', ',')}")
    List<Pair<String, List<String>>> nodeClassMapping;

    public Graph buildCollaboratorGraph(Map<String, Collaborator> collaborators) {
        Graph graph = new SingleGraph("Collaborator Graph");
        if (collaborators == null)
            return graph;

        Set<String> allCollaborators = new HashSet<>(collaborators.size() + 5);

        collaborators.keySet().forEach(name -> allCollaborators.add(name)); //Add all names
        collaborators.values().forEach(c -> c.getCollaborators().forEach(name -> allCollaborators.add(name))); //Add all collaborators' names.

        allCollaborators.forEach(name -> createNode(graph, collaborators, name)); // Create a node for each collaborator
        allCollaborators.forEach(name -> addEdge(graph, collaborators, name));

        collaborators.values()
                .forEach(c -> c.getCollaborators()
                        .forEach(name -> addEdge(graph, c, name)));

        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", "url('static/graph_style.css')");
        graph.getEachEdge().forEach(e -> {
            if (similarNodes(collaborators.get(e.getNode0().getId()), collaborators.get(e.getNode1().getId())))
                e.addAttribute("ui.class", "similar");
        });
        return graph;
    }

    public Graph buildAreaGraph(Map<String, Collaborator> collaborators) {
        Graph graph = new SingleGraph("Area Graph");
        if (collaborators == null)
            return graph;

        Set<String> allCollaborators = new HashSet<>(collaborators.size() + 5);

        collaborators.keySet().forEach(name -> allCollaborators.add(name)); //Add all names
        collaborators.values().forEach(c -> c.getCollaborators().forEach(name -> allCollaborators.add(name))); //Add all collaborators' names.

        allCollaborators.forEach(name -> createNode(graph, collaborators, name)); // Create a node for each collaborator
        allCollaborators.forEach(name -> addEdge(graph, collaborators, name));

        collaborators.values()
                .forEach(c -> c.getCollaborators()
                        .forEach(name -> addEdge(graph, c, name)));

        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", "url('static/graph_style.css')");
        graph.getEachEdge().forEach(e -> {
            if (similarNodes(collaborators.get(e.getNode0().getId()), collaborators.get(e.getNode1().getId())))
                e.addAttribute("ui.class", "similar");
        });
        return graph;
    }

    private boolean similarNodes(Collaborator c0, Collaborator c1) {
        boolean hasAreaMatch = false;
        if (c0 == null || c1 == null)
            return hasAreaMatch;
        for (String a0 : c0.getAreas())
            for (String a1 : c1.getAreas())
                if (a0.equalsIgnoreCase(a1))
                    hasAreaMatch = true;
//        boolean hasAreaMatch = c0.getAreas().stream()
//                .anyMatch(a0 -> c1.getAreas().stream().anyMatch(a1 -> Objects.equals(a0, a1)));
        logger.trace("similarNodes(): Node \"" + c0.getName() + "\" and \"" + c1.getName() + "\" has an Area in common.");
        return hasAreaMatch;
    }

    private void createNode(Graph graph, Map<String, Collaborator> collaborators, String name) {
        if (collaborators == null) {
            logger.error("createNode(): Collaborators is null!");
            return;
        }
        Collaborator c = collaborators.get(name);
        if (c == null) {
            logger.warn("createNode(): Collaborator c is null! This happens when someone (" + name + ") is listed as someone else's collaborator, but didn't repond to the survey themself.");
            Node node = graph.addNode(name);
            node.addAttribute("ui.label", name);
//            node.addAttribute("ui.class", "node");
        } else {
            logger.trace("createNode(): Adding node for: \"" + c.getName() + "\"");
            Node node = graph.addNode(c.getName());
            node.addAttribute("ui.label", c.getName());
            setNodeClass(node, c.getAreas(), nodeClassMapping);
        }
    }

    private void setNodeClass(Node node, List<String> areas, List<Pair<String, List<String>>> nodeClassMapping) {
        for (Pair<String, List<String>> mapping : nodeClassMapping){
            if (areas.containsAll(mapping.getValue1())) {
                node.addAttribute("ui.class", mapping.getValue0());
                logger.debug("setNodeClass(): Setting Node ID '" + node.getId() + "' to node area class: '" + mapping.getValue0() + "'.");
                return;
            }
        }
        logger.debug("setNodeClass(): No node area class matched for Node ID '" + node.getId() + "' with areas: " + areas.toString());
    }

    private void addEdge(Graph graph, Map<String, Collaborator> collaboratorMap, String name) {
        if (collaboratorMap.get(name) == null || collaboratorMap.get(name).getCollaborators() == null)
            return;
        collaboratorMap.get(name).getCollaborators().forEach(c -> addEdge(graph, c, name));
    }

    private void addEdge(Graph graph, Collaborator c, String name) {
        final String id = c.getName() + " <-> " + name;
        if (graph.getEdge(id) == null)
            try {
                Edge edge = graph.addEdge(id, c.getName(), name);
                edge.addAttribute("weight", "5");
                logger.debug("addEdge(): Added edge: " + id);
            } catch (EdgeRejectedException e) {
                logger.info("addEdge(): Unable to create Edge: " + id);
            }
    }

    private void addEdge(Graph graph, String c, String name) {
        final String id = c + " <-> " + name;
        if (graph.getEdge(id) == null)
            try {
                Edge edge = graph.addEdge(id, c, name);
                edge.addAttribute("weight", "5");
                logger.debug("addEdge(): Added edge: " + id);
            } catch (EdgeRejectedException e) {
                logger.info("addEdge(): Unable to create Edge: " + id);
            }
    }
}
