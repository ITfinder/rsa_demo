package com.qdf.rsa_demo.service.impl;

import com.qdf.rsa_demo.service.RSAService;
import com.qdf.rsa_demo.utils.RSAUtil;
import org.springframework.stereotype.Service;

import java.security.KeyPair;

@Service
public class RSAServiceImpl implements RSAService {

    /**
     * 初始化秘钥对
     * @return
     * @throws Exception
     */
    @Override
    public KeyPair generateKeyPair() throws Exception {
        return RSAUtil.genKeyPair();
    }

    /**
     * 验证签名
     * @param afterRsa 经过rsa算法加密后的字符串
     * @return
     * @throws Exception
     */
    @Override
    public boolean verify(String afterRsa) throws Exception {
        return RSAUtil.verify(afterRsa);
    }

}
