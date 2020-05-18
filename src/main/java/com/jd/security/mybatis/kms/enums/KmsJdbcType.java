package com.jd.security.mybatis.kms.enums;

public enum KmsJdbcType {
    DATE("DATE"),
    TIMESTAMP("TIMESTAMP"),
    TIME("TIME");

    private String jdbcType;

    private KmsJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getJdbcType() {
        return this.jdbcType;
    }

}
