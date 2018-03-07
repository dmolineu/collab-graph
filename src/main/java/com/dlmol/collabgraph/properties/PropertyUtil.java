package com.dlmol.collabgraph.properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PropertyUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);

    public List<Pair<String, List<String>>> getMappingList(String prop, String entrySep, String keySep, String itemSep) {
        if (prop == null)
            return null;
        String[] entries = prop.split(entrySep);
        List<Pair<String, List<String>>> mapping =
                Arrays.asList(entries).stream()
                        .map(e -> getPair(keySep, itemSep, e))
                        .collect(Collectors.toList());
        logger.trace("getMappingList(): Returning: " + getMappingString(mapping));
        return mapping;
    }

    private String getMappingString(List<Pair<String, List<String>>> mapping) {
        StringBuilder sb = new StringBuilder("\n");
        mapping.forEach(p -> sb.append("\tkey: \"" + p.getValue0() + "\", values: \"" +
                StringUtils.join(p.getValue1(), "\", \"") + "\"\n"));
        return sb.toString();
    }

    private Pair<String, List<String>> getPair(String keySep, String itemSep, String entry) {
        final String key = entry.substring(0, entry.indexOf(keySep));
        final List<String> values = Arrays.asList(entry.substring(entry.indexOf(keySep) + 1, entry.length()).split(itemSep));
        logger.trace("About to create Pair with key \"" + key + "\" and values: \"" + StringUtils.join(values, ",") + "\"");
        return Pair.with(key, values);
    }
}
