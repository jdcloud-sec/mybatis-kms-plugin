package com.jd.security.mybatis.kms.hanlder;

import com.jd.security.mybatis.kms.service.KmsCipherTextService;
import com.jd.security.mybatis.kms.util.KmsFactory;
import com.jd.security.mybatis.kms.util.KmsUtil;
import com.jd.security.mybatis.kms.util.LoadKmsConfig;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public final class KmsCipherTextHandler<E> extends BaseTypeHandler<E>{
    static {
        LoadKmsConfig.loadConfig();
    }

    public void setNonNullParameter(PreparedStatement ps, int i, E e, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, KmsFactory.getInstance().encrypt(KmsUtil.toCipherString(e, jdbcType == null ? null : jdbcType.name())));
    }

    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return (E) KmsCipherTextService.getKmsCiperTextServiceIns().getResult(rs, columnName);
    }

    public E getNullableResult(ResultSet rs, int i) throws SQLException {
        return (E) KmsCipherTextService.getKmsCiperTextServiceIns().getResult(rs, i);
    }

    public E getNullableResult(CallableStatement cs, int i) throws SQLException {
        return (E) KmsCipherTextService.getKmsCiperTextServiceIns().getResult(cs, i);
    }

}
