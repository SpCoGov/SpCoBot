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
package top.spco.service.command.commands.valorant;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * 表示Valorant商店中随机刷新的4个皮肤
 *
 * @author SpCo
 * @version 1.3.0
 * @since 1.3.0
 */
public class  SkinsPanelLayoutContainer {
    private SkinsPanelLayout SkinsPanelLayout;

    public SkinsPanelLayout getSkinsPanelLayout() {
        return SkinsPanelLayout;
    }
    public static class SkinsPanelLayout {
        @SerializedName("SingleItemStoreOffers")
        private SingleItemStoreOffer[] singleItemStoreOffers;

        @SerializedName("SingleItemOffersRemainingDurationInSeconds")
        private int remainingTime;

        public int getRemainingTime() {
            return remainingTime;
        }

        public SingleItemStoreOffer[] getSingleItemStoreOffers() {
            return singleItemStoreOffers;
        }

        @Override
        public String toString() {
            return "SkinsPanelLayout{" +
                    "singleItemStoreOffers=" + Arrays.toString(singleItemStoreOffers) +
                    ", remainingTime=" + remainingTime +
                    '}';
        }

        /**
         * 获得每日商店的 {@code WeaponSkin} 对象
         *
         * @return 返回 {@code WeaponSkin} 对象列表
         */
        public LinkedList<WeaponSkin> getSkins() throws IOException {
            LinkedList<WeaponSkin> skins = new LinkedList<>();
            for (var offer : this.singleItemStoreOffers) {
                HttpGet request = new HttpGet("https://valorant-api.com/v1/weapons/skinlevels/" + offer.offerId + "?language=" + WeaponSkin.LANGUAGE_CODE);
                try (CloseableHttpClient session = HttpClients.custom().build()) {
                    HttpResponse response = session.execute(request);
                    JsonObject responseData = JsonParser.parseString(EntityUtils.toString(response.getEntity())).getAsJsonObject();
                    if (responseData.get("status").getAsInt() == 200) {
                        JsonObject data = responseData.get("data").getAsJsonObject();
                        String displayName = data.get("displayName").getAsString();
                        String iconURL = data.get("displayIcon").getAsString();
                        skins.add(new WeaponSkin(UUID.fromString(offer.getOfferId()), displayName, new URL(iconURL), offer.getCost().values().iterator().next()));
                    } else {
                        throw new RuntimeException("Failed to obtain skin information: " + responseData);
                    }
                }
            }
            return skins;
        }

        public static class SingleItemStoreOffer {
            @SerializedName("OfferID")
            public String offerId;
            @SerializedName("Cost")
            public Map<String, Integer> cost;

            public String getOfferId() {
                return offerId;
            }

            public Map<String, Integer> getCost() {
                return cost;
            }

            @Override
            public String toString() {
                return "SingleItemStoreOffer{" +
                        "offerId='" + offerId + '\'' +
                        ", cost=" + cost +
                        '}';
            }
        }

    }

    /**
     * 通过<a href="https://github.com/HeyM1ke/ValorantClientAPI/blob/master/docsv2/store/PlayerStore.md">ValorantClientAPI PlayerStore</a>获取今日商店的随机皮肤
     *
     * @param json Api的返回值
     * @return 用户今日商店的随机皮肤
     */
    public static SkinsPanelLayoutContainer parseStore(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, SkinsPanelLayoutContainer.class);
    }

    @Override
    public String toString() {
        return "SkinsPanelLayoutContainer{" +
                "SkinsPanelLayout=" + SkinsPanelLayout +
                '}';
    }
}