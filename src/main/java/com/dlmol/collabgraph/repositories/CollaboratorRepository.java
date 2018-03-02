package com.dlmol.collabgraph.repositories;

import com.dlmol.collabgraph.entity.Collaborator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CollaboratorRepository {
    private static final Logger logger = LoggerFactory.getLogger(CollaboratorRepository.class);

    @Getter
    @Setter
    Map<String, Collaborator> collaborators;

    public void addCollaborator(Collaborator c) {
        init();
        collaborators.put(c.getName(), c);
    }

    public Collaborator getCollaborator(String name){
        return collaborators.get(name);
    }

    private void init() {
        if (collaborators == null)
            collaborators = new HashMap<>();
    }

    @Override
    public String toString() {
        init();
        logger.debug("toString(): collaborators.size() == " + collaborators.size());
        if (collaborators.isEmpty())
            return "[empty]";
//        String str = StringUtils.join(
//                collaborators.values().stream()
//                        .map(c -> "name: " + c.getName() +
//                                ", collaborators: [" + StringUtils.join(c.getCollaborators(), ",") + "], departments: [" +
//                                StringUtils.join(c.getDepartments(), ",") + "], centers: [" +
//                                StringUtils.join(c.getCenters(), ",") + "]")
//                        .collect(Collectors.toList())
//                , "\n");
        String str = collaborators.values().stream()
                .map(c -> "name: " + c.getName() +
                        ", collaborators: [" + StringUtils.join(c.getCollaborators(), ",") + "], departments: [" +
                        StringUtils.join(c.getDepartments(), ",") + "], centers: [" +
                        StringUtils.join(c.getCenters(), ",") + "]")
                .sorted()
                .collect(Collectors.joining("\n"));
        return str;
    }
}
