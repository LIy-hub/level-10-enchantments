# Level 10 Enchantments

A Fabric mod for Minecraft 26.1.2 that extends selected vanilla enchantments to level X.

十级附魔是一个适用于 Minecraft 26.1.2 的 Fabric 模组，将适合的原版附魔扩展至 X 级。

## Features / 功能

- 29 selected vanilla enchantments can reach level X  
  29 种适合的原版附魔可提升至 X 级
- Rare level VI-X breakthroughs from the enchanting table  
  附魔台有较低概率直接突破至 VI-X 级
- Mending X repairs 8 durability per XP  
  经验修补 X 每点经验修复 8 点耐久
- Universal 100-level anvil cost cap  
  铁砧花费上限为 100 级
- Selected incompatible enchantments can be combined with progressive surcharges  
  部分互斥附魔可以组合，并产生递增的额外花费
- Elytra supports selected chestplate enchantments  
  鞘翅支持部分胸甲附魔
- Up to 25 bookshelves affect enchanting  
  最多 25 个书架可以影响附魔
- Animated rainbow names for level-X enchantments on the client  
  客户端显示 X 级附魔动态彩虹名称

## Requirements / 运行要求

- Minecraft `26.1.2`
- Fabric Loader `0.19.3+`
- Fabric API `0.146.1+` (tested with `0.154.2+26.1.2`)
- Java `25+`

Fabric API `0.146.1+` is required. The currently verified version is `0.154.2+26.1.2`.

需要 Fabric API `0.146.1+`，当前实测版本为 `0.154.2+26.1.2`。

For multiplayer, the mod is required on the server and optional on clients. Singleplayer users install it on the client.

多人游戏中服务端必须安装，客户端可选；单人游戏需要在客户端安装。

## Building / 构建

The current PowerShell build pipeline expects a local Minecraft 26.1.2 server JAR and Fabric libraries in the parent server workspace. Run:

当前 PowerShell 构建流程需要父级服务器工作区中的 Minecraft 26.1.2 服务端 JAR 和 Fabric 依赖。运行：

```powershell
.\build.ps1
.\test.ps1
```

Build outputs are written to `build/dist/`.

## License / 许可证

Licensed under the GNU Lesser General Public License v3.0 or later:

`LGPL-3.0-or-later`

See [LICENSE](LICENSE), [COPYING.LESSER](COPYING.LESSER), and [COPYING](COPYING).
