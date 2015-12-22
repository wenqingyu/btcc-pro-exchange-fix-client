package com.btcc;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhenning on 15/9/23.
 */
public class GenAccountString {

    private String bytArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (byte b : a){
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    private String getSignature(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {

        // get an hmac_sha1 key from the raw key bytes
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");

        // get an hmac_sha1 Mac instance and initialize with the signing key
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);

        // compute the hmac on input data bytes
        byte[] rawHmac = mac.doFinal(data.getBytes());

        return bytArrayToHex(rawHmac);
    }

    public String getAccountString(String accesskey, String secretkey) throws InvalidKeyException, NoSuchAlgorithmException
    {
        String methodstr = "method=getForwardsAccountInfo&params=";
        String tonce = "" + (System.currentTimeMillis() * 1000);
        String params = "tonce=" + tonce.toString() + "&accesskey=" + accesskey + "&requestmethod=post&id=1&" + methodstr;

        String hash = getSignature(params, secretkey);
        String userpass = accesskey + ":" + hash;
        String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());

        return String.format("%s:%s", tonce, basicAuth);
    }
}
