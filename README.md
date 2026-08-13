# Level 10 Enchantments / 十级附魔

[![CI](https://github.com/LIy-hub/level-10-enchantments/actions/workflows/ci.yml/badge.svg)](https://github.com/LIy-hub/level-10-enchantments/actions/workflows/ci.yml)
[![CodeQL](https://github.com/LIy-hub/level-10-enchantments/actions/workflows/codeql.yml/badge.svg)](https://github.com/LIy-hub/level-10-enchantments/actions/workflows/codeql.yml)

A Fabric mod for Minecraft 1.20.1 that extends 26 selected vanilla enchantments
to level X without adding new enchantments.

This visual compatibility update is versioned `1.5.3`.

十级附魔是适用于 Minecraft 1.20.1 的 Fabric 模组，将 26 种原版附魔扩展至
X 级，不新增自定义附魔。本次视觉兼容更新版本为 `1.5.3`。

## Features / 功能

- 26 selected vanilla enchantments can reach level X through gated loot and
  master-librarian trades.<br>
  26 种原版附魔可通过限定结构战利品和大师级图书管理员交易达到 X 级。
- The enchanting table remains capped at each enchantment's vanilla maximum.
  Up to 25 bookshelves only improve the chance of vanilla-maximum and
  multi-enchantment results.<br>
  附魔台仍受每种附魔的原版等级上限约束；最多 25 个书架只提高获得原版最高级和
  多附魔结果的概率。
- Anvils increase equal enchantments only below the vanilla maximum. Operation
  costs are capped at 100 levels.<br>
  铁砧只会在原版等级上限以下合并升级同级附魔，操作花费最高为 100 级。
- Protection-family and damage-family combinations are allowed with progressive
  anvil surcharges.<br>
  保护类和伤害类附魔允许组合，并按组合数量增加铁砧附加花费。
- Elytra accepts Protection, Fire Protection, Blast Protection, Projectile
  Protection, and Thorns.<br>
  鞘翅可接受保护、火焰保护、爆炸保护、弹射物保护和荆棘。
- Mending scales from 2 durability per XP at level I to 8 at level X.<br>
  经验修补从 I 级每点经验修复 2 点耐久，线性提升到 X 级修复 8 点。
- Thorns scales to a 100% trigger chance and 10–15 retaliation damage at level X
  without damaging the enchanted item.<br>
  荆棘在 X 级达到 100% 触发率和 10–15 点反伤，且不会损耗附魔物品耐久。
- Clients with the mod installed see a stable per-enchantment aurora gradient
  on level X and every higher level. Higher levels move faster and use a richer
  hue span with a bounded breathing highlight; different enchantments on the
  same item use deterministic, de-synchronised phases. XI-XIII are localized
  and XIV+ uses a stable Roman-numeral fallback. Dynamic styling runs only
  after tooltip extensions finish, so Enchantment Descriptions remains stable;
  the original static enchantment colour is transferred only to that exact
  enchantment's description and never bleeds into lore or attributes. The
  server remains authoritative; clients without the mod simply do not see the
  animation.<br>
  安装模组的客户端会看到 X 级及所有更高等级的稳定极光渐变；等级越高，动画越快、
  色相层次越丰富，并带有受限的呼吸高光。同一物品上的不同附魔按稳定 ID 错相，
  不会同步扫色。XI–XIII 具备本地化文本，XIV 以上使用稳定的罗马数字回退。动态样式
  会在其他提示扩展完成后再应用，因此兼容 Enchantment Descriptions；附魔原有静态
  颜色只会转移到该附魔自己的解释行，不会污染 Lore 或属性行。未安装本模组的客户端
  仍可连接，但不会看到动画。

## Supported enchantments / 支持的附魔

Sharpness, Smite, Bane of Arthropods, Impaling, Power, Fire Aspect, Knockback,
Punch, Piercing, Sweeping Edge, Looting, Riptide, Loyalty, Efficiency, Fortune,
Unbreaking, Luck of the Sea, Protection, Fire Protection, Blast Protection,
Projectile Protection, Thorns, Respiration, Frost Walker, Soul Speed, and
Mending.

锋利、亡灵杀手、节肢杀手、穿刺、力量、火焰附加、击退、冲击、穿透、横扫之刃、
抢夺、激流、忠诚、效率、时运、耐久、海之眷顾、保护、火焰保护、爆炸保护、
弹射物保护、荆棘、水下呼吸、冰霜行者、灵魂疾行和经验修补。

Density, Wind Burst, and Lunge do not exist in Minecraft 1.20.1, so this branch
does not add or backport them.

致密、风爆和突刺并不存在于 Minecraft 1.20.1，因此本分支不会新增或回移这些附魔。

## High-level acquisition / 高阶附魔获取

Each existing enchantment on generated equipment or books rolls independently.
Each selected container also makes one independent roll for at most one extra
single-enchantment high-level book. Both results may appear in the same
container.

战利品装备或附魔书上的每条已有附魔分别独立抽取。每个指定容器还会独立抽取一次，
最多额外生成一本单附魔高阶附魔书；两类结果可以同时出现。

| Source / 来源 | VI | VII | VIII | IX | X | Total / 总计 |
|---|---:|---:|---:|---:|---:|---:|
| Stronghold library / 要塞图书馆 | 5.0% | 2.5% | 1.2% | 0.5% | 0.2% | 9.4% |
| Woodland mansion / 林地府邸 | 6.0% | 3.0% | 1.5% | 0.7% | 0.3% | 11.5% |
| Ordinary bastion chests / 普通堡垒遗迹箱子 | 7.0% | 4.0% | 2.0% | 1.0% | 0.4% | 14.4% |
| Ancient city / 远古城市 | 7.0% | 4.5% | 3.0% | 1.5% | 0.8% | 16.8% |
| Bastion treasure / 堡垒遗迹藏宝室 | 8.0% | 5.0% | 3.5% | 2.0% | 1.0% | 19.5% |
| End city treasure / 末地城宝藏 | 9.0% | 6.0% | 4.0% | 2.5% | 1.5% | 23.0% |

End City high-level loot works only in the vanilla `minecraft:the_end`
dimension. Soul Speed VI-X is exclusive to bastion sources. Trial vaults do not
exist in this version and are not treated as sources.

末地城高阶战利品仅在原版 `minecraft:the_end` 维度中生效。灵魂疾行 VI-X
仅来自堡垒遗迹。此版本不存在试炼宝库，因此不会将其作为来源。

Every master librarian receives exactly one persistent, restockable high-level
book trade. Soul Speed is excluded from this trade pool.

每位大师级图书管理员固定获得一项可补货、可持久保存的高阶附魔书交易；灵魂疾行
不进入该交易池。

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

- Minecraft `1.20.1`
- Fabric Loader `0.19.3+`
- Fabric API `0.92.11+1.20.1` or newer compatible 1.20.1 build
- Java `17+`

This branch is compiled and checked against exactly Fabric API
`0.92.11+1.20.1`.

本分支使用 Fabric API `0.92.11+1.20.1` 精确编译并检查。

## Building / 构建

The repository includes a Gradle 8.14.1 wrapper. The branch uses Java 17,
official Mojang mappings, Fabric Loom 1.10.5, and legacy code/mixin adapters
because Minecraft 1.20.1 predates data-driven enchantments.

仓库内置 Gradle 8.14.1 Wrapper。本分支使用 Java 17、Mojang 官方映射和
Fabric Loom 1.10.5；由于 Minecraft 1.20.1 尚未采用数据驱动附魔，本分支通过
旧版代码 API 与 Mixin 完成等价适配。

```powershell
.\build.ps1
.\test.ps1
```

Build outputs are written to `build/dist/`.

构建产物输出到 `build/dist/`。

## License / 许可证

Licensed under the GNU Lesser General Public License v3.0 or later
(`LGPL-3.0-or-later`). See [LICENSE](LICENSE), [COPYING.LESSER](COPYING.LESSER),
and [COPYING](COPYING).

本项目依据 GNU 宽通用公共许可证 v3.0 或更高版本发布。
