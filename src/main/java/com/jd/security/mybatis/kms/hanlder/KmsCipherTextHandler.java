package com.jd.security.mybatis.kms.hanlder;

import com.jd.security.mybatis.kms.service.KmsCipherTextService;
import com.jd.security.mybatis.kms.util.KmsFactory;
import com.jd.security.mybatis.kms.util.KmsUtil;
import com.jd.security.mybatis.kms.util.LoadProperties;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public final class KmsCipherTextHandler<E> extends BaseTypeHandler<E>{
    static {
        LoadProperties.loadProps();
    }

    public KmsCipherTextHandler() {
    }

    public void setNonNullParameter(PreparedStatement ps, int i, E e, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, KmsFactory.getIns().encryptString(KmsUtil.toCipherString(e, jdbcType == null ? null : jdbcType.name())));
    }

    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return (E) KmsCipherTextService.getIns().getResult(rs, columnName);
    }

    public E getNullableResult(ResultSet rs, int i) throws SQLException {
        return (E) KmsCipherTextService.getIns().getResult(rs, i);
    }

    public E getNullableResult(CallableStatement cs, int i) throws SQLException {
        return (E) KmsCipherTextService.getIns().getResult(cs, i);
    }


}
