# Build Evidence / 构建证据

- Branch / 分支: `mc/26.1.2`
- Minecraft: `26.1.2`
- Mod version / 模组版本: `1.5.1`
- Gradle: `9.5.1`
- Loom: `1.17.17`
- Fabric Loader: `0.19.3`
- Fabric API: `0.155.2+26.1.2`
- Java: `25.0.2`

## Successful gate / 成功门禁

```powershell
.\gradlew.bat clean check build --no-daemon --no-parallel --stacktrace
```

Passed production, client, and test compilation; seven gameplay policy tests;
the target-version vanilla resource parity test; sources packaging; universal
JAR packaging; and packaged metadata/Mixin/resource validation.

生产端、客户端与测试源码编译、七项玩法策略测试、目标版本原版资源逐项差分、
源码包、通用 JAR 以及包内元数据、Mixin 和资源校验全部通过。

Full successful log / 完整成功日志:
`D:\CodexWorktrees\level10-evidence\26.1.2\clean-check-build-attempt2.log`

## Artifacts / 产物

| Artifact | SHA-256 |
|---|---|
| `build/libs/level10-enchantments-1.5.1+mc26.1.2.jar` | `3490B4868D3477C575053EC86D83409D67483E7E74EA1B1EB76BA46847DC83FE` |
| `build/libs/level10-enchantments-1.5.1+mc26.1.2-sources.jar` | `ACCE0FF36ADADB38EDFB9F499D34E8D31478CA4D1C795A86AC5493F5AA96AC1E` |

## Retained failure evidence / 保留的失败证据

1. System Gradle 8.8 could not generate a wrapper while running on Java 25:
   `Unsupported class file major version 69`. The repository now uses the
   official Fabric example Wrapper assets pinned to Gradle 9.5.1.
2. The first Gradle 9.5.1 gate compiled and ran every custom test successfully,
   then failed only because its empty built-in `test` task defaults to
   `failOnNoDiscoveredTests=true`. The build now explicitly disables that empty
   task's false-positive while `check` continues to require all eight executable
   contract tasks.

First-gate log / 首轮失败日志:
`D:\CodexWorktrees\level10-evidence\26.1.2\clean-check-build-attempt1.log`
