package top.spco.core.config;

/**
 * @author SpCo
 * @version 4.1.0
 * @since 4.1.0
 */
public class PaymentApiConfig extends Config {
    public PaymentApiConfig() {
        super("payment");
        init();
    }

    private final ConfigSpecHelper.ConfigValue<String> WECHAT_PAY_MERCHANT_ID = builder
            .define("wechat_pay_merchant_id", "");
    private final ConfigSpecHelper.ConfigValue<String> WECHAT_PAY_PRIVATE_KEY_PATH = builder
            .define("wechat_pay_private_key_path", "");
    private final ConfigSpecHelper.ConfigValue<String> WECHAT_PAY_MERCHANT_SERIAL_NUMBER = builder
            .define("wechat_pay_merchant_serial_number", "");
    private final ConfigSpecHelper.ConfigValue<String> WECHAT_PAY_API_V3_KEY = builder
            .define("wechat_pay_api_v3_key", "");
    private final ConfigSpecHelper.ConfigValue<String> WECHAT_PAY_APP_ID = builder
            .define("wechat_pay_app_id", "");
    private final ConfigSpecHelper.ConfigValue<String> ALIPAY_APP_ID = builder
            .define("alipay_app_id", "");
    private final ConfigSpecHelper.ConfigValue<String> ALIPAY_PRIVATE_KEY = builder
            .define("alipay_private_key", "");
    private final ConfigSpecHelper.ConfigValue<String> ALIPAY_ALIPAY_PUBLIC_KEY = builder
            .define("alipay_alipay_public_key", "");
    private final ConfigSpecHelper.ConfigValue<Integer> NOTIFY_SERVER_PORT = builder
            .define("notify_server_port", 6698);
    private final ConfigSpecHelper.ConfigValue<String> NOTIFY_URL = builder
            .define("notify_url", "https://notify.url/");

    public String getWechatPayMerchantId() {
        return WECHAT_PAY_MERCHANT_ID.get();
    }

    public String getWechatPayPrivateKeyPath() {
        return WECHAT_PAY_PRIVATE_KEY_PATH.get();
    }

    public String getWechatPayMerchantSerialNumber() {
        return WECHAT_PAY_MERCHANT_SERIAL_NUMBER.get();
    }

    public String getWechatPayApiV3Key() {
        return WECHAT_PAY_API_V3_KEY.get();
    }

    public String getWechatPayAppId() {
        return WECHAT_PAY_APP_ID.get();
    }

    public String getAlipayAppId() {
        return ALIPAY_APP_ID.get();
    }

    public String getAlipayPrivateKey() {
        return ALIPAY_PRIVATE_KEY.get();
    }

    public String getAlipayAlipayPublicKey() {
        return ALIPAY_ALIPAY_PUBLIC_KEY.get();
    }

    public int getNotifyServerPort() {
        return NOTIFY_SERVER_PORT.get();
    }

    public String getNotifyUrl() {
        return NOTIFY_URL.get();
    }
}
