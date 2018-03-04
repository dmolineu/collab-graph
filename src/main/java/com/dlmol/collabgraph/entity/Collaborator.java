package com.dlmol.collabgraph.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "Collaborator{" +
                "name='" + name + '\'' +
                ", collaborators=" + collaborators +
                ", departments=" + departments +
                ", centers=" + centers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collaborator that = (Collaborator) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(collaborators, that.collaborators) &&
                Objects.equals(departments, that.departments) &&
                Objects.equals(centers, that.centers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, collaborators, departments, centers);
    }
}
