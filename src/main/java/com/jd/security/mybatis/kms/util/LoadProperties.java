package com.jd.security.mybatis.kms.util;

import com.jd.security.mybatis.kms.vo.KmsProps;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;


public final class LoadProperties {
    private static final Logger LOG = Logger.getLogger(LoadProperties.class.getName());

    private LoadProperties() {
    }

    public static Properties loadProps(String fileName) {
        String path = KmsUtil.getClassPath();
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(path + fileName));
        } catch (IOException var4) {
            LOG.severe("LoadProperties method loadProps error " + var4.getLocalizedMessage());
        }
        return prop;
    }

    public static void loadProps() {
        setAcesProps(loadProps("kms.properties"), true);
        validProperties();
    }

    public static void setAcesProps(Properties properties, boolean isFileLoad) {
        String prefix = "";
        if (isFileLoad) {
            prefix = "kms.";
        }

        if (!properties.isEmpty()) {
            String accessKey = properties.getProperty(prefix + "accessKey");
            String secretKey = properties.getProperty(prefix + "secretKey");
            String endpoint = properties.getProperty(prefix + "endpoint");
            String keyId = properties.getProperty(prefix + "keyId");
            String schema = properties.getProperty(prefix + "schema");
            String indexSalt = properties.getProperty(prefix + "indexSalt");
            String writePlaintext = properties.getProperty(prefix + "writePlaintext");
            String isProd = properties.getProperty(prefix + "isProd");

            KmsProps.setAccessKey(accessKey);
            KmsProps.setSecretKey(secretKey);
            KmsProps.setEndpoint(endpoint);
            KmsProps.setKeyId(keyId);
            KmsProps.setSchema(schema);
            KmsProps.setIndexSalt(indexSalt);
            KmsProps.setWritePlaintext(writePlaintext);
            KmsProps.setIsProd(isProd);
        }
    }


    public static void validProperties() {
        String msgPrefix = "kms.properties ";
        String msgReason = " is null.";
        String msgField = null;

        if (KmsUtil.isBlank(KmsProps.getIsProd())) {
            msgField = "isProd";
        } else if (!KmsUtil.isBooleanString(KmsProps.getIsProd())) {
            msgField = "isProd";
            msgReason = " is not true or false.";
        } else if (KmsUtil.isBlank(KmsProps.getWritePlaintext())) {
            msgField = "writePlaintext";
        } else if (!KmsUtil.isBooleanString(KmsProps.getWritePlaintext())) {
            msgField = "writePlaintext";
            msgReason = " is not true or false.";
        } else if (KmsUtil.isBlank(KmsProps.getIndexSalt())) {
            msgField = "indexSalt";
        } else if (KmsProps.getIndexSalt().length() < 16) {
            msgField = "indexSalt";
            msgReason = " length must greater than 16 bits.";
        } else if (KmsUtil.isBlank(KmsProps.getAccessKey())){
            msgField = "accessKey";
        } else if (KmsUtil.isBlank(KmsProps.getSecretKey())){
            msgField = "secretKey";
        } else if (KmsUtil.isBlank(KmsProps.getEndpoint())){
            msgField = "endpoint";
        } else if (KmsUtil.isBlank(KmsProps.getKeyId())){
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
