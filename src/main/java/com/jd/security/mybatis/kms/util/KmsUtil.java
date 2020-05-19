package com.jd.security.mybatis.kms.util;

import com.jd.security.mybatis.kms.enums.KmsJdbcType;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;


public final class KmsUtil {
    private static final Logger LOG = Logger.getLogger(KmsUtil.class.getName());

    private KmsUtil() {
    }

    public static boolean isBlank(Object obj) {
        return obj == null || String.valueOf(obj).trim().length() == 0;
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }


    public static boolean isBooleanString(String str) {
        return Boolean.FALSE.toString().equals(str) || Boolean.TRUE.toString().equals(str);
    }

    public static String date2String(Date date, String jdbcType) {
        String res = null;
        if (date != null) {
            String format;
            if (jdbcType == null) {
                format = "yyyy-MM-dd HH:mm:ss";
            } else if (KmsJdbcType.DATE.getJdbcType().equals(jdbcType.toUpperCase())) {
                format = "yyyy-MM-dd";
            } else if (KmsJdbcType.TIMESTAMP.getJdbcType().equals(jdbcType.toUpperCase())) {
                format = "yyyy-MM-dd HH:mm:ss";
            } else if (KmsJdbcType.TIME.getJdbcType().equals(jdbcType.toUpperCase())) {
                format = "HH:mm:ss";
            } else {
                format = "yyyy-MM-dd HH:mm:ss";
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            res = dateFormat.format(date);
        }
        return res;
    }

    public static Date string2UtilDate(String str, String format) {
        Date res = null;
        if (!isBlank(str)) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                res = dateFormat.parse(str);
            } catch (ParseException var4) {
                LOG.severe("KmsUtil method stringToDate error " + var4.getLocalizedMessage());
            }
        }
        return res;
    }

    public static Timestamp string2Timestamp(String str, String format) {
        Timestamp res = null;
        Date date = string2UtilDate(str, format);
        if (date != null) {
            res = new Timestamp(date.getTime());
        }
        return res;
    }

    public static java.sql.Date string2SqlDate(String str, String format) {
        java.sql.Date res = null;
        Date date = string2UtilDate(str, format);
        if (date != null) {
            res = new java.sql.Date(date.getTime());
        }
        return res;
    }

    public static Time string2SqlTime(String str, String format) {
        Time res = null;
        Date date = string2UtilDate(str, format);
        if (date != null) {
            res = new Time(date.getTime());
        }
        return res;
    }

    public static String toIndexString(Object obj, String jdbcType) {
        return toCipherString(obj, jdbcType);
    }

    public static String toCipherString(Object obj, String jdbcType) {
        String res;
        if (obj == null) {
            res = null;
        } else if (obj instanceof java.sql.Date) {
            res = date2String((java.sql.Date)obj, jdbcType);
        } else if (obj instanceof Time) {
            res = date2String((Time)obj, KmsJdbcType.TIME.getJdbcType());
        } else if (obj instanceof Timestamp) {
            res = date2String((Timestamp)obj, jdbcType);
        } else if (obj instanceof Date) {
            res = date2String((Date)obj, jdbcType);
        } else {
            res = String.valueOf(obj);
        }
        return res;
    }

    public static String getClassPath() {
        return KmsUtil.class.getResource("/").getPath();
    }

    public static String getPlainTextColumn(String columnName) {
        String res;
        if (isPlainTextColumn(columnName)) {
            res = columnName.substring(0, columnName.length() - 8);
        } else {
            res = columnName;
        }
        return res;
    }

    private static boolean isPlainTextColumn(String columnName) {
        return columnName != null && columnName.toLowerCase().endsWith("_encrypt");
    }

}
