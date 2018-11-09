package com.dlmol.collabgraph.graph;

import com.dlmol.collabgraph.entity.AreaInteraction;
import com.dlmol.collabgraph.entity.AreaInteractionRepository;
import com.dlmol.collabgraph.entity.Collaborator;
import com.dlmol.collabgraph.properties.PropertyUtil;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.IntStream;

@Component
public class GraphBuilder {
    private static final Logger logger = LoggerFactory.getLogger(GraphBuilder.class);
    public static final String NODE_NAME_ATTRIBUTE = "ui.label";
    public static final String UI_STYLE_ATTRIBUTE = "ui.style";

    @Autowired
    PropertyUtil propertyUtil;

    @Value("#{propertyUtil.getMappingList('${node.area.class.mapping}', ';', '=', ',')}")
    List<Pair<String, List<String>>> nodeClassMapping;

    @Value("${use.connection.count.based.node.size}")
    boolean isCountBasedNodeSizingEnabled;

    @Value("${min.node.size}")
    int minNodeSize;

    @Value("${max.node.size}")
    int maxNodeSize;

    // http://graphstream-project.org/doc/FAQ/Attributes/Is-there-a-list-of-attributes-with-a-predefined-meaning-for-the-layout-algorithms/
    public Graph buildCollaboratorGraph(Map<String, Collaborator> collaborators) {
        Graph graph = new SingleGraph("Collaborator Graph");
        if (collaborators == null)
            return graph;

        Set<String> allCollaborators = new HashSet<>(collaborators.size() + 5);

        collaborators.keySet().forEach(name -> allCollaborators.add(name)); //Add all names
        collaborators.values().forEach(c -> c.getCollaborators().forEach(name -> allCollaborators.add(name))); //Add all collaborators' names.

        Queue<Pair<Integer, Integer>> circleCoords = getCircleCoords(getScreenWidth(), getScreenHeight(), 80, allCollaborators.size());

        allCollaborators.forEach(name -> createNode(graph, collaborators, name, circleCoords)); // Create a node for each collaborator
        allCollaborators.forEach(name -> addEdge(graph, collaborators, name));

        collaborators.values()
                .forEach(c -> c.getCollaborators()
                        .forEach(name -> addEdge(graph, c, name)));
        if(isCountBasedNodeSizingEnabled)
            setNodeSizesBasedOnEdgeCount(graph);
        graph.addAttribute("layout.quality", 4);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", "url('static/collab_graph_style.css')");
        graph.getEachEdge().forEach(e -> {
            if (similarNodes(collaborators.get(e.getNode0().getId()), collaborators.get(e.getNode1().getId())))
                e.addAttribute("ui.class", "similar");
        });
        return graph;
    }

    private void setNodeSizesBasedOnEdgeCount(Graph graph) {
        final int minCount = graph.getNodeSet().stream().map(n -> n.getEdgeSet().size())
                .min(Comparator.comparing(Integer::valueOf)).get();
        final int maxCount = graph.getNodeSet().stream().map(n -> n.getEdgeSet().size())
                .max(Comparator.comparing(Integer::valueOf)).get();
        final float sizePerConnection = (float) (maxNodeSize - minNodeSize) / (maxCount - minCount);
        graph.getNodeSet().stream().forEach(n -> {
            final int connections = n.getEdgeSet().size();
            final String size = "size: " + (int) (minNodeSize + sizePerConnection * (connections - 1)) + "px;";
            logger.trace("Setting attribute \"" + UI_STYLE_ATTRIBUTE + "\" to: '" + size + "' for node: " + n.getAttribute(NODE_NAME_ATTRIBUTE));
            n.addAttribute(UI_STYLE_ATTRIBUTE, size);
        });
    }

    public static Queue<Pair<Integer, Integer>> getCircleCoords(final int xMax, final int yMax, final int padding, final int n) {
        Queue<Pair<Integer, Integer>> coords = new ArrayBlockingQueue<>(n);
        final int centerX = xMax / 2;
        final int centerY = yMax / 2;
        final int xRadius = centerX - padding;
        final int yRadius = centerY - padding;
//        IntStream.of(n).forEach(m -> {
        for (int m=0; m<n; m++) {
            final int x = (int) (xRadius * Math.sin(2 * m * Math.PI / n) + 0.5 + centerX);
            final int y = (int) (yRadius * Math.cos(2 * m * Math.PI / n) + 0.5 + centerY);
            coords.add(new Pair<>(x, y));
        }
//        });
        if (n == coords.size())
            logger.debug("getCircleCoords(" + n + "): Returning " + coords.size() + " coordinates: " + coords.toString());
        else
            logger.error("getCircleCoords(" + n + "): Returning " + coords.size() + ", expected " + n +
                    "!\n\tCoordinates: " + coords.toString());

        return coords;
    }

    public Graph buildAreaGraph(Map<String, Collaborator> collaborators) {
        Graph graph = new SingleGraph("Area Graph");
        if (collaborators == null)
            return graph;

        Set<String> areas = new HashSet<>();
        collaborators.values().forEach(c -> c.getAreas().forEach(a -> areas.add(a)));
        AreaInteractionRepository aiRepository = new AreaInteractionRepository();
        collaborators.values().forEach(c -> {
            List<String> collaboratorsAreas = getCollaboratorsAreas(c.getCollaborators(), collaborators);
            c.getAreas().forEach(a -> collaboratorsAreas.forEach(ca -> aiRepository.addAreaInteraction(a, ca)));
        });
        logger.debug("Area count: " + areas.size());

        Queue<Pair<Integer, Integer>> circleCoords = getCircleCoords(getScreenHeight(), getScreenHeight(), 80, areas.size());
        //Create Area Nodes
        for (String area : areas) {
            Node node = graph.addNode(area);
            node.addAttribute(NODE_NAME_ATTRIBUTE, area);
            final Pair<Integer, Integer> coord = circleCoords.remove();
            node.setAttribute("xy", coord.getValue0(), coord.getValue1());
        }

        int minCount = aiRepository.getAreaInteractions().stream()
                .filter(ai -> isValidAreaInteraction(ai))
                .map(ai -> ai.getCount())
                .min(Integer::compareTo)
                .orElseThrow(NoSuchElementException::new);
        //Create Area Edges
        aiRepository.getAreaInteractions().stream()
                .filter(ai -> isValidAreaInteraction(ai))
                .forEach(ai -> {
                    Edge edge = graph.addEdge(ai.getAreasLabel(), ai.getAreas().get(0), ai.getAreas().get(1));
                    final int edgeWeight = (ai.getCount() / minCount);
                    edge.setAttribute(UI_STYLE_ATTRIBUTE, "size: " + edgeWeight + "px;");
                    edge.setAttribute("ui.class", "area");
                    logger.trace("Created area Edge \"" + ai.getAreasLabel() + "\" with size: " + edgeWeight);
                });

        graph.addAttribute("layout.quality", 4);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", "url('static/area_graph_style.css')");
        return graph;
    }

    private boolean isValidAreaInteraction(AreaInteraction ai) {
        return ai.getAreas().size() == 2 && !ai.getAreas().get(0).equalsIgnoreCase(ai.getAreas().get(1));
    }

    /**
     * @param names
     * @param collaborators
     * @return List of Areas for each collaborator in List of Names.
     */
    private List<String> getCollaboratorsAreas(List<String> names, Map<String, Collaborator> collaborators) {
        ArrayList<String> areas = new ArrayList<>();
        if (names == null || collaborators == null || collaborators.size() == 0)
            return areas;
        collaborators.values().stream()
                .filter(c -> names.contains(c.getName()))
                .forEach(c -> areas.addAll(c.getAreas()));
        return areas;
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

    private Node createNode(Graph graph, Map<String, Collaborator> collaborators, String name, Queue<Pair<Integer, Integer>> circleCoords) {
        if (collaborators == null) {
            logger.error("createNode(): Collaborators is null!");
            return null;
        }
        Collaborator c = collaborators.get(name);
        Node node;
        if (c == null) {
            logger.warn("createNode(): Collaborator c is null! This happens when someone (" + name + ") is listed as someone else's collaborator, but didn't respond to the survey themself.");
            node = graph.addNode(name);
            node.addAttribute(NODE_NAME_ATTRIBUTE, getLastName(name));
        } else {
            logger.trace("createNode(): Adding node for: \"" + c.getName() + "\"");
            node = graph.addNode(c.getName());
            node.addAttribute(NODE_NAME_ATTRIBUTE, getLastName(c.getName()));
            setNodeClass(node, c.getAreas(), nodeClassMapping);
        }
        Pair<Integer, Integer> coord = circleCoords.remove();
        node.setAttribute("xy", coord.getValue0(), coord.getValue1());
        return node;
    }

    private String getLastName(String name) {
        return name.substring(name.lastIndexOf(" ") + 1, name.length());
    }

    private void setNodeClass(Node node, List<String> areas, List<Pair<String, List<String>>> nodeClassMapping) {
        for (Pair<String, List<String>> mapping : nodeClassMapping) {
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

    private static int getScreenWidth(){
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        logger.debug("getScreenWidth(): Returning: " + width);
        return width;
    }

    private static int getScreenHeight(){
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int height = gd.getDisplayMode().getHeight();
        logger.debug("getScreenHeight(): Returning: " + height);
        return height;
    }
}
