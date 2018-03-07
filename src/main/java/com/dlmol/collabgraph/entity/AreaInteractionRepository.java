package com.dlmol.collabgraph.entity;

import lombok.Getter;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class AreaInteractionRepository {

    @Getter
    Set<AreaInteraction> areaInteractions = new HashSet<>();

    public void addAreaInteraction(String a1, String a2) {
        AreaInteraction areaInteraction = getAreaInteraction(a1, a2);
        if (areaInteraction == null) {
            areaInteractions.add(new AreaInteraction(a1, a2));
        } else {
            areaInteraction.incrementCount();
        }
    }

    private Integer getAreaInteractionCount(String a1, String a2) {
        final AreaInteraction areaInteraction = getAreaInteraction(a1, a2);
        return areaInteraction == null ? null : areaInteraction.getCount();
    }

    private AreaInteraction getAreaInteraction(String a1, String a2) {
        for (AreaInteraction ai : areaInteractions) {
            if (ai.getAreas().contains(a1) && ai.getAreas().contains(a2))
                return ai;
        }
        return null;
    }

    public Integer getMaxCount() {
        return areaInteractions.stream()
                .map(ai -> Integer.valueOf(ai.getCount()))
                .max(Integer::compareTo).orElseThrow(NoSuchElementException::new);
    }
}
