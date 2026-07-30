# Build Evidence / 构建证据

- Branch / 分支: `mc/26.1`
- Minecraft: `26.1`
- Mod version / 模组版本: `1.5.1`
- Gradle: `9.5.1`
- Loom: `1.17.17`
- Fabric Loader: `0.19.3`
- Fabric API: `0.145.1+26.1`
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
`D:\CodexWorktrees\level10-evidence\26.1\clean-check-build-attempt1.log`

## Artifacts / 产物

| Artifact | SHA-256 |
|---|---|
| `build/libs/level10-enchantments-1.5.1+mc26.1.jar` | `51BC13CA2C46A8A4052F9C81FE72567486EC11A24F7488D496539ECDB9E2CDFB` |
| `build/libs/level10-enchantments-1.5.1+mc26.1-sources.jar` | `F516C16D1146526C56198979DDE36BDC93B8962CFE84A8B6D03E83B9745C5D86` |

## Migration result / 迁移结果

The first target-version gate passed. No Minecraft 26.1-specific Java or
resource adaptation beyond dependency and metadata pinning was required; the
resource parity test independently confirmed that the committed 29
enchantments match the 26.1 vanilla definitions except for the frozen 1.5.1
gameplay changes.

目标版本首轮门禁通过。除依赖与元数据锁定外，无需增加 Minecraft 26.1
专属 Java 或资源改动；资源差分测试独立确认 29 份附魔定义与 26.1 原版一致，
差异仅限已经冻结的 1.5.1 玩法变更。
