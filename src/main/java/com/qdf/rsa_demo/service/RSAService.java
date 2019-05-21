package com.qdf.rsa_demo.service;

import java.security.KeyPair;
import java.util.Map;

public interface RSAService {

    /**
     * 初始化秘钥对
     * @return
     * @throws Exception
     */
    KeyPair generateKeyPair() throws Exception;

    /**
     * 验证签名
     * @param param 经过key排序后的参数
     * @return
     * @throws Exception
     */
    boolean verify(Map<String, Object> param, String sign) throws Exception ;

    KeyPair testConnect() throws Exception;

    String getSignByParam(String key);

    String getResultByParam(String content);
}
