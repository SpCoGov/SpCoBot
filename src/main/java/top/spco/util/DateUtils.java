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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * 用于处理日期和时间的工具类。
 *
 * @author SpCo
 * @version 0.1.2
 * @since 0.1.2
 */
public class DateUtils {
    public static final String DATE_TIME_MINUTE_FORMAT = "yyyy-MM-dd'T'HH:mm";

    /**
     * 获取当前日期，时区为 "Asia/Shanghai"。
     *
     * @return 当前日期。
     */
    public static LocalDate today() {
        return LocalDate.now(ZoneId.of("Asia/Shanghai"));
    }

    /**
     * 获取当前日期和时间，时区为 "Asia/Shanghai"。
     *
     * @return 当前日期和时间。
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
    }

    /**
     * 获取具有分钟精度的当前日期和时间（秒和纳秒部分设置为0），时区为 "Asia/Shanghai"。
     *
     * @return 具有分钟精度的当前日期和时间。
     */
    public static LocalDateTime nowAtMinutePrecision() {
        return DateUtils.now().withSecond(0).with(ChronoField.NANO_OF_SECOND, 0);
    }

    /**
     * 通过将指定的分钟数添加到当前时间来计算未来日期和时间。
     *
     * @param minutes 要添加的分钟数。
     * @return 计算得到的未来日期和时间。
     */
    public static LocalDateTime calculateFutureTime(long minutes) {
        return DateUtils.calculateFutureTime(DateUtils.now(), minutes);
    }

    /**
     * 通过将指定的分钟数添加到给定的日期和时间来计算未来日期和时间。
     *
     * @param dateTime 基础日期和时间。
     * @param minutes  要添加的分钟数。
     * @return 计算得到的未来日期和时间。
     */
    public static LocalDateTime calculateFutureTime(LocalDateTime dateTime, long minutes) {
        return dateTime.plus(minutes, ChronoUnit.MINUTES);
    }

    /**
     * 使用指定的格式解析日期和时间字符串。
     *
     * @param dateTimeString 要解析的日期和时间字符串。
     * @param format         用于解析的格式。
     * @return 解析后的 LocalDateTime。
     * @throws DateTimeParseException 如果输入字符串不符合预期的格式。
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeString, String format) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    /**
     * 计算给定的起始日期时间到当前日期时间之间的分钟间隔。
     *
     * @param startDateTime 起始日期时间
     * @return 从起始日期时间到当前日期时间之间的分钟间隔
     */
    public static long calculateMinutesBetween(LocalDateTime startDateTime) {
        LocalDateTime currentDateTime = DateUtils.nowAtMinutePrecision();
        Duration duration = Duration.between(startDateTime, currentDateTime);
        return duration.toMinutes();
    }

    public static Date getTodayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}