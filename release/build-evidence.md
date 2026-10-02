# Minecraft 26.3 build evidence

- Branch: `mc/26.3`; mod version remains `1.5.1`
- Minecraft 26.3; Java 25; Fabric Loader 0.19.5; Fabric API 0.161.0+26.3
- Loom 1.17.21; Gradle 9.6.0, wrapper distribution checksum pinned
- Verified code checkpoint: `d945e8f941dfd1458a501df4dca8642e3f486bb5`
- [Successful CI build](https://github.com/LIy-hub/level-10-enchantments/actions/runs/37045895145)
- [JAR and sources](https://github.com/LIy-hub/level-10-enchantments/actions/runs/37045895145/artifacts/11244430908)

## Passed

`./gradlew clean check build --no-daemon` compiles main, client and test sources, runs the eight policy executables, validates the packaged JAR and runs Fabric Loader JUnit.

The policies cover enchanting caps, anvil merging/costs, compatibility surcharge, loot identity/probability/dimension gates, librarian trades, rainbow text and resource parity. The parity test compares all 29 enchantments against Mojang's actual 26.3 resources while allowing only the frozen 1.5.1 gameplay changes.

Loader bootstrap checks mod discovery and injected methods on the common Mixin targets; the follow-up adds an explicit client enchantment-name target assertion. CI runs on each commit.

## Runtime JAR SHA-256

`2d5e106602858e50c728322ffb279fa9a1ed20c12b7870daee4be2d54e20d287`

The downloaded CI artifact was independently inspected and the resource parity test also passed locally against Mojang's official 26.3 client JAR.

## Verification limits

No dedicated-server/world startup, client UI, or interactive/multiplayer playtest is claimed. No EULA acceptance, release/tag creation, or CurseForge upload was performed.

Local Gradle cannot initialize Loom because this cloud environment denies its Unix-domain socket capability probe. No patched build tools or sandbox bypasses were used; the normal GitHub runner performed the complete Gradle build.

Historical 26.2 evidence remains on the unchanged `mc/26.2` branch.
