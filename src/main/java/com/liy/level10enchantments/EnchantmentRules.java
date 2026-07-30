package com.liy.level10enchantments;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class EnchantmentRules {
    public record Rule(int vanillaMax, boolean tableEligible, String effectMode) {
    }

    private static final Map<String, Rule> RULES = load();

    private EnchantmentRules() {
    }

    public static Optional<Rule> find(String id) {
        return Optional.ofNullable(RULES.get(id));
    }

    public static Map<String, Rule> all() {
        return RULES;
    }

    private static Map<String, Rule> load() {
        InputStream stream = EnchantmentRules.class.getResourceAsStream("/level10-enchantments.rules.csv");
        if (stream == null) {
            throw new IllegalStateException("Missing level10-enchantments.rules.csv");
        }

        LinkedHashMap<String, Rule> result = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                if (line.isBlank()) {
                    continue;
                }

                String[] fields = line.split(",", -1);
                if (fields.length != 4) {
                    throw new IllegalStateException("Invalid rule: " + line);
                }
                Rule prior = result.put(fields[0], new Rule(
                        Integer.parseInt(fields[1]),
                        Boolean.parseBoolean(fields[2]),
                        fields[3]
                ));
                if (prior != null) {
                    throw new IllegalStateException("Duplicate rule: " + fields[0]);
                }
            }
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }

        if (result.size() != 26) {
            throw new IllegalStateException("Expected 26 rules, found " + result.size());
        }
        return Collections.unmodifiableMap(result);
    }
}
