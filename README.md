# 目录

- [关于](#关于)
- [支持的聊天平台](#支持的聊天平台)
- [部署](#部署)
- [许可证](#许可证)
- [鸣谢](#鸣谢)
- [注意事项](#注意事项)

# 关于

SpCoBot 是一个可运行于多平台的综合机器人，旨在提供各种服务和功能来增强用户体验。

# 支持的聊天平台

| 聊天平台     | 此项目已支持 | 使用的SDK                                                     |
|----------|--------|------------------------------------------------------------|
| QQ       | ✅      | [Mirai](https://github.com/mamoe/mirai)                    |
| Telegram | ✅      | [TelegramBots](https://github.com/rubenlagus/TelegramBots) |
| KOOK     | ❌      | [JKOOK](https://github.com/SNWCreations/JKook)             |

# 部署

SpCoBot 可以用作插件和应用程序。SpCoBot 作为插件时，具体的部署方法请见对应SDK的插件安装方法。作为应用程序时，使用以下参数：
```
java -jar SpCoBot.jar +<平台代码>
```
例如：
```
java -jar SpCoBot.jar +tg
```
目前，一个 SpCoBot 实例仅支持一个聊天平台和一个机器人。

# 鸣谢

- 本项目部分代码源自 [Fabric](https://github.com/FabricMC/fabric) ,
  版权归 [Fabric所有贡献者](https://github.com/FabricMC/fabric/graphs/contributors) 所有。
- 本项目部分代码源自 [Forge](https://github.com/MinecraftForge/MinecraftForge) ,
  版权归 [Forge所有贡献者](https://github.com/MinecraftForge/MinecraftForge/graphs/contributors) 所有。

## 赞助商

[<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png" width="200"/>](https://www.jetbrains.com/)

# 许可证

本项目遵循 [Apache License Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源许可证。

## 条款概要

- **开源要求：** 基于本项目的任何衍生项目必须以开源方式发布。
- **商用禁止：** 禁止用于商业用途。
- **引用声明：** 若你引用了本项目或其衍生项目，需要在描述或应用的任意部位明确提及本项目的使用。
- **修改声明：**
  若你对本项目进行修改并发布，或参考本项目内部实现发布另一个项目，必须在文章首部或相关内容首次出现的位置明确声明来源于本仓库 [SpCoBot](https://github.com/SpCoGov/SpCoBot)。

## 注意事项

- 不得扭曲或隐藏该项目的开源性质。你需要在项目的相关文档或应用程序中明确表明其使用了本项目或其衍生项目，并且这些项目是免费且开源的。

在继续使用或参与本项目的开发前，请务必详细阅读和理解 [Apache License Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
的完整内容，以确保你对许可证条款有充分的了解。

如果你有任何疑问或需要进一步的许可说明，请联系项目维护者。
