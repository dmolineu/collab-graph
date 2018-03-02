package com.dlmol.collabgraph.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Collaborator {

    @Getter
    @Setter
    String name;

    @Getter()
    @Setter
    List<String> collaborators = new ArrayList<>();

    @Getter
    @Setter
    List<String> departments = new ArrayList<>();

    @Getter
    @Setter
    List<String> centers = new ArrayList<>();


}
