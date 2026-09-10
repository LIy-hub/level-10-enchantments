# Source branches and builds / 源码分支与构建

[Project overview / 返回项目介绍](../README.md)

The published multi-version releases are maintained on `mc/<Minecraft version>` branches. The default `main` branch retains the older 1.4.1 source and its PowerShell build scripts. The project overview describes the published loot-and-trading gameplay; use the appropriate version branch to build it.

已发布的多版本源码位于 `mc/<Minecraft 版本>` 分支。默认 `main` 仍保留旧的 1.4.1 源码和 PowerShell 构建脚本；首页介绍的是已发布的探索与交易玩法。构建时请选择相应版本分支。

For example / 以 26.1.2 为例：

```powershell
git clone --branch mc/26.1.2 --single-branch https://github.com/LIy-hub/level-10-enchantments.git
cd level-10-enchantments
.\gradlew.bat clean check build
```

This branch uses JDK 25 and the Gradle Wrapper. Output JARs are written to `build/libs/`. Read the chosen branch's `gradle.properties` for its Minecraft, Loader, Fabric API, and mod versions; mod version numbers need not be identical across branches.

该分支使用 JDK 25 与 Gradle Wrapper，产物输出至 `build/libs/`。其他目标请替换分支名，并查阅该分支的 `gradle.properties` 与构建配置。不同分支的模组补丁版本不一定相同。

The older `main` build runs `build.ps1` and `test.ps1` against a parent server workspace. It requires that workspace's Minecraft 26.1.2 server JAR and Fabric libraries; it is not a standalone build route for current releases.

## Gameplay reference

The [26.1.2 branch's acquisition tables](https://github.com/LIy-hub/level-10-enchantments/blob/mc/26.1.2/README.md#high-level-acquisition--高阶附魔获取) list loot probabilities, librarian costs, and restock limits. The corresponding behavior is defined in `LootBalancePolicy`, `MasterLibrarianTradePolicy`, and `AnvilLevelMergePolicy` on that branch.

获取概率、交易价格和补货次数见上述分支的表格。其他游戏版本使用各自分支中的规则；旧版没有的附魔或结构不应套用新版数值。
