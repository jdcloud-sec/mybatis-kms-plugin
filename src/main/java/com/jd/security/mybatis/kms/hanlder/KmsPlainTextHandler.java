package com.jd.security.mybatis.kms.hanlder;

import com.jd.security.mybatis.kms.util.LoadProperties;
import com.jd.security.mybatis.kms.vo.KmsProps;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public final class KmsPlainTextHandler<E> extends BaseTypeHandler<E> {
    static {
        LoadProperties.loadProps();
    }

    public KmsPlainTextHandler() {
    }

    public void setNonNullParameter(PreparedStatement ps, int i, E e, JdbcType jdbcType) throws SQLException {
        if (!KmsProps.isWritePlaintext()) {
            ps.setObject(i, (Object)null);
        } else {
            ps.setObject(i, e);
        }

    }

    public E getNullableResult(ResultSet rs, String s) throws SQLException {
        return (E) rs.getObject(s);
    }

    public E getNullableResult(ResultSet rs, int i) throws SQLException {
        return (E) rs.getObject(i);
    }

    public E getNullableResult(CallableStatement cs, int i) throws SQLException {
        return (E) cs.getObject(i);
    }


}
