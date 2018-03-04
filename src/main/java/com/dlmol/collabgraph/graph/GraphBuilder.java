package com.dlmol.collabgraph.graph;

import com.dlmol.collabgraph.entity.Collaborator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphBuilder {
    private static final Logger logger = LoggerFactory.getLogger(GraphBuilder.class);

    protected String getStyleSheet() {
        return  "node {" +
                    "	fill-color: black;" +
                    "   size: 100px;" +
                    "}" +
                    "node.cb3 {" +
                    "	fill-color: blue;" +
                    "   size: 100px;" +
                    "}";
    }

    public Graph buildGraph(Map<String, Collaborator> collaborators) {
        Graph graph = new SingleGraph("Collaboration Graph");
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
        return graph;
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
            node.addAttribute("ui.class", "node");
            node.addAttribute("fill-color", "red");
            node.addAttribute("size", "30px");
            return;
        }
        logger.trace("createNode(): Adding node for: \"" + c.getName() + "\"");
        Node node = graph.addNode(c.getName());
        node.addAttribute("ui.label", c.getName());
        node.addAttribute("fill-color", "green");
        node.addAttribute("size", "30px");
        if (c.getCenters() != null && c.getCenters().contains("CB3")) {
            logger.trace("createNode(): " + c.getName() + " is in center CB3, setting fill-color to 'red'.");
            node.addAttribute("ui.class", "node.cb3");
            node.addAttribute("fill-color", "blue");
        }
    }

    private void addEdge(Graph graph, Map<String, Collaborator> collaboratorMap, String name) {
        if (collaboratorMap.get(name) == null || collaboratorMap.get(name).getCollaborators() == null)
            return;
        collaboratorMap.get(name).getCollaborators().forEach(c -> addEdge(graph, c, name));

    }

    private void addEdge(Graph graph, Collaborator c, String name) {
        final String id = c.getName() + " and " + name;
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
        final String id = c + " and " + name;
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
