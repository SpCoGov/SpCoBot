package top.spco.telegram;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import top.spco.SpCoBot;
import top.spco.events.MessageEvents;

import java.util.List;

class TelegramLongPolling implements LongPollingSingleThreadUpdateConsumer {
    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            TelegramBot bot = new TelegramBot(Telegram.getSelf());
            TelegramMessage telegramMessage = new TelegramMessage(message);
            if (message.isUserMessage()) {
                MessageEvents.FRIEND_MESSAGE.invoker().onFriendMessage(bot, new TelegramFriend(message.getFrom()), telegramMessage, message.getDate());
            }
            if (message.isGroupMessage() || message.isSuperGroupMessage()) {
                TelegramGroup group = new TelegramGroup(message.getChat());
                TelegramMember member = (TelegramMember) group.getMember(message.getFrom().getId());
                MessageEvents.GROUP_MESSAGE.invoker().onGroupMessage(bot, group, member, telegramMessage, message.getDate());
            }
            if (message.isChannelMessage()) {
                MessageEvents.CHANNEL_MESSAGE.invoker().onChannelMessage(bot, new TelegramChannel(message.getChat()), new TelegramUser(message.getSenderChat()), telegramMessage, message.getDate());
            }
//            if (message.isUserMessage()) {
//                SpCoBot.LOGGER.info("{}|{}", message.getText(), message.toString());
//                List<Message> sentMessage = TelegramMessageSender.sendMessage(Telegram.getInstance().telegramClient, String.valueOf(message.getChatId()), message);
//                for (Message m : sentMessage) {
//                    SpCoBot.LOGGER.info("机器人发送：{}", m.toString());
//                }
//            }
        }
    }
}
