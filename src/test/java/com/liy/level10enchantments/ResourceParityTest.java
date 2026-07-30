package com.liy.level10enchantments;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ResourceParityTest {
    private static final Set<String> ELYTRA_ENCHANTMENTS = Set.of(
            "protection",
            "fire_protection",
            "blast_protection",
            "projectile_protection",
            "thorns"
    );
    private static final Set<String> COMPATIBILITY_ENCHANTMENTS = Set.of(
            "protection",
            "fire_protection",
            "blast_protection",
            "projectile_protection",
            "sharpness",
            "smite",
            "bane_of_arthropods"
    );

    private ResourceParityTest() {
    }

    public static void main(String[] args) throws Exception {
        Path resources = Path.of(requireProperty("level10.resourcesDir"));
        String minecraftVersion = requireProperty("level10.minecraftVersion");
        Path minecraftJar = findMinecraftJar();
        List<Rule> rules = readRules(resources.resolve("level10-enchantments.rules.csv"));
        require(rules.size() == 29, "rule manifest contains 29 enchantments");

        try (JarFile jar = new JarFile(minecraftJar.toFile())) {
            for (Rule rule : rules) {
                String name = rule.id().substring("minecraft:".length());
                String relative = "data/minecraft/enchantment/" + name + ".json";
                JsonObject vanilla = readJarJson(jar, relative);
                require(vanilla.get("max_level").getAsInt() == rule.vanillaMaximum(),
                        name + " vanilla maximum");
                applyAllowedChanges(vanilla, name, rule.effectMode());

                Path generatedPath = resources.resolve(relative.replace('/', java.io.File.separatorChar));
                require(Files.isRegularFile(generatedPath), "generated " + relative);
                JsonObject generated = JsonParser.parseString(Files.readString(
                        generatedPath,
                        StandardCharsets.UTF_8
                )).getAsJsonObject();
                require(vanilla.equals(generated), name + " differs from vanilla beyond allowed fields");
            }
        }

        for (String excluded : List.of(
                "breach",
                "depth_strider",
                "feather_falling",
                "lure",
                "quick_charge",
                "swift_sneak"
        )) {
            require(!Files.exists(resources.resolve(
                    "data/minecraft/enchantment/" + excluded + ".json"
            )), "excluded enchantment is absent: " + excluded);
        }

        JsonObject tag = JsonParser.parseString(Files.readString(
                resources.resolve("data/level10enchantments/tags/item/chest_armor_plus_elytra.json"),
                StandardCharsets.UTF_8
        )).getAsJsonObject();
        JsonArray values = tag.getAsJsonArray("values");
        require(!tag.get("replace").getAsBoolean(), "elytra item tag is additive");
        require(values.size() == 2, "elytra item tag has two values");
        require(values.get(0).getAsString().equals("#minecraft:enchantable/armor"),
                "elytra tag retains vanilla armor");
        require(values.get(1).getAsString().equals("minecraft:elytra"),
                "elytra tag adds elytra");

        System.out.println("PASS: " + minecraftVersion
                + " resources match vanilla beyond the frozen 1.5.1 gameplay changes; jar="
                + minecraftJar);
    }

    private static void applyAllowedChanges(JsonObject json, String name, String effectMode) {
        json.addProperty("max_level", 10);
        switch (effectMode) {
            case "mending" -> applyMending(json);
            case "thorns" -> applyThorns(json);
            case "lunge" -> applyLunge(json);
            case "vanilla" -> {
            }
            default -> throw new AssertionError("Unknown effect mode: " + effectMode);
        }

        if (ELYTRA_ENCHANTMENTS.contains(name)) {
            json.addProperty("supported_items", "#level10enchantments:chest_armor_plus_elytra");
        }
        if (COMPATIBILITY_ENCHANTMENTS.contains(name)) {
            json.remove("exclusive_set");
        }
    }

    private static void applyMending(JsonObject json) {
        JsonObject effect = json.getAsJsonObject("effects")
                .getAsJsonArray("minecraft:repair_with_xp")
                .get(0).getAsJsonObject()
                .getAsJsonObject("effect");
        JsonObject factor = new JsonObject();
        factor.addProperty("type", "minecraft:linear");
        factor.addProperty("base", 2.0);
        factor.addProperty("per_level_above_first", 2.0 / 3.0);
        effect.add("factor", factor);
    }

    private static void applyThorns(JsonObject json) {
        JsonObject trigger = json.getAsJsonObject("effects")
                .getAsJsonArray("minecraft:post_attack")
                .get(0).getAsJsonObject();
        JsonObject chance = new JsonObject();
        chance.addProperty("type", "minecraft:linear");
        chance.addProperty("base", 0.1);
        chance.addProperty("per_level_above_first", 0.1);
        trigger.getAsJsonObject("requirements")
                .getAsJsonObject("chance")
                .add("amount", chance);

        JsonArray effects = trigger.getAsJsonObject("effect").getAsJsonArray("effects");
        JsonObject damage = findEffect(effects, "minecraft:damage_entity");
        JsonObject minimum = new JsonObject();
        minimum.addProperty("type", "minecraft:linear");
        minimum.addProperty("base", 1.0);
        minimum.addProperty("per_level_above_first", 1.0);
        JsonObject maximum = new JsonObject();
        maximum.addProperty("type", "minecraft:linear");
        maximum.addProperty("base", 5.0);
        maximum.addProperty("per_level_above_first", 10.0 / 9.0);
        damage.add("min_damage", minimum);
        damage.add("max_damage", maximum);

        JsonArray replacement = new JsonArray();
        replacement.add(damage);
        trigger.getAsJsonObject("effect").add("effects", replacement);
    }

    private static void applyLunge(JsonObject json) {
        JsonArray effects = json.getAsJsonObject("effects")
                .getAsJsonArray("minecraft:post_piercing_attack")
                .get(0).getAsJsonObject()
                .getAsJsonObject("effect")
                .getAsJsonArray("effects");
        JsonObject exhaustion = findEffect(effects, "minecraft:apply_exhaustion");
        JsonObject linear = new JsonObject();
        linear.addProperty("type", "minecraft:linear");
        linear.addProperty("base", 4.0);
        linear.addProperty("per_level_above_first", 4.0);
        JsonObject amount = new JsonObject();
        amount.addProperty("type", "minecraft:clamped");
        amount.addProperty("min", 0.0);
        amount.addProperty("max", 12.0);
        amount.add("value", linear);
        exhaustion.add("amount", amount);
    }

    private static JsonObject findEffect(JsonArray effects, String type) {
        for (JsonElement element : effects) {
            JsonObject effect = element.getAsJsonObject();
            if (effect.get("type").getAsString().equals(type)) {
                return effect;
            }
        }
        throw new AssertionError("Missing vanilla effect " + type);
    }

    private static List<Rule> readRules(Path path) throws IOException {
        List<Rule> rules = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            require("id,vanilla_max,table_eligible,effect_mode".equals(line), "rule header");
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                require(fields.length == 4, "rule field count: " + line);
                rules.add(new Rule(fields[0], Integer.parseInt(fields[1]), fields[3]));
            }
        }
        return rules;
    }

    private static JsonObject readJarJson(JarFile jar, String path) throws IOException {
        JarEntry entry = jar.getJarEntry(path);
        require(entry != null, "Minecraft JAR contains " + path);
        try (Reader reader = new java.io.InputStreamReader(
                jar.getInputStream(entry),
                StandardCharsets.UTF_8
        )) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    private static Path findMinecraftJar() throws IOException {
        String marker = "data/minecraft/enchantment/sharpness.json";
        for (String entry : System.getProperty("java.class.path").split(
                java.io.File.pathSeparator,
                -1
        )) {
            Path candidate = Path.of(entry);
            if (!Files.isRegularFile(candidate) || !entry.endsWith(".jar")) {
                continue;
            }
            try (JarFile jar = new JarFile(candidate.toFile())) {
                if (jar.getJarEntry(marker) != null) {
                    return candidate;
                }
            }
        }
        throw new AssertionError("Unable to locate the Minecraft common JAR on the test classpath");
    }

    private static String requireProperty(String name) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new AssertionError("Missing system property " + name);
        }
        return value;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private record Rule(String id, int vanillaMaximum, String effectMode) {
    }
}
