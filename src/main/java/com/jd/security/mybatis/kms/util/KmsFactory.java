package com.jd.security.mybatis.kms.util;

import com.alibaba.fastjson.JSON;
import com.jd.security.mybatis.kms.vo.KmsProps;
import com.jdcloud.sdk.auth.StaticCredentialsProvider;
import com.jdcloud.sdk.client.Environment;
import com.jdcloud.sdk.http.HttpRequestConfig;
import com.jdcloud.sdk.http.Protocol;
import com.jdcloud.sdk.service.kms.client.KmsClient;
import com.jdcloud.sdk.service.kms.model.DecryptRequest;
import com.jdcloud.sdk.service.kms.model.DecryptResponse;
import com.jdcloud.sdk.service.kms.model.EncryptRequest;
import com.jdcloud.sdk.service.kms.model.EncryptResponse;
import com.jdcloud.sdk.utils.Base64Utils;
import com.jdcloud.sdk.utils.StringUtils;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;


public final class KmsFactory {
    private static KmsFactory factory;
    private static final Logger LOG = Logger.getLogger(KmsFactory.class.getName());
    private KmsClient kmsClient = this.getKmsClient();

    private KmsFactory() {
    }

    public static final synchronized KmsFactory getIns() {
        if (factory == null) {
            factory = new KmsFactory();
        }
        return factory;
    }

    public String encryptString(String plaintext) {
        String plaintextBase64 = null;
        try {
            plaintextBase64 = Base64.encodeBase64String(plaintext.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        EncryptRequest encryptRequest = new EncryptRequest();
        encryptRequest.setKeyId(KmsProps.getKeyId());
        encryptRequest.setPlaintext(plaintextBase64);
        try{
            EncryptResponse encryptResponse = kmsClient.encrypt(encryptRequest);
            if (encryptResponse == null || encryptResponse.getError() != null ||encryptResponse.getResult() == null){
                LOG.severe("KmsFactory method encryptString response error, response: " + JSON.toJSONString(encryptResponse));
                throw new RuntimeException();
            }
            String ciphertextBlob = encryptResponse.getResult().getCiphertextBlob();
            return ciphertextBlob;
        } catch (Exception e){
            LOG.severe("KmsFactory method encryptString error " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    public String decryptString(String cipher) {
        DecryptRequest decryptRequest = new DecryptRequest();
        decryptRequest.setKeyId(KmsProps.getKeyId());
        decryptRequest.setCiphertextBlob(cipher);
        try{
            DecryptResponse decryptResponse = kmsClient.decrypt(decryptRequest);
            if (decryptResponse == null || decryptResponse.getError() != null ||decryptResponse.getResult() == null){
                LOG.severe("KmsFactory method decryptString response error, response: " + JSON.toJSONString(decryptResponse));
                throw new RuntimeException();
            }
            String plaintext = decryptResponse.getResult().getPlaintext();

            String rst = new String(Base64.decodeBase64(plaintext), "UTF-8");

            return rst;
        }catch (Exception e){
            LOG.severe("KmsFactory method decryptString error " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }


    public String calculateStringIndex(String plaintext) {
        return sha256_HMAC(plaintext, KmsProps.getIndexSalt());
    }

    private static String sha256_HMAC(String message, String secret) {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes("UTF-8"));
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            System.out.println("KmsFactory method HmacSHA256 error, " + e.getMessage());
        }
        return hash;
    }
    private  static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }


    private KmsClient getKmsClient() {
        String schema = StringUtils.isBlank(KmsProps.getSchema()) ? "https" : KmsProps.getSchema();
        Protocol protocol = Protocol.HTTPS;
        if ("http".equalsIgnoreCase(schema)){
            protocol = Protocol.HTTP;
        }
        KmsClient ins =  KmsClient.builder()
                .credentialsProvider(new StaticCredentialsProvider(KmsProps.getAccessKey(), KmsProps.getSecretKey()))
                .httpRequestConfig(new HttpRequestConfig.Builder().protocol(protocol).build())
                .environment(new Environment.Builder().endpoint(KmsProps.getEndpoint()).build())
                .build();
        return ins;
    }

}
