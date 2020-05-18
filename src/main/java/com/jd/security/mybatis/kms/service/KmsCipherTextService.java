package com.jd.security.mybatis.kms.service;

import com.jd.security.mybatis.kms.util.KmsFactory;
import com.jd.security.mybatis.kms.util.KmsUtil;
import com.jd.security.mybatis.kms.vo.KmsProps;
import com.jd.security.mybatis.kms.vo.MetaColumn;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;


public final class KmsCipherTextService {
    private static KmsCipherTextService factory;
    private static final Logger LOG = Logger.getLogger(KmsCipherTextService.class.getName());

    private KmsCipherTextService() {
    }

    public static final synchronized KmsCipherTextService getIns() {
        if (factory == null) {
            factory = new KmsCipherTextService();
        }
        return factory;
    }

    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        return this.getDecryptResult(rs, columnName);
    }

    public Object getResult(ResultSet rs, int i) throws SQLException {
        MetaColumn mc = this.getMetaColumn(rs.getMetaData(), i);
        return this.getDecryptResult(rs, mc.getColumnName());
    }

    public Object getResult(CallableStatement cs, int i) throws SQLException {
        MetaColumn mc = this.getMetaColumn(cs.getMetaData(), i);
        ResultSet rs = cs.getResultSet();
        return this.getDecryptResult(rs, mc.getColumnName());
    }

    private boolean validColumn(String columnName) {
        boolean res = false;
        if (columnName != null && columnName.toLowerCase().endsWith("_encrypt")) {
            res = true;
            return res;
        } else {
            String msg = columnName + " is invalid.";
            LOG.severe(msg);
            throw new RuntimeException(msg);
        }
    }

    private Object getDecryptResult(ResultSet rs, String columnName) throws SQLException {
        boolean isSuccess = this.validColumn(columnName);
        Object res = null;
        if (isSuccess) {
            String plainTextColumn = KmsUtil.getPlainTextColumn(columnName);
            MetaColumn metaColumn = this.getMetaColumn(rs.getMetaData(), plainTextColumn, columnName);
            if (KmsProps.isWritePlaintext()) {
                String cipherVal = rs.getString(columnName);
                if (!KmsUtil.isBlank(cipherVal)) {
                    res = this.resultTypeCast(metaColumn, KmsFactory.getIns().decryptString(cipherVal));
                }
                if (res == null) {
                    res = rs.getObject(plainTextColumn);
                }
            } else {
                res = this.resultTypeCast(metaColumn, KmsFactory.getIns().decryptString(rs.getString(columnName)));
            }
        }
        return res;
    }

    private MetaColumn getMetaColumn(ResultSetMetaData rsm, String plainTextColumnName, String cipherColumnName) throws SQLException {
        MetaColumn column = null;
        int columnCount = rsm.getColumnCount();

        for(int i = 1; i <= columnCount; ++i) {
            String dbColumnName = rsm.getColumnName(i);
            String dbColumnLabel = rsm.getColumnLabel(i);
            if (plainTextColumnName.equalsIgnoreCase(dbColumnName) || plainTextColumnName.equalsIgnoreCase(dbColumnLabel)) {
                column = this.getMetaColumn(rsm, i);
                break;
            }
        }

        if (column == null) {
            String msg = "select column must contain " + cipherColumnName + " and " + plainTextColumnName;
            throw new RuntimeException(msg);
        } else {
            return column;
        }
    }

    private MetaColumn getMetaColumn(ResultSetMetaData data, int i) throws SQLException {
        MetaColumn column = new MetaColumn();
        column.setCatalogName(data.getCatalogName(i));
        column.setColumnClassName(data.getColumnClassName(i));
        column.setColumnLabel(data.getColumnLabel(i));
        column.setColumnName(data.getColumnName(i));
        column.setColumnType(data.getColumnType(i));
        column.setTableName(data.getTableName(i));
        return column;
    }

    private Object resultTypeCast(MetaColumn metaColumn, String str) {
        String columnClass = metaColumn.getColumnClassName();
        Object res;
        if (str == null) {
            res = null;
        } else if ("java.lang.String".equals(columnClass)) {
            res = str;
        } else if ("java.lang.Integer".equals(columnClass)) {
            res = Integer.valueOf(str);
        } else if ("java.lang.Long".equals(columnClass)) {
            res = Long.valueOf(str);
        } else if ("java.lang.Float".equals(columnClass)) {
            res = Float.valueOf(str);
        } else if ("java.lang.Double".equals(columnClass)) {
            res = Double.valueOf(str);
        } else if ("java.math.BigDecimal".equals(columnClass)) {
            res = new BigDecimal(str);
        } else if ("java.sql.Date".equals(columnClass)) {
            res = KmsUtil.stringToSqlDate(str, "yyyy-MM-dd");
        } else if ("java.sql.Time".equals(columnClass)) {
            res = KmsUtil.stringToSqlTime(str, "HH:mm:ss");
        } else if ("java.sql.Timestamp".equals(columnClass)) {
            res = KmsUtil.stringToTimestamp(str, "yyyy-MM-dd HH:mm:ss");
        } else if ("java.util.Date".equals(columnClass)) {
            res = KmsUtil.stringToUtilDate(str, "yyyy-MM-dd HH:mm:ss");
        } else {
            res = null;
            RuntimeException rex = new RuntimeException(columnClass + " error" );
            LOG.severe(rex.getLocalizedMessage());
            if (!KmsProps.isWritePlaintext()) {
                throw rex;
            }
        }
        return res;
    }

}
