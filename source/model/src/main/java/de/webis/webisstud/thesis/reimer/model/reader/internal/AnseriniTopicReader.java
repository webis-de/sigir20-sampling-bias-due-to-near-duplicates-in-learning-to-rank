package de.webis.webisstud.thesis.reimer.model.reader.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

/**
 * A reader of topics, i.e., information needs or queries, in a variety of standard formats.
 * <p>
 * From [Anserini](https://github.com/castorini/anserini/), Apache License 2.0
 */
public interface AnseriniTopicReader<K> {
    SortedMap<K, Map<String, String>> read(BufferedReader reader) throws IOException;
}