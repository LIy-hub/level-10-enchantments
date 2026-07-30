# Level 10 Enchantments / 十级附魔

Level 10 Enchantments extends selected vanilla enchantments to level X while
keeping high levels behind structure loot and master-librarian trades. It does
not add custom enchantments.

十级附魔将选定的原版附魔扩展至 X 级，并让高阶等级继续受结构战利品和大师级
图书管理员交易限制；模组不新增自定义附魔。

## Minecraft 1.20.2 features / Minecraft 1.20.2 功能

- 26 supported vanilla enchantments; Density, Wind Burst, and Lunge are absent
  from this Minecraft version and are not backported.<br>
  支持 26 种原版附魔；此版本不存在致密、风爆和突刺，本模组不会回移它们。
- Enchanting-table results stay within vanilla level maxima; up to 25
  bookshelves improve vanilla-maximum and multi-enchantment results.<br>
  附魔台结果仍受原版等级上限约束；最多 25 个书架提高原版最高级与多附魔结果概率。
- Equal-level anvil merging stops at the vanilla maximum; all operations are
  capped at 100 levels.<br>
  同级铁砧合并在原版上限处停止；所有操作花费最高为 100 级。
- Selected protection and damage enchantments can coexist with progressive
  anvil surcharges; selected chestplate enchantments work on Elytra.<br>
  指定保护类和伤害类附魔可在递增花费下共存；指定胸甲附魔可用于鞘翅。
- Mending scales from 2 durability per XP at I to 8 at X. Thorns X has a 100%
  chance to deal 10–15 retaliation damage without durability loss.<br>
  经验修补从 I 级每点经验修复 2 点提升至 X 级 8 点；荆棘 X 必定造成 10–15 点
  反伤且不消耗耐久。
- Six version-existing structure source profiles use independent VI-X rolls for
  existing enchantments and one possible extra enchanted book. Soul Speed VI-X
  remains bastion-only; End City results remain End-dimension-only.<br>
  六类本版本已有结构来源会对已有附魔独立抽取 VI-X，并另行抽取至多一本高阶附魔书。
  灵魂疾行 VI-X 仅来自堡垒遗迹；末地城结果仅在末地维度生效。
- Every master librarian receives exactly one persistent, restockable VI-X
  trade using the documented 35/25/18/12/10 level distribution. Soul Speed is
  excluded.<br>
  每位大师级图书管理员固定获得一项可持久保存、可补货的 VI-X 交易，等级概率为
  35/25/18/12/10；灵魂疾行不进入交易池。
- An optional client installation displays animated aurora-gradient level-X
  names. Server gameplay works for clients without the mod.<br>
  客户端可选安装，用于显示 X 级名称的动态极光渐变；未安装客户端仍可参与服务端玩法。

## Requirements / 运行要求

- Minecraft `1.20.2`
- Fabric Loader `0.19.3+`
- Fabric API `0.91.6+1.20.2`
- Java `17+`

Dedicated servers require the mod. Multiplayer clients may omit it; singleplayer
users install it on the client.

专用服务端必须安装。多人客户端可不安装；单人游戏需在客户端安装。

## License / 许可证

GNU Lesser General Public License v3.0 or later (`LGPL-3.0-or-later`).
Every release includes a corresponding sources JAR.

GNU 宽通用公共许可证 v3.0 或更高版本。每个版本均提供对应的源码 JAR。
