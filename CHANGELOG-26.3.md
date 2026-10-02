# Minecraft 26.3 port

- Target Minecraft 26.3, Fabric Loader 0.19.5, Fabric API 0.161.0+26.3, Java 25, Loom 1.17.21 and Gradle 9.6.0.
- Rebase all 29 enchantment definitions against the official 26.3 resources: new predicate `type` fields, registry/tag references and Frost Walker block-state schema.
- Preserve the frozen 1.5.1 level-X balance, enchanting-table limits, loot probabilities, librarian trades, anvil policy and special Mending/Thorns/Lunge behavior.
- Add a Fabric Loader JUnit bootstrap check that verifies the mod is discovered and its actual common/client mixin methods are present.
- Keep historical version branches unchanged. No CurseForge upload, GitHub Release or tag is created.

## Verification boundary

Compilation, packaged-JAR validation, all eight policy suites and loader/mixin bootstrap are automated.
A dedicated-server/world startup and interactive/multiplayer gameplay test have not been performed.
