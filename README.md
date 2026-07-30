# Level 10 Enchantments

[![CI](https://github.com/LIy-hub/level-10-enchantments/actions/workflows/ci.yml/badge.svg)](https://github.com/LIy-hub/level-10-enchantments/actions/workflows/ci.yml)
[![CodeQL](https://github.com/LIy-hub/level-10-enchantments/actions/workflows/codeql.yml/badge.svg)](https://github.com/LIy-hub/level-10-enchantments/actions/workflows/codeql.yml)

A Fabric mod for Minecraft 1.21.8 that extends selected vanilla enchantments to level X.

十级附魔是一个适用于 Minecraft 1.21.8 的 Fabric 模组，将适合的原版附魔扩展至 X 级。

## Features / 功能

- 28 selected vanilla enchantments can reach level X<br>
  28 种适合的原版附魔可提升至 X 级
- Enchanting never exceeds vanilla maxima; 25 bookshelves make vanilla maximum-level and multi-enchantment results more likely<br>
  附魔台不再突破原版等级；25 个书架只提高原版最高级和多附魔结果的概率
- Anvils merge equal enchantments only within the vanilla range; equal levels at or above the vanilla maximum no longer increase<br>
  铁砧只在原版等级范围内叠加升级；达到原版上限后，同级合并不再升级
- Level VI-X enchantments are gated behind selected structure loot and a fixed master-librarian trade<br>
  VI-X 级附魔仅来自指定结构战利品和大师级图书管理员的固定高阶交易
- Mending X repairs 8 durability per XP  
  经验修补 X 每点经验修复 8 点耐久
- Universal 100-level anvil cost cap  
  铁砧花费上限为 100 级
- Selected incompatible enchantments can be combined with progressive surcharges  
  部分互斥附魔可以组合，并产生递增的额外花费
- Elytra supports selected chestplate enchantments  
  鞘翅支持部分胸甲附魔
- Smooth aurora-gradient names for level-X enchantments on the client, without font-weight flicker<br>
  客户端以平滑极光渐变显示 X 级附魔，并移除粗体闪烁

## High-level acquisition / 高阶附魔获取

Each existing enchantment on generated equipment or books rolls independently.
Each selected container also makes one independent roll for at most one extra
single-enchantment high-level book. Equipment and the extra book can therefore
appear together in the same container.

战利品装备或书上的每一条已有附魔分别独立抽取。每个指定箱子或宝库还会独立抽取一次，
最多额外生成一本单附魔高阶附魔书。因此，同一个容器可以同时出现高阶装备与额外高阶附魔书。

| Source / 来源 | VI | VII | VIII | IX | X | Total / 总计 |
|---|---:|---:|---:|---:|---:|---:|
| Stronghold library / 要塞图书馆 | 5.0% | 2.5% | 1.2% | 0.5% | 0.2% | 9.4% |
| Woodland mansion / 林地府邸 | 6.0% | 3.0% | 1.5% | 0.7% | 0.3% | 11.5% |
| Ordinary bastion chests / 普通堡垒遗迹箱子 | 7.0% | 4.0% | 2.0% | 1.0% | 0.4% | 14.4% |
| Ancient city / 远古城市 | 7.0% | 4.5% | 3.0% | 1.5% | 0.8% | 16.8% |
| Normal trial vault / 普通试炼宝库 | 8.0% | 5.0% | 3.0% | 1.5% | 0.7% | 18.2% |
| Bastion treasure / 堡垒遗迹藏宝室 | 8.0% | 5.0% | 3.5% | 2.0% | 1.0% | 19.5% |
| End city treasure / 末地城宝藏 | 9.0% | 6.0% | 4.0% | 2.5% | 1.5% | 23.0% |
| Ominous trial vault / 不祥试炼宝库 | 10.0% | 7.0% | 5.0% | 3.0% | 2.0% | 27.0% |

End City high-level loot is enabled only in the vanilla `minecraft:the_end`
dimension. Reusing the End City loot table in replica or custom dimensions does
not produce level VI-X results.

末地城高阶战利品仅在原版 `minecraft:the_end` 维度中生效。复刻维度或自定义维度即使复用
原版末地城战利品表，也不会生成 VI-X 级结果。

Soul Speed VI-X is exclusive to bastion sources. Wind Burst VI-X is exclusive
to ominous trial vaults. Master librarians sell neither; Mending and Frost
Walker remain in the general loot and librarian pools.

灵魂疾行 VI-X 仅出现在堡垒遗迹来源；风爆 VI-X 仅出现在不祥试炼宝库。
图书管理员不会出售这两种附魔；经验修补和冰霜行者仍属于通用战利品与交易池。

Lunge does not exist in Minecraft 1.21.8, so this branch does not add or backport
it. Lunge support begins only on the Minecraft 1.21.11 branch.

突刺（Lunge）并不存在于 Minecraft 1.21.8，因此本分支不会新增或向旧版本移植该附魔。
突刺支持仅从 Minecraft 1.21.11 分支开始。

Every master librarian receives exactly one persistent, restockable high-level
book trade:

每位大师级图书管理员固定获得一项可补货的高阶附魔书交易：

| Level / 等级 | Chance / 概率 | Cost / 价格 | Max uses per restock / 每次补货次数 |
|---|---:|---|---:|
| VI | 35% | 32 emeralds + book / 32 绿宝石 + 书 | 4 |
| VII | 25% | 40 emeralds + book / 40 绿宝石 + 书 | 3 |
| VIII | 18% | 48 emeralds + diamond / 48 绿宝石 + 钻石 | 2 |
| IX | 12% | 56 emeralds + echo shard / 56 绿宝石 + 回响碎片 | 1 |
| X | 10% | 64 emeralds + netherite ingot / 64 绿宝石 + 下界合金锭 | 1 |

Reputation and curing discounts affect only the emerald cost, never the
catalyst item.

声望与治愈折扣只影响绿宝石价格，不会减免催化材料。

## Requirements / 运行要求

- Minecraft `1.21.8`
- Fabric Loader `0.19.3+`
- Fabric API `0.136.1+1.21.8`
- Java `21+`

This branch is compiled and verified against Fabric API `0.136.1+1.21.8`.

本分支使用 Fabric API `0.136.1+1.21.8` 编译并验证。

For multiplayer, the mod is required on the server and optional on clients. Singleplayer users install it on the client.

多人游戏中服务端必须安装，客户端可选；单人游戏需要在客户端安装。

## Building / 构建

The repository includes a Gradle 9.5.1 wrapper. The build uses Java 21,
official Mojang mappings, Fabric Loom 1.17.17, and downloads the exact
Minecraft 1.21.8 server data required to generate the enchantment overrides. Run:

仓库内置 Gradle 9.5.1 Wrapper。构建使用 Java 21、Mojang 官方映射和
Fabric Loom 1.17.17，并自动下载生成附魔覆盖所需的 Minecraft 1.21.8
服务端数据。运行：

```powershell
.\build.ps1
.\test.ps1
```

Build outputs are written to `build/dist/`.

## License / 许可证

Licensed under the GNU Lesser General Public License v3.0 or later:

`LGPL-3.0-or-later`

See [LICENSE](LICENSE), [COPYING.LESSER](COPYING.LESSER), and [COPYING](COPYING).
