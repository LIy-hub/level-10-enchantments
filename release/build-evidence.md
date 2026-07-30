# Build Evidence / 构建证据

- Branch / 分支: `mc/26.2`
- Minecraft: `26.2`
- Mod version / 模组版本: `1.5.1`
- Gradle: `9.5.1`
- Loom: `1.17.17`
- Fabric Loader: `0.19.3`
- Fabric API: `0.155.2+26.2`
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
`D:\CodexWorktrees\level10-evidence\26.2\clean-check-build-attempt2.log`

## Artifacts / 产物

| Artifact | SHA-256 |
|---|---|
| `build/libs/level10-enchantments-1.5.1+mc26.2.jar` | `A8AE474138830E71E9ADD7E728548E7C0E861E2F932B39743D1F8832C5CBA15B` |
| `build/libs/level10-enchantments-1.5.1+mc26.2-sources.jar` | `BF4FE76491EE485CCBF89A88251F3B29D3F267045B65828C1863383A24F12487` |

## Retained failure evidence / 保留的失败证据

The first Minecraft 26.2 gate compiled production, client, and test code and
passed all seven gameplay policy tests, then correctly failed the vanilla
resource parity test because 26.2 changed the `smite` entity predicate schema.
All 29 override resources were regenerated from the target-version common JAR;
ten files changed, and the second full gate passed the deep parity comparison.

Minecraft 26.2 首轮门禁完成全部源码编译并通过七项玩法策略测试，随后因 26.2
修改了 `smite` 实体谓词结构而被原版资源差分测试正确拦截。29 份覆盖资源随后
全部从目标版本 common JAR 重新生成，其中十份产生实际差异；第二轮完整门禁及
深度差分全部通过。

First-gate log / 首轮失败日志:
`D:\CodexWorktrees\level10-evidence\26.2\clean-check-build-attempt1.log`
