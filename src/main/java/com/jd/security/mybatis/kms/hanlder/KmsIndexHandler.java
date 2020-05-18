package com.jd.security.mybatis.kms.hanlder;

import com.jd.security.mybatis.kms.util.KmsFactory;
import com.jd.security.mybatis.kms.util.KmsUtil;
import com.jd.security.mybatis.kms.util.LoadProperties;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public final class KmsIndexHandler<E> extends BaseTypeHandler<E> {
    static {
        LoadProperties.loadProps();
    }

    public KmsIndexHandler() {
    }

    public void setNonNullParameter(PreparedStatement ps, int i, E e, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, KmsFactory.getIns().calculateStringIndex(KmsUtil.toIndexString(e, jdbcType == null ? null : jdbcType.name())));
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
