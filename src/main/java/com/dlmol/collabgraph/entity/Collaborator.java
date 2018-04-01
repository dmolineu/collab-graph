package com.dlmol.collabgraph.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Collaborator {

    @Getter
    @Setter
    String name;

    @Getter
    @Setter
    List<String> collaborators = new ArrayList<>();

    @Getter
    @Setter
    List<String> departments = new ArrayList<>();

    @Getter
    @Setter
    List<String> centers = new ArrayList<>();

    @Getter
    @Setter
    List<String> areas = new ArrayList<>();
}
