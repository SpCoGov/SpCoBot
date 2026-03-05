package top.spco.qq;

import top.spco.SpCoBot;
import top.spco.core.Platform;
import top.spco.core.PlatformAdapter;

public class QQAdapter extends PlatformAdapter {
    private static QQAdapter instance;

    private QQAdapter() {
        super(Platform.QQ);
        QQNTWebSocketClient.runQQNTWebSocketClient();
    }

    public static QQAdapter getInstance() {
        if (instance == null) {
            instance = new QQAdapter();
        }
        return instance;
    }
}
