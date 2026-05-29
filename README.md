# 🎰 LilisLottery — 高级抽奖系统

> 基于 Spigot 1.20.6 的高可定制 Minecraft 抽奖插件

---

## 📋 项目简介

**LilisLottery** 是一款 Minecraft 服务器抽奖插件，提供基于 GUI 的交互式抽奖体验。支持自定义配方、权重抽奖、概率操控加成物等高级功能，所有配置均可通过 YAML 文件灵活调整。

- **作者**: LozinLilis
- **版本**: `0.0.2-SNAPSHOT`
- **核心依赖**: Spigot 1.20.6、Java 1.8

---

## ✨ 核心特性

- **🗂️ 可视化 GUI** — 通过箱子界面完成抽奖全流程操作，交互直观
- **⚖️ 权重抽奖** — 支持为每个奖品独立配置权重，按概率随机抽取
- **🧪 概率操控公式** — 配方可附加"加成物"，通过 JavaScript 表达式动态调整各奖品权重
- **📦 NBT 物品识别** — 基于 NBT 标签的物品内部识别机制，不必拘泥于物品材质
- **⚙️ YAML 驱动配置** — 所有配方、GUI 布局、插件参数均由 YAML 文件控制
- **🔊 声音反馈** — 抽奖关键节点播放音效（可在 `config.yml` 中开关）
- **🔄 热重载** — 通过 `/llo reload` 指令动态重载配方与配置
- **♻️ NBTAPI 隔离** — 使用 Maven Shade 插件将 NBTAPI 重定位至内部命名空间，避免与其他插件冲突

---

## 🚀 快速开始

### 环境要求

| 依赖项 | 版本要求 |
|--------|----------|
| Java | 1.8+ |
| Spigot/Paper | 1.20.6+ |
| NBTAPI | 2.15.0（已内置打包） |

### 安装方式

1. 将编译产物 `LilisLottery-*.jar` 放入服务器的 `plugins/` 目录
2. 重启服务器（或使用插件管理工具加载）
3. 插件自动生成默认配置文件至 `plugins/LilisLottery/`

```
plugins/LilisLottery/
├── config.yml          # 插件主配置
├── lotteryGUI.yml      # 抽奖 GUI 界面配置
└── Recipes/
    └── example.yml     # 示例配方（可参考编写）
```

### 指令一览

| 指令 | 别名 | 说明 |
|------|------|------|
| `/lilis_lot` | `/llo` | 打开抽奖主界面 |
| `/lilis_lot reload` | `/llo reload` | 重载插件配置与配方 |

---

## ⚙️ 配置文件说明

### `config.yml`

```yaml
debug: true                     # 是否启用调试输出
required_key: "lot"             # 消耗材料的 NBT 识别键
lucky_key: "lluc"               # 加成物的 NBT 识别键
precision: "#.#"                # 概率展示精度（DecimalFormat 模式）
enable_sound: true              # 是否启用声音
```

### `lotteryGUI.yml`

定义抽奖主界面的标题、尺寸及各功能槽位（预览材料、结果预览、加成物槽、确认按钮等）。

### `Recipes/*.yml` — 配方文件

```yaml
id: "示例配方"                   # 配方唯一 ID（重复则相关配方全部禁用）

required_item:                  # 消耗材料列表
  - inner_id: "example_1"       # 内部识别值（物品 NBT lot 键的值）
    amount: 1                   # 每次抽奖消耗数量
    name: "example 1"           # 展示名称

reward:                         # 奖品定义
  奖品A:
    desc: "这是一等奖"           # 奖品描述
    command:                    # 中奖后执行的指令（{player} 替换为玩家名）
      - "give {player} diamond 10"
    weight: 40                  # 中奖权重（越大越容易中）

lucky:                          # 加成物（可选）
  lucky_1:                      # 加成物标识
    amount: 1                   # 消耗数量
    formula:                    # 概率操控公式
      - "奖品A: 奖品A + 10"      # 格式: "奖品ID: JavaScript 表达式"
```

---

## 🏗️ 技术架构

```
lilislottery/
├── command/        # 指令处理（GUI 打开、重载）
├── event/          # 事件监听（玩家进出、GUI 交互）
├── lottery/
│   ├── cache/      # 配方缓存管理
│   ├── info/       # 配方数据结构定义
│   └── recipe/     # 配方、奖励、消耗物、加成物实体
├── main/           # 插件入口、指令注册、事件注册
├── tab/            # Tab 补全
├── ui/             # GUI 创建、动作枚举、服务层
└── utils/item/     # 物品构造、消耗计算、NBT 操作
```

---

## 🛠️ 构建说明

```bash
# 克隆仓库
git clone <repo-url>

# Maven 构建
mvn clean package

# 输出目录: target/LilisLottery-*.jar
```

Maven Shade 插件会将 NBTAPI 重定位至 `org.lozin.shaded.nbtapi`，确保与服务器上其他使用 NBTAPI 的插件不发生类冲突。

---

## 📅 项目信息

- **项目完工时间**: 2026 年 5 月 29 日
- **本文档生成模型**: Qoder（基于 Qwen AI 模型）

---

> *May the odds be ever in your favor.* 🍀
