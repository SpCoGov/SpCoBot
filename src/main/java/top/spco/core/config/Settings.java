/*
 * Copyright 2023 SpCo
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
package top.spco.core.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import top.spco.SpCoBot;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code Settings} 类用于管理和操作应用程序的配置项。
 * 配置项保存在一个YAML文件中，该文件的路径通过构造函数传递。
 * 如果配置文件不存在或为空，将创建一个带有默认值的配置文件。
 * <p>
 * 配置项按照{@link SettingsGroup} 枚举的分组进行组织，每个组包含多个键值对。
 * 每个键值对的键为枚举常量的名称，值为该枚举常量的默认值。
 *
 * @author SpCo
 * @version 0.3.2
 * @since 0.2.1
 */
public class Settings {
    private Map<String, Object> settings;
    private final Map<String, Object> defaultSettings = new HashMap<>();
    private final String filePath;

    /**
     * 构造一个{@code Settings}对象，指定配置文件路径。
     *
     * @param filePath 配置文件的路径
     */
    public Settings(String filePath) {
        this.filePath = filePath;
        loadSettings();
    }

    /**
     * 加载配置项。如果配置文件不存在或为空，将创建一个带有默认值的配置文件。
     */
    public void loadSettings() {
        SpCoBot.logger.info("正在加载配置项");
        try (InputStream input = new FileInputStream(filePath)) {
            Yaml yaml = new Yaml();
            settings = yaml.load(input);
            if (settings == null) {
                SpCoBot.logger.info("正在创建默认配置");
                // 如果配置文件为空，创建一个带有默认值的配置文件
                settings = getDefaultSettings();
                saveSettings();
                SpCoBot.logger.info("默认配置创建完成，请前往" + filePath + "修改配置文件。");
                System.exit(-2);
            }
        } catch (IOException e) {
            SpCoBot.logger.info("正在创建默认配置");
            // 如果文件不存在，创建一个带有默认值的配置文件
            settings = getDefaultSettings();
            saveSettings();
            SpCoBot.logger.info("默认配置创建完成，请前往" + filePath + "修改配置文件。");
            System.exit(-2);
        }
    }

    /**
     * 获取配置项的默认值。
     *
     * @return 包含默认配置项的Map
     */
    private Map<String, Object> getDefaultSettings() {
        defaultSettings.clear();
        // 添加默认配置项
        setDefaultProperty(SettingsVersion.class);
        setDefaultProperty(BotSettings.class);
        setDefaultProperty(DashScopeSettings.class);
        setDefaultProperty(ValorantGroupSettings.class);
        return defaultSettings;
    }

    /**
     * 设置配置项的默认值。
     *
     * @param group 配置项的分组
     * @param <T>   实现了{@link SettingsGroup}接口的枚举类型
     */
    private <T extends Enum<T> & SettingsGroup> void setDefaultProperty(Class<T> group) {
        T[] items = group.getEnumConstants();
        if (items.length == 0) {
            return;
        }
        Map<String, Object> settings = new HashMap<>();
        for (T item : items) {
            settings.put(item.toString(), item.defaultValue());
        }
        defaultSettings.put(items[0].groupName(), settings);
    }

    /**
     * 保存当前配置项到文件。
     */
    public void saveSettings() {
        try (FileWriter writer = new FileWriter(filePath)) {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            Yaml yaml = new Yaml(options);
            yaml.dump(settings, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取配置项的值。
     *
     * @param key 配置项的枚举常量
     * @param <T> 实现了{@link SettingsGroup}接口的枚举类型
     * @return 配置项的值，如果未找到则返回null
     */
    public <T extends Enum<T> & SettingsGroup> Object getProperty(T key) {
        String groupName = key.groupName();
        if (settings.containsKey(groupName) && settings.get(groupName) instanceof Map) {
            var group = (Map<String, Object>) settings.get(groupName);
            return group.get(key.toString());
        } else {
            return null;
        }
    }

    /**
     * 获取配置项的值，并将其转换为long类型。
     *
     * @param key 配置项的枚举常量
     * @param <T> 实现了{@link SettingsGroup}接口的枚举类型
     * @return 配置项的long值
     * @throws IllegalArgumentException 如果配置项的值不是long类型
     */
    public <T extends Enum<T> & SettingsGroup> long getLongProperty(T key) {
        Object value = getProperty(key);
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else {
            throw new IllegalArgumentException("Property " + key + " is not a long");
        }
    }

    /**
     * 设置配置项的值，并保存到文件。
     *
     * @param key   配置项的枚举常量
     * @param value 配置项的新值
     * @param <T>   实现了{@link SettingsGroup}接口的枚举类型
     */
    public <T extends Enum<T> & SettingsGroup> void setProperty(T key, Object value) {
        String groupName = key.groupName();
        if (settings.containsKey(groupName) && settings.get(groupName) instanceof Map) {
            var group = (Map<String, Object>) settings.get(groupName);
            group.put(key.toString(), value);
            settings.put(groupName, group);
        }
        saveSettings();
    }
}