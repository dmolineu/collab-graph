package com.dlmol.collabgraph.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AreaInteraction {

    private Set<String> areas = new HashSet<>(2);

    @Getter
    private int count;

    public AreaInteraction (String a1, String a2) {
        this.areas.add(a1);
        this.areas.add(a2);
        this.count = 0;
    }

    public void incrementCount(){
        this.count++;
    }

    public List<String> getAreas(){
        return areas.stream().sorted().collect(Collectors.toList());
    }

    public String getAreasLabel(){
        return StringUtils.join(areas.stream().sorted().collect(Collectors.toList()), " <-> ");
    }
}
