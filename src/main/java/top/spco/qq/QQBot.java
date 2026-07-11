package top.spco.qq;

import top.spco.api.Bot;
import top.spco.api.Group;
import top.spco.api.User;

public class QQBot extends Bot {
    private final String name;
    private final String id;

    public QQBot(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public Group getGroup(String id) {
        // TODO: 实现这个
        return null;
    }

    @Override
    public User getUser(String id) {
        // TODO: 实现这个
        return null;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
