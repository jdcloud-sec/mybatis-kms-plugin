package com.jd.security.mybatis.kms.service;

import com.jd.security.mybatis.kms.util.KmsFactory;
import com.jd.security.mybatis.kms.util.KmsUtil;
import com.jd.security.mybatis.kms.vo.KmsConfig;
import com.jd.security.mybatis.kms.vo.DbMetaColumn;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;


public final class KmsCipherTextService {
    private static KmsCipherTextService kmsCipherTextService;
    private static final Logger LOG = Logger.getLogger(KmsCipherTextService.class.getName());

    private KmsCipherTextService() {
    }

    public static final synchronized KmsCipherTextService getKmsCiperTextServiceIns() {
        if (kmsCipherTextService == null) {
            kmsCipherTextService = new KmsCipherTextService();
        }
        return kmsCipherTextService;
    }

    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        return this.getDecryptResult(rs, columnName);
    }

    public Object getResult(ResultSet rs, int i) throws SQLException {
        DbMetaColumn mc = this.getMetaColumn(rs.getMetaData(), i);
        return this.getDecryptResult(rs, mc.getColumnName());
    }

    public Object getResult(CallableStatement cs, int i) throws SQLException {
        DbMetaColumn mc = this.getMetaColumn(cs.getMetaData(), i);
        ResultSet rs = cs.getResultSet();
        return this.getDecryptResult(rs, mc.getColumnName());
    }

    private boolean validIsEncryptColumn(String columnName) {
        if (columnName != null && columnName.toLowerCase().endsWith("_encrypt")) {
            return true;
        } else {
            String msg = columnName + " is invalid.";
            LOG.severe(msg);
            throw new RuntimeException(msg);
        }
    }

    private Object getDecryptResult(ResultSet rs, String columnName) throws SQLException {
        boolean isSuccess = validIsEncryptColumn(columnName);
        Object rst = null;
        if (isSuccess) {
            String plainTextColumn = KmsUtil.getPlainTextColumn(columnName);
            DbMetaColumn dbMetaColumn = getMetaColumn(rs.getMetaData(), plainTextColumn, columnName);
            if (KmsConfig.isWritePlaintext()) {
                String cipherVal = rs.getString(columnName);
                if (!KmsUtil.isBlank(cipherVal)) {
                    rst = resultTypeCast(dbMetaColumn, KmsFactory.getInstance().decrypt(cipherVal));
                }
                if (rst == null) {
                    rst = rs.getObject(plainTextColumn);
                }
            } else {
                rst = resultTypeCast(dbMetaColumn, KmsFactory.getInstance().decrypt(rs.getString(columnName)));
            }
        }
        return rst;
    }

    private DbMetaColumn getMetaColumn(ResultSetMetaData rsm, String plainTextColumnName, String cipherColumnName) throws SQLException {
        DbMetaColumn column = null;
        int columnCount = rsm.getColumnCount();

        for(int i = 1; i <= columnCount; ++i) {
            String dbColumnName = rsm.getColumnName(i);
            String dbColumnLabel = rsm.getColumnLabel(i);
            if (plainTextColumnName.equalsIgnoreCase(dbColumnName) || plainTextColumnName.equalsIgnoreCase(dbColumnLabel)) {
                column = getMetaColumn(rsm, i);
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

    private DbMetaColumn getMetaColumn(ResultSetMetaData data, int i) throws SQLException {
        DbMetaColumn column = new DbMetaColumn();
        column.setCatalogName(data.getCatalogName(i));
        column.setColumnClassName(data.getColumnClassName(i));
        column.setColumnLabel(data.getColumnLabel(i));
        column.setColumnName(data.getColumnName(i));
        column.setColumnType(data.getColumnType(i));
        column.setTableName(data.getTableName(i));
        return column;
    }

    private Object resultTypeCast(DbMetaColumn dbMetaColumn, String str) {
        String columnClass = dbMetaColumn.getColumnClassName();
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
            res = KmsUtil.string2SqlDate(str, "yyyy-MM-dd");
        } else if ("java.sql.Time".equals(columnClass)) {
            res = KmsUtil.string2SqlTime(str, "HH:mm:ss");
        } else if ("java.sql.Timestamp".equals(columnClass)) {
            res = KmsUtil.string2Timestamp(str, "yyyy-MM-dd HH:mm:ss");
        } else if ("java.util.Date".equals(columnClass)) {
            res = KmsUtil.string2UtilDate(str, "yyyy-MM-dd HH:mm:ss");
        } else {
            res = null;
            RuntimeException rex = new RuntimeException(columnClass + " error" );
            LOG.severe(rex.getLocalizedMessage());
            if (!KmsConfig.isWritePlaintext()) {
                throw rex;
            }
        }
        return res;
    }

}
