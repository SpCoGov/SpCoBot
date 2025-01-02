/*
 * Copyright 2025 SpCo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.service.command.commands;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import top.spco.SpCoBot;
import top.spco.api.Bot;
import top.spco.api.Friend;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.service.chat.ChatBuilder;
import top.spco.service.chat.ChatType;
import top.spco.service.chat.Stage;
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMarker;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.CommandScope;
import top.spco.service.command.exceptions.CommandSyntaxException;
import top.spco.service.command.usage.Usage;
import top.spco.service.command.usage.UsageBuilder;
import top.spco.service.command.usage.parameters.IntegerParameter;
import top.spco.service.command.usage.parameters.SelectionParameter;
import top.spco.trade.PaymentMethod;
import top.spco.trade.Trade;
import top.spco.user.BotUser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@CommandMarker
public class RechargeCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"recharge"};
    }

    @Override
    public String getDescriptions() {
        return "充值";
    }

    @Override
    public List<Usage> getUsages() {
        return List.of(
                new UsageBuilder(getLabels()[0], "充值")
                        .add(new IntegerParameter("金额", false, null, 1, 5000000))
                        .build(),
                new UsageBuilder(getLabels()[0], "指定充值方式充值")
                        .add(new IntegerParameter("金额", false, null, 1, 5000000))
                        .add(new SelectionParameter("支付方式", false, null, "alipay", "wechat"))
                        .build());
    }

    @Override
    public CommandScope getScope() {
        return CommandScope.ONLY_PRIVATE;
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) throws CommandSyntaxException {
        ChatType ct = from instanceof Friend ? ChatType.FRIEND : ChatType.GROUP_TEMP;
        ChatBuilder chatBuilder = new ChatBuilder(ct, sender);
        AtomicReference<String> paymentMethodName = new AtomicReference<>();
        AtomicReference<PaymentMethod> method = new AtomicReference<>();
        if (usageName.equals("充值")) {
            chatBuilder.addStage(new Stage(() -> "请选择支付方式：\n\n“微信支付”或“支付宝” \n\n注：充值过程中可以随时发送“退出”来退出充值。",
                    (chat, bot1, source, sender1, message1, time1) -> {
                        if (message1.toMessageContext().equals("退出")) {
                            chat.stop();
                            return;
                        }
                        switch (message1.toMessageContext()) {
                            case "微信支付" -> {
                                paymentMethodName.set(message1.toMessageContext());
                                method.set(PaymentMethod.WECHAT_PAY);
                                chat.next();
                            }
                            case "支付宝" -> {
                                paymentMethodName.set(message1.toMessageContext());
                                method.set(PaymentMethod.ALIPAY);
                                chat.next();
                            }
                            default -> {
                                source.quoteReply(message1, "请发送“微信支付”或“支付宝”");
                                chat.replay();
                            }
                        }
                    }));
        } else {
            switch ((String) meta.getParams().get("支付方式")) {
                case "alipay" -> {
                    paymentMethodName.set("支付宝");
                    method.set(PaymentMethod.ALIPAY);
                }
                case "wechat" -> {
                    paymentMethodName.set("微信支付");
                    method.set(PaymentMethod.WECHAT_PAY);
                }
            }
        }
        int amount = (int) meta.getParams().get("金额");
        String description = amount + " StarCoin";
//        if (method.get() == PaymentMethod.ALIPAY) {
//            chatBuilder.addStage(new Stage(() -> "支付宝支付方式尚未开放。",
//                    (chat, bot1, source, sender1, message1, time1) -> {
//                        chat.stop();
//                    }));
//        }
        chatBuilder.addStage(new Stage(() -> "交易信息：\n交易金额：" + amount + "CNY\n商品：" + description + "\n支付方式：" + paymentMethodName.get() + "\n\n发生“确认”以确认支付信息。请添加机器人为好友，以便能收到支付成功的通知。",
                (chat, bot1, source, sender1, message1, time1) -> {
                    if (message1.toMessageContext().equals("退出")) {
                        chat.stop();
                        return;
                    }
                    if (!message1.toMessageContext().equals("确认")) {
                        source.quoteReply(message1, "请输入“确认”或“退出”");
                        chat.replay();
                    }
                    Trade trade = null;
                    switch (method.get()) {
                        case WECHAT_PAY ->
                                trade = SpCoBot.getInstance().getRechargeSystem().createWechatPay(user, amount * 100, description);
                        case ALIPAY ->
                                trade = SpCoBot.getInstance().getRechargeSystem().createAlipay(user, amount, description);
                    }
                    try {
                        QRCodeWriter qrCodeWriter = new QRCodeWriter();
                        BitMatrix matrix = qrCodeWriter.encode(trade.getQrCode(), BarcodeFormat.QR_CODE, 256, 256);
                        int width = matrix.getWidth();
                        int height = matrix.getHeight();
                        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                        // 设置二维码颜色
                        int onColor = 0xFF000000; // 黑色
                        int offColor = 0xFFFFFFFF; // 白色
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                image.setRGB(x, y, matrix.get(x, y) ? onColor : offColor);
                            }
                        }
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", os);
                        InputStream is = new ByteArrayInputStream(os.toByteArray());

                        Message<?> qrCodeMessage = SpCoBot.getInstance().getMessageService().toImage(is, sender1).toMessage().append("订单号：" + trade.getTradeNo());
                        from.quoteReply(message1, qrCodeMessage);
                        chat.stop();
                    } catch (IOException | WriterException e) {
                        from.handleException(message1, "创建二维码失败", e);
                    }
                }));
        chatBuilder.build().start();
    }
}