package de.webis.webisstud.thesis.reimer.model.reader.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Topic reader for standard XML format used in the TREC Web Tracks.
 * <p>
 * From [Anserini](https://github.com/castorini/anserini/), Apache License 2.0
 */
public class AnseriniWebXmlTopicReader implements AnseriniTopicReader<Integer> {

    private String extractNumber(String line) {
        int i = line.indexOf("number");
        if (i == -1) throw new IllegalArgumentException("line does not contain the tag : " + "number");
        int j = line.indexOf("\"", i + "number".length() + 2);
        if (j == -1) throw new IllegalArgumentException("line does not contain quotation");
        return line.substring(i + "number".length() + 2, j);
    }

    @Override
    public SortedMap<Integer, Map<String, String>> read(BufferedReader reader) throws IOException {
        SortedMap<Integer, Map<String, String>> map = new TreeMap<>();
        Map<String, String> fields = new HashMap<>();

        String number = "";
        String query;
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("<topic")) {
                number = extractNumber(line);
            }
            if (line.startsWith("<query>") && line.endsWith("</query>")) {
                query = line.substring(7, line.length() - 8).trim();
                fields.put("title", query);
            }
            if (line.startsWith("</topic>")) {
                map.put(Integer.valueOf(number), fields);
                fields = new HashMap<>();
            }
        }

        return map;
    }
}