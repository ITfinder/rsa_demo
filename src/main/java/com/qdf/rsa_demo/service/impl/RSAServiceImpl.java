package com.qdf.rsa_demo.service.impl;

import com.qdf.rsa_demo.service.RSAService;
import com.qdf.rsa_demo.utils.RSAUtil;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Map;

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
     * @param param 经过key排序后的参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean verify(Map<String,Object> param,String sign) throws Exception {
        //排序后将值拼接，然后rsa加密
        String result  = joinParam(param);
        return RSAUtil.verify(result,sign);
    }

    /**
     * 拼接算法:
     * 将排序后的map集合中的值依次取出，append，再经过RSA算法加密，返回
     * @param param 经过key排序后的参数
     * @return
     */
    private String joinParam(Map<String,Object> param) throws Exception {
        StringBuffer sb = new StringBuffer();
        for(Map.Entry<String, Object> entry:param.entrySet()){
            System.out.println("key:"+entry.getKey()+"--value:"+entry.getValue());
            sb.append(entry.getValue());
        }
        return RSAUtil.encrypt(sb.toString(),RSAUtil.getPublicKey());
    }

    @Override
    public KeyPair testConnect() throws Exception {
        return RSAUtil.getKeyPair();
    }

}
