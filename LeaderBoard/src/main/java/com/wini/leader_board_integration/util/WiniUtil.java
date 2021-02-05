package com.wini.leader_board_integration.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.wini.leader_board_integration.data.enums.PaymentPlatform;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by kamal on 1/28/2019.
 */
public class WiniUtil {
    private static final String PADDING = "===";
    private static final String HMAC_SHA256_MAC_NAME = "HMACSHA256";
    public static final Map<String, Object> gamePlatformLinksConfigs = new HashMap<>();
    public static final Map<String, Object> loginKeys = new HashMap<>();
    public static final Map<String, Object> paymentKeys = new HashMap<>();
    public static final Map<String, Object> platformkeylist = new HashMap<>();

    private static Supplier<Long> EPOCH_NOW = () -> LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

    public static final String getAppId(@NotNull final String gamePlatformlinkId, @NotNull final Integer login, @NotNull final String clientType) {
        final Map<String, Object> loginKey = (Map<String, Object>) com.wini.leader_board_integration.util.WiniUtil.loginKeys.get(gamePlatformlinkId);
        final Map<String, Object> platformLoginKey = (Map<String, Object>) loginKey.get(login.toString());
        final Map<String, String> client = (Map<String, String>) platformLoginKey.get(clientType);
        final String appId = client.get("app_id");
        return appId;
    }

    public JsonNode getNode(String path, JsonNode parent) {
        JsonNode findedNode = null;
        if (path.contains(".")) {
            String[] paths = path.split("\\.");
            for (int i = 0; (i + 1) < paths.length; i++) {
                if (findedNode == null) {
                    findedNode = getNode(paths[i + 1], parent.findPath(paths[i]));

                } else {
                    findedNode = getNode(paths[i + 1], findedNode);
                }
            }
        } else {
            return parent.findPath(path);
        }
        return findedNode;
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public static byte[] hmacSHA256(String secret, String src) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), HMAC_SHA256_MAC_NAME);
            Mac mac = Mac.getInstance(HMAC_SHA256_MAC_NAME);
            mac.init(secretKeySpec);
            return mac.doFinal(src.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException e) {
            throw new IllegalStateException(e);
        }

    }

    public static byte[] base64Decode(String input) {
        return new Base64(true).decode(input);
    }

    private static String padForBase64(String base64) {
        return base64 + PADDING.substring(0, (4 - base64.length() % 4) % 4);
    }

    public static byte[] base64DecodeToBytes(String in) {
        return java.util.Base64.getDecoder().decode(padForBase64(in.replace('_', '/').replace('-', '+')).getBytes());
    }


    public static Optional<String> extractPurchaseToken(int platform, Map<String, Object> data) {
        if (platform == PaymentPlatform.FACEBOOK_INSTANCE.getCode()) {
            return Optional.ofNullable(data.get("signedRequest").toString());
        }

        if (platform == PaymentPlatform.GOOGLE.getCode()) {
            return Optional.ofNullable(data.get("purchaseToken").toString());
        }

        if (platform == PaymentPlatform.CAFEBAZAAR.getCode()) {
            return Optional.ofNullable(data.get("signedRequest").toString());
        }
        if (platform == PaymentPlatform.PAYPAL.getCode()) {
            return Optional.ofNullable(data.get("id").toString());
        }
        if (platform == PaymentPlatform.APPSTORE.getCode()) {
            return Optional.ofNullable(data.get("Payload").toString());
        }
        return Optional.empty();
    }

    public static Optional<String> extractIdentifier(int platform, Map<String, Object> data) {
        if (platform == PaymentPlatform.FACEBOOK_INSTANCE.getCode()) {
            return Optional.ofNullable(data.get("paymentID").toString());
        }
        if (platform == PaymentPlatform.PAYPAL.getCode()) {
            return Optional.ofNullable(data.get("id").toString());
        }
        if (platform == PaymentPlatform.APPSTORE.getCode()) {
            return Optional.ofNullable(data.get("TransactionID").toString());
        }
        if (platform == PaymentPlatform.GOOGLE.getCode()) {
            return Optional.ofNullable(data.get("orderId").toString());
        }
//
//        if (platform == PaymentPlatform.CAFEBAZAAR.getCode()) {
//            return Optional.ofNullable(data.get("signedRequest").toString());
//        }
        return Optional.empty();
    }

    public static long toEpochSecond() {
        return EPOCH_NOW.get();
    }

    public static LocalDateTime toDateTime(long longValue) {

        return LocalDateTime.ofInstant(Instant.ofEpochSecond(longValue), ZoneId.systemDefault());
    }

    public static boolean isExpiredAfter(LocalDateTime dateTime, long amountToAdd, ChronoUnit unit) {
        return LocalDateTime.now().isBefore(dateTime.plus(amountToAdd, unit));
    }

    public static boolean isExpiredAfter(long value, long amountToAdd, ChronoUnit unit) {
        return isExpiredAfter(millisToDateTime(value), amountToAdd, unit);
    }

    public static LocalDateTime millisToDateTime(long longValue) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(longValue), ZoneId.systemDefault());
    }

    public static LocalDateTime expireAt(Long startTime, Long currentTime, Double everyHours) {
        Double dif = Double.valueOf(Math.abs(currentTime - startTime));
        Double d = Math.ceil((Double.valueOf(currentTime) - Double.valueOf(startTime)) / (everyHours * 3600));
        Double d1 = d * everyHours * 3600;
        Double expireAt = currentTime + (d1 - dif);
        return toDateTime(expireAt.longValue());
    }

    public static Long expireAtEpoch(Long startTime, Long currentTime, Integer everyHours) {
        Double dif = Double.valueOf(Math.abs(currentTime - startTime));
        Double d = Math.ceil((Double.valueOf(currentTime) - Double.valueOf(startTime)) / (everyHours * 3600));
        Double d1 = d * everyHours * 3600;
        Double expireAt = currentTime + (d1 - dif);
        return expireAt.longValue();
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }


    public static String weekOfYear() {
        String weekNumber;
        LocalDate date = LocalDate.now();
        int week = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        weekNumber = String.format("%d|%d", date.getYear(), week);
        return weekNumber;
    }

    public static String dayOfYear() {
        LocalDate date = LocalDate.now();
        return String.format("%d|%d", date.getYear(), date.getDayOfYear());
    }

    public static Optional<String> encode(String content) {
        return Optional.ofNullable(java.util.Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8)));
    }

    public static Optional<String> decode(String content) {
        return Optional.of(new String(java.util.Base64.getDecoder().decode(content), StandardCharsets.UTF_8));
    }

    public static boolean stringValueExist(final JSONArray jsonArray,final  String value) {
        return jsonArray.toString().contains(value.trim());
    }

    public static String getURLBase(HttpServletRequest request) throws MalformedURLException {

        URL requestURL = new URL(request.getRequestURL().toString());
        String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
        return requestURL.getProtocol() + "://" + requestURL.getHost() + port;

    }
}
