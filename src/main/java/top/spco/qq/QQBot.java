package top.spco.qq;

import top.spco.api.Bot;
import top.spco.api.Group;

public class QQBot extends Bot {
    private final String name;
    private final String id;

    public QQBot(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public Group getGroup(String id) {
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
