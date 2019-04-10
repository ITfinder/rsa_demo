package com.qdf.rsa_demo;

import com.qdf.rsa_demo.service.RSAService;
import com.qdf.rsa_demo.utils.RSAUtil;
import com.qdf.rsa_demo.utils.TreeMapUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RsaDemoApplicationTests {

    private Logger logger = LoggerFactory.getLogger(RsaDemoApplicationTests.class);

    @Autowired
    private RSAService rsaService;

    /**
     * 测试连接
     */
    @Test
    public void testConnect() {
        try {
            KeyPair keyPair = rsaService.testConnect();
            RSAUtil.printLogInfo(keyPair,"测试连接");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 【请不要执行】
     * 该方法是初始化秘钥对。
     * 若想修改秘钥对，首先修改RSAUtil.java文件中RSA_KEY_STORE(秘钥对信息)地址和PUBLIC_KEY_STORE（公钥信息）地址，
     * 然后执行本方法，并将PUBLIC_KEY_STORE对应的文本发给接口调用方，以实现公钥同步
     */
    @Test
    public void generateKeyPair() {
        try {
            //调用初始化秘钥对方法
            KeyPair keyPair = rsaService.generateKeyPair();
            //日志
            RSAUtil.printLogInfo(keyPair,"初始化秘钥对");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 签名验证
     */
    @Test
    public void varify(){
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("wid",1);
            param.put("rsa","rsa");
            param.put("asp","asp");
            param.put("php","php");
            param.put("java","java");

            param = TreeMapUtils.sortMapByKey(param);
            StringBuffer sb = new StringBuffer();
            for(Map.Entry<String, Object> entry:param.entrySet()){
                System.out.println("key:"+entry.getKey()+"--value:"+entry.getValue());
                sb.append(entry.getValue());
            }
            String sign = RSAUtil.encrypt(sb.toString(),RSAUtil.getPublicKey());

            logger.info("sign===【{}】",sign);


            logger.info("结果：{}",rsaService.verify(param,sign));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
