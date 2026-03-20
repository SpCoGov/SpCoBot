package top.spco.qq;

import top.spco.core.Platform;
import top.spco.core.PlatformAdapter;

public class QQAdapter extends PlatformAdapter {
    private static QQAdapter instance;
    private final QQNTWebSocketClient client;

    private QQAdapter() {
        super(Platform.QQ);
        this.client = QQNTWebSocketClient.runQQNTWebSocketClient();
    }

    public static QQAdapter getInstance() {
        if (instance == null) {
            instance = new QQAdapter();
        }
        return instance;
    }

    public QQNTWebSocketClient getClient() {
        return client;
    }
}
