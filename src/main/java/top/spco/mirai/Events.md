### Bot
- [x] Bot 登录完成: BotOnlineEvent
- Bot 离线: BotOfflineEvent
    - [x] 主动: Active
    - [x] 被挤下线: Force
    - [x] 被服务器断开或因网络问题而掉线: Dropped
    - [x] 服务器主动要求更换另一个服务器: RequireReconnect
- Bot 重新登录: BotReloginEvent
- Bot 头像改变: BotAvatarChangedEvent
- Bot 昵称改变: BotNickChangedEvent
- [x] Bot 被戳: NudgeEvent

### 消息
- 被动收到消息：MessageEvent
    - [x] 群消息：GroupMessageEvent
    - [x] 好友消息：FriendMessageEvent
    - [x] 群临时会话消息：GroupTempMessageEvent
    - 陌生人消息：StrangerMessageEvent
    - 其他客户端消息：OtherClientMessageEvent
- 主动发送消息前: MessagePreSendEvent
    - 群消息: GroupMessagePreSendEvent
    - 好友消息: FriendMessagePreSendEvent
    - 群临时会话消息: GroupTempMessagePreSendEvent
    - 陌生人消息：StrangerMessagePreSendEvent
- 从其他客户端同步消息 MessageSyncEvent
    - 群消息: GroupMessageSyncEvent
    - 好友消息: FriendMessageSyncEvent
    - 群临时会话消息: GroupTempMessageSyncEvent
    - 陌生人消息: StrangerMessageSyncEvent
- 主动发送消息后: MessagePostSendEvent
    - [x] 群消息: GroupMessagePostSendEvent
    - 好友消息: FriendMessagePostSendEvent
    - 群临时会话消息: GroupTempMessagePostSendEvent
    - 陌生人消息：StrangerMessagePostSendEvent
    - 其他客户端消息：OtherClientMessagePostSendEvent
- 消息撤回: MessageRecallEvent
    - [x] 好友撤回: FriendRecall
    - [x] 群撤回: GroupRecall
    - 群临时会话撤回: TempRecall ???
- 图片上传前: BeforeImageUploadEvent
- 图片上传完成: ImageUploadEvent
    - 图片上传成功: Succeed
    - 图片上传失败: Failed
- [x] 戳一戳: NudgeEvent

### 群
- 机器人被踢出群或在其他客户端主动退出一个群: BotLeaveEvent
    - 机器人主动退出一个群: Active
    - 机器人被管理员或群主踢出群: Kick
- 机器人在群里的权限被改变: BotGroupPermissionChangeEvent
- 机器人被禁言: BotMuteEvent
- 机器人被取消禁言: BotUnmuteEvent
- 机器人成功加入了一个新群: BotJoinGroupEvent

#### 群设置
- 群设置改变: GroupSettingChangeEvent
    - 群名改变: GroupNameChangeEvent
    - 入群公告改变: GroupEntranceAnnouncementChangeEvent
    - 全员禁言状态改变: GroupMuteAllEvent
    - 匿名聊天状态改变: GroupAllowAnonymousChatEvent
    - 允许群员邀请好友加群状态改变: GroupAllowMemberInviteEvent

#### 群成员
##### 成员列表变更
- 成员已经加入群: MemberJoinEvent
    - 成员被邀请加入群: Invite
    - 成员主动加入群: Active

- 成员已经离开群: MemberLeaveEvent
    - 成员被踢出群: Kick
    - 成员主动离开群: Quit

- [x] 一个账号请求加入群: MemberJoinRequestEvent
- [x] 机器人被邀请加入群: BotInvitedJoinGroupRequestEvent

##### 名片和头衔
- 成员群名片改动: MemberCardChangeEvent
- 成员群特殊头衔改动: MemberSpecialTitleChangeEvent
- 成员群荣誉改变: MemberHonorChangeEvent

##### 成员权限
- 成员权限改变: MemberPermissionChangeEvent

##### 动作
- 群成员被禁言: MemberMuteEvent
- 群成员被取消禁言: MemberUnmuteEvent

### 好友
- 好友昵称改变: FriendRemarkChangeEvent
- 成功添加了一个新好友: FriendAddEvent
- 好友已被删除: FriendDeleteEvent
- [x] 一个账号请求添加机器人为好友: NewFriendRequestEvent
- 好友头像改变: FriendAvatarChangedEvent
- 好友昵称改变: FriendNickChangedEvent
- 好友输入状态改变: FriendInputStatusChangedEvent