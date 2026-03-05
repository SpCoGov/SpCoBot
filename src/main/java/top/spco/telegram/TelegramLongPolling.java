package top.spco.telegram;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import top.spco.events.MessageEvents;

class TelegramLongPolling implements LongPollingSingleThreadUpdateConsumer {
    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            TelegramBot bot = new TelegramBot(TelegramAdapter.getSelf());
            TelegramMessage telegramMessage = new TelegramMessage(message);
            if (message.isUserMessage()) {
                MessageEvents.PRIVATE_MESSAGE.invoker().onPrivateMessage(bot, new TelegramUser(message.getFrom()), telegramMessage, message.getDate());
            }
            if (message.isGroupMessage() || message.isSuperGroupMessage()) {
                TelegramGroup group = new TelegramGroup(message.getChat());
                TelegramUser member = (TelegramUser) group.getMember(message.getFrom().getId() + "");
                MessageEvents.GROUP_MESSAGE.invoker().onGroupMessage(bot, group, member, telegramMessage, message.getDate());
            }
            if (message.isChannelMessage()) {
                // TODO: 修复这个
                //MessageEvents.CHANNEL_MESSAGE.invoker().onChannelMessage(bot, new TelegramChannel(message.getChat()), new TelegramUser(message.getSenderChat()), telegramMessage, message.getDate());
            }
        }
    }
}
