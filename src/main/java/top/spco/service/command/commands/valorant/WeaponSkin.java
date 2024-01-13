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
package top.spco.service.command.commands.valorant;

import java.net.URL;
import java.util.UUID;

/**
 * Valorant中武器的一个皮肤
 *
 * @author SpCo
 * @version 1.3.0
 * @since 1.3.0
 */
public class WeaponSkin {
    /**
     * 皮肤属性的语言，如名称
     */
    public static final String LANGUAGE_CODE = "zh-TW";
    private final UUID uuid;
    private final String displayName;
    private final URL iconURL;
    private final int cost;

    public WeaponSkin(UUID uuid, String displayName, URL iconURL, int cost) {
        this.uuid = uuid;
        this.displayName = displayName;
        this.iconURL = iconURL;
        this.cost = cost;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public URL getaIconURL() {
        return iconURL;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "WeaponSkin{" +
                "uuid=" + uuid +
                ", displayName='" + displayName + '\'' +
                ", cost=" + cost +
                '}';
    }
}