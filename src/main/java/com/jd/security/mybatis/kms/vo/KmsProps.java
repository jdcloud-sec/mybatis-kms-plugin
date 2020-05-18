package com.jd.security.mybatis.kms.vo;


public final class KmsProps {
    private static String accessKey;
    private static String secretKey;
    private static String endpoint;
    private static String keyId;
    private static String schema;
    private static String indexSalt;
    private static String writePlaintext;
    private static String isProd;


    public static String getAccessKey() {
        return accessKey;
    }

    public static void setAccessKey(String accessKey) {
        KmsProps.accessKey = accessKey;
    }

    public static String getSecretKey() {
        return secretKey;
    }

    public static void setSecretKey(String secretKey) {
        KmsProps.secretKey = secretKey;
    }

    public static String getEndpoint() {
        return endpoint;
    }

    public static void setEndpoint(String endpoint) {
        KmsProps.endpoint = endpoint;
    }

    public static String getKeyId() {
        return keyId;
    }

    public static void setKeyId(String keyId) {
        KmsProps.keyId = keyId;
    }

    public static String getIndexSalt() {
        return indexSalt;
    }

    public static void setIndexSalt(String indexSalt) {
        KmsProps.indexSalt = indexSalt;
    }

    public static String getWritePlaintext() {
        return writePlaintext;
    }

    public static Boolean isWritePlaintext() {
        return Boolean.parseBoolean(writePlaintext);
    }

    public static void setWritePlaintext(String writePlaintext) {
        KmsProps.writePlaintext = writePlaintext;
    }

    public static String getIsProd() {
        return isProd;
    }

    public static void setIsProd(String isProd) {
        KmsProps.isProd = isProd;
    }

    public static String getSchema() {
        return schema;
    }

    public static void setSchema(String schema) {
        KmsProps.schema = schema;
    }
}
