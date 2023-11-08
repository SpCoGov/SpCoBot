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
package top.spco.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * Created on 2023/11/3 0003 15:46
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class HashUtils {
    /**
     * 计算输入字符串的 SHA-256 哈希值。
     *
     * @param input 要进行 SHA-256 加密的字符串
     * @return 输入字符串的 SHA-256 哈希值，以十六进制字符串的形式返回
     * @throws NoSuchAlgorithmException 如果 SHA-256 算法不可用
     */
    public static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
        byte[] inputBytes = input.getBytes();
        byte[] hashBytes = sha256Digest.digest(inputBytes);

        // 将字节数组转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xFF & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}