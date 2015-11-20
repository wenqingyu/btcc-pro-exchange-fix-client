package com.btcc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

/**
 * Created by zhenning on 15/9/24.
 */
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    Properties prop;

    private static Config _instance = new Config();
    public static Config getInstance()
    {
        return _instance;
    }

    private Config() {
        this.prop = new Properties();
        File configFile = new File("quickfix-client.properties");
        if(configFile.exists())
        {
            logger.debug("Configure file is found in path : {}", configFile.getAbsolutePath());

            try(InputStream in = new FileInputStream(configFile))
            {
                prop.load(in);
                in.close();
            } catch (IOException e) {
                logger.debug("load property error", e);
            }
        }else {
            logger.debug("Configure file is not found in path : {}", configFile.getAbsolutePath());
        }
    }

    private String getAccessKey()
    {
        return this.prop.getProperty("AccessKey");
    }

    private String getSecretKey()
    {
        return this.prop.getProperty("SecretKey");
    }

    public String getAccount() {
        String accessKey = Config.getInstance().getAccessKey();
        String secretKey = Config.getInstance().getSecretKey();
        try {
            return new GenAccountString().getAccountString(accessKey, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
