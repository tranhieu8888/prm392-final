package com.teamapp.core.db;

import androidx.room.TypeConverter;
import java.util.Date;
import java.util.UUID;

public final class DateConverters {
    @TypeConverter public static Long fromDate(Date d){ return d == null ? null : d.getTime(); }
    @TypeConverter public static Date toDate(Long v){ return v == null ? null : new Date(v); }

    @TypeConverter public static String fromUuid(UUID u){ return u == null ? null : u.toString(); }
    @TypeConverter public static UUID toUuid(String s){ return (s == null || s.isEmpty()) ? null : UUID.fromString(s); }
}
