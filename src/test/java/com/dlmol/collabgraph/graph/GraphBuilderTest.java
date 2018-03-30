package com.dlmol.collabgraph.graph;

import org.javatuples.Pair;
import org.junit.Test;

import java.util.Queue;

import static org.junit.Assert.*;

public class GraphBuilderTest {

    @Test
    public void getCircleCoords() {
        final int xMax = 100;
        final int yMax = 100;

        Queue<Pair<Integer, Integer>> coords = GraphBuilder.getCircleCoords(xMax, yMax, 0,4);

        Pair<Integer, Integer> coord1 = coords.remove();
        assertEquals(xMax / 2, coord1.getValue0().longValue());
        assertEquals(yMax, coord1.getValue1().longValue());

        Pair<Integer, Integer> coord2 = coords.remove();
        assertEquals(xMax, coord2.getValue0().longValue());
        assertEquals(yMax / 2, coord2.getValue1().longValue());

        Pair<Integer, Integer> coord3 = coords.remove();
        assertEquals(xMax / 2, coord3.getValue0().longValue());
        assertEquals(0, coord3.getValue1().longValue());

        Pair<Integer, Integer> coord4 = coords.remove();
        assertEquals(0, coord4.getValue0().longValue());
        assertEquals(yMax / 2, coord4.getValue1().longValue());
    }
}