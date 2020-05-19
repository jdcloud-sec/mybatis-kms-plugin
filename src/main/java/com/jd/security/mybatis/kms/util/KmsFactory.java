package com.jd.security.mybatis.kms.util;

import com.alibaba.fastjson.JSON;
import com.jd.security.mybatis.kms.vo.KmsConfig;
import com.jdcloud.sdk.auth.StaticCredentialsProvider;
import com.jdcloud.sdk.client.Environment;
import com.jdcloud.sdk.http.HttpRequestConfig;
import com.jdcloud.sdk.http.Protocol;
import com.jdcloud.sdk.service.kms.client.KmsClient;
import com.jdcloud.sdk.service.kms.model.DecryptRequest;
import com.jdcloud.sdk.service.kms.model.DecryptResponse;
import com.jdcloud.sdk.service.kms.model.EncryptRequest;
import com.jdcloud.sdk.service.kms.model.EncryptResponse;
import com.jdcloud.sdk.utils.StringUtils;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;


public final class KmsFactory {
    private static KmsFactory factory;
    private static final Logger LOG = Logger.getLogger(KmsFactory.class.getName());
    private KmsClient kmsClient = getKmsClient();

    private KmsFactory() {
    }

    public static final synchronized KmsFactory getInstance() {
        return factory == null ? new KmsFactory() : factory;
    }

    public String encrypt(String plaintext) {
        String plaintextBase64 = null;
        try {
            plaintextBase64 = Base64.encodeBase64String(plaintext.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LOG.severe("KmsFactory method encrypt exception: " + JSON.toJSONString(e));
            throw new RuntimeException(e);
        }

        EncryptRequest request = new EncryptRequest();
        request.setKeyId(KmsConfig.getKeyId());
        request.setPlaintext(plaintextBase64);
        try{
            EncryptResponse response = kmsClient.encrypt(request);
            if (response == null || response.getError() != null || response.getResult() == null){
                LOG.severe("KmsFactory method encryptString response error, response: " + JSON.toJSONString(response));
                throw new RuntimeException();
            }
            return response.getResult().getCiphertextBlob();
        } catch (Exception e){
            LOG.severe("KmsFactory method encryptString error: " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String cipher) {
        DecryptRequest request = new DecryptRequest();
        request.setKeyId(KmsConfig.getKeyId());
        request.setCiphertextBlob(cipher);
        try{
            DecryptResponse response = kmsClient.decrypt(request);
            if (response == null || response.getError() != null || response.getResult() == null){
                LOG.severe("KmsFactory method decryptString response error, response: " + JSON.toJSONString(response));
                throw new RuntimeException();
            }
            return new String(Base64.decodeBase64(response.getResult().getPlaintext()), "UTF-8");
        }catch (Exception e){
            LOG.severe("KmsFactory method decryptString error: " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }


    public String generateIndexString(String plaintext) {
        return sha256_HMAC(plaintext, KmsConfig.getIndexSalt());
    }

    private static String sha256_HMAC(String message, String secret) {
        String rst = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            sha256_HMAC.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes("UTF-8"));
            rst = byteArrayToHexString(bytes);
        } catch (Exception e) {
            System.out.println("KmsFactory method HmacSHA256 error, " + e.getMessage());
        }
        return rst;
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        String tmp;
        for (int i = 0; b != null && i < b.length; i++) {
            tmp = Integer.toHexString(b[i] & 0XFF);
            if (tmp.length() == 1)
                sb.append('0');
            sb.append(tmp);
        }
        return sb.toString().toLowerCase();
    }

    private KmsClient getKmsClient() {
        String schema = StringUtils.isBlank(KmsConfig.getSchema()) ? "https" : KmsConfig.getSchema();
        Protocol protocol = Protocol.HTTPS;
        if ("http".equalsIgnoreCase(schema)){
            protocol = Protocol.HTTP;
        }
        KmsClient client =  KmsClient.builder()
                .credentialsProvider(new StaticCredentialsProvider(KmsConfig.getAccessKey(), KmsConfig.getSecretKey()))
                .httpRequestConfig(new HttpRequestConfig.Builder().protocol(protocol).build())
                .environment(new Environment.Builder().endpoint(KmsConfig.getEndpoint()).build())
                .build();
        return client;
    }

}
