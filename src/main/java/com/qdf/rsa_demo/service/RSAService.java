package com.qdf.rsa_demo.service;

import java.security.KeyPair;

public interface RSAService {

    /**
     * 初始化秘钥对
     * @return
     * @throws Exception
     */
    KeyPair generateKeyPair() throws Exception;

    /**
     * 验证签名
     * @param afterRsa 经过rsa算法加密后的字符串
     * @return
     * @throws Exception
     */
    boolean verify(String afterRsa) throws Exception ;
}
