# common

✨ 星社 Project 核心代码库。

## 📦 模块说明

- `dependency-loader`: 用于加载依赖库。
- `misc`: 杂项功能。
- `rpc`: 跨端通信辅助，gRPC 包装。
- `utils`: 工具合集。
- `hypervisor`: 服务端监测、调控工具。
- `visual`: 视觉，客户端 UI 相关 API。
- `interactive`: 交互框架，其 `inventory` 部分基于 [guiy-compose](https://github.com/MineInAbyss/guiy-compose) 开发。
- `menu`: 运行在生存区域的「手账」功能。
- `whitelist`: 代理端白名单实现。
- `player-database`: 以玩家为单位，使用键值对存储数据。
- `options`: 玩家设置框架。
- `essentials`: 基础功能集合。
- `daily`: 每日签到功能。
- `provider`: 统一管理数据库链接、Redis 缓存连接实例。
- `protocol-checker`: 玩家客户端协议检查。
- `bridge`: 在后端调用代理端 API / 代理端调用后端 API。

## 🔧 构建

> [!NOTE]
>
> 此处以 Linux 系统上的步骤举例。
>
> 如果您使用的是 Windows，可能需要修改部分命令。
>

1. 将本项目拉取到你的设备：`git clone https://github.com/PlutoProject/common.git`
2. 进入项目目录：`cd ./common`
3. 打包构建：`./gradlew shadowJar`

## 👨‍💻 贡献

目前我们还没有制定明确的贡献指南。

如果你是社区中的一位玩家，你可以直接提交 Pull Request，前提是你认为你的修改是有意义且正确的。

## 📄️ 许可

[PlutoProject/common](https://github.com/PlutoProject/common)
在 [GNU Lesser General Public License v3.0](https://www.gnu.org/licenses/lgpl-3.0.html) 下许可。

`interactive` 的 `inventory` 部分在未说明的情况下使用 [MIT License](https://opensource.org/license/mit) 许可。

![license](lgpl-v3.png)
