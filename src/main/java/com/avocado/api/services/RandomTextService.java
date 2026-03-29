package com.avocado.api.services;

import com.avocado.api.models.RandomTextRequest;
import com.avocado.api.models.RandomTextResponse;

import java.util.List;
import java.util.Random;

public class RandomTextService {

    private static final List<String> WORD_POOL = List.of(
            "aurora", "cascade", "delta", "ember", "fjord", "glimmer", "harbor",
            "ignite", "jade", "kindle", "luminous", "mosaic", "nebula", "orbit",
            "prism", "quartz", "radiant", "solace", "terrain", "umbra", "verdant",
            "wander", "xenon", "yield", "zenith", "amber", "breeze", "crimson",
            "dusk", "eclipse", "fable", "gossamer", "haze", "ivory", "jubilee",
            "kelp", "lapis", "mirth", "nimbus", "opal", "petal", "quest",
            "reverie", "sage", "twilight", "umber", "veil", "whisper", "zeal"
    );

    private static final int DEFAULT_WORD_COUNT = 20;

    public RandomTextResponse generate(RandomTextRequest request) {
        int seed = request.inputText().hashCode();
        Random random = new Random(seed);
        String randomText = buildRandomText(random, DEFAULT_WORD_COUNT);
        return new RandomTextResponse(request.inputText(), randomText, seed);
    }

    private String buildRandomText(Random random, int wordCount) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < wordCount; i++) {
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            String word = WORD_POOL.get(random.nextInt(WORD_POOL.size()));
            builder.append(i == 0 ? capitalize(word) : word);
        }
        builder.append('.');
        return builder.toString();
    }

    private String capitalize(String word) {
        if (word == null || word.isEmpty()) return word;
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }
}
