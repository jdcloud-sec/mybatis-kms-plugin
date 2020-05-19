package com.jd.security.mybatis.kms.util;

import com.jd.security.mybatis.kms.vo.KmsConfig;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;


public final class LoadKmsConfig {
    private static final Logger LOG = Logger.getLogger(LoadKmsConfig.class.getName());

    private LoadKmsConfig() {
    }

    public static Properties loadConfig(String fileName) {
        String path = KmsUtil.getClassPath();
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(path + fileName));
        } catch (IOException var4) {
            LOG.severe("LoadProperties method loadProps error " + var4.getLocalizedMessage());
        }
        return prop;
    }

    public static void loadConfig() {
        setAcesProps(loadConfig("kms.properties"), true);
        validProperties();
    }

    public static void setAcesProps(Properties properties, boolean isFileLoad) {
        String prefix = isFileLoad ? "kms." : "";

        if (!properties.isEmpty()) {
            KmsConfig.setAccessKey(properties.getProperty(prefix + "accessKey"));
            KmsConfig.setSecretKey(properties.getProperty(prefix + "secretKey"));
            KmsConfig.setEndpoint(properties.getProperty(prefix + "endpoint"));
            KmsConfig.setKeyId(properties.getProperty(prefix + "keyId"));
            KmsConfig.setSchema(properties.getProperty(prefix + "schema"));
            KmsConfig.setIndexSalt(properties.getProperty(prefix + "indexSalt"));
            KmsConfig.setWritePlaintext(properties.getProperty(prefix + "writePlaintext"));
            KmsConfig.setIsProd(properties.getProperty(prefix + "isProd"));
        }
    }


    public static void validProperties() {
        String msgPrefix = "kms.properties ";
        String msgReason = " is null.";
        String msgField = null;

        if (KmsUtil.isBlank(KmsConfig.getIsProd())) {
            msgField = "isProd";
        } else if (!KmsUtil.isBooleanString(KmsConfig.getIsProd())) {
            msgField = "isProd";
            msgReason = " is not true or false.";
        } else if (KmsUtil.isBlank(KmsConfig.getWritePlaintext())) {
            msgField = "writePlaintext";
        } else if (!KmsUtil.isBooleanString(KmsConfig.getWritePlaintext())) {
            msgField = "writePlaintext";
            msgReason = " is not true or false.";
        } else if (KmsUtil.isBlank(KmsConfig.getIndexSalt())) {
            msgField = "indexSalt";
        } else if (KmsConfig.getIndexSalt().length() < 16) {
            msgField = "indexSalt";
            msgReason = " length must greater than 16 bits.";
        } else if (KmsUtil.isBlank(KmsConfig.getAccessKey())){
            msgField = "accessKey";
        } else if (KmsUtil.isBlank(KmsConfig.getSecretKey())){
            msgField = "secretKey";
        } else if (KmsUtil.isBlank(KmsConfig.getEndpoint())){
            msgField = "endpoint";
        } else if (KmsUtil.isBlank(KmsConfig.getKeyId())){
            msgField = "KeyId";
        }

        if (msgField != null) {
            String msg = msgPrefix + msgField + msgReason;
            RuntimeException exception = new RuntimeException(msg);
            LOG.severe(exception.getLocalizedMessage());
            throw exception;
        }
    }
}
