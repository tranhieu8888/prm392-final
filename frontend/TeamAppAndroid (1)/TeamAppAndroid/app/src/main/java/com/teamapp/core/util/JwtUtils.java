package com.teamapp.core.util;

import android.util.Base64;
import org.json.JSONObject;

public final class JwtUtils {
    private JwtUtils() {}

    /** Lấy exp (epoch seconds) từ JWT. Trả -1 nếu không có/không parse được. */
    public static long getExp(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return -1;
            String payloadJson = new String(Base64.decode(parts[1],
                    Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING));
            JSONObject obj = new JSONObject(payloadJson);
            return obj.optLong("exp", -1);
        } catch (Exception ignored) {
            return -1;
        }
    }

    /** Token hết hạn? Có cộng buffer skewSeconds cho an toàn. */
    public static boolean isExpired(String jwt, long skewSeconds) {
        long exp = getExp(jwt);
        if (exp <= 0) return false; // nếu không có exp thì coi như không biết -> tuỳ policy
        long now = System.currentTimeMillis() / 1000L;
        return (now + skewSeconds) >= exp;
    }
}
