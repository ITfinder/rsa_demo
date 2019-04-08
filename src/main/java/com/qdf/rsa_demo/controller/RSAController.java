package com.qdf.rsa_demo.controller;

import com.qdf.rsa_demo.entity.JsonResult;
import com.qdf.rsa_demo.entity.Param;
import com.qdf.rsa_demo.service.RSAService;
import com.qdf.rsa_demo.utils.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;


/**
 * 流程：
 *
 * 一、服务器生成秘钥对并保存到redis中
 *
 * 二、加密解密过程：
 * 1. 客户端向服务器申请公钥钥；
 *
 * 2. 服务器接收到客户端的申请以后，读取文redis的公钥，并将公钥发给客户端
 *
 * 3. 客户端接收到公钥以后，使用公钥对密码加密，然后将密文发给服务器；
 *
 * 4. 服务器接收到密文以后，使用私钥解密，判断是否是正确的密码。
 */
@Controller
@RequestMapping("/rsa")
public class RSAController {

    private static Logger logger = LoggerFactory.getLogger(RSAController.class);

    @Autowired
    private RSAService rsaService;

    /**
     * 初始化秘钥对
     * @return ResultJson 结果封装类
     */
    @ResponseBody
    @RequestMapping(value = "generateKeyPair",method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public JsonResult generateKeyPair(){
        try {
            logger.info("初始化秘钥对开始执行.....");
            //调用初始化秘钥对方法
            KeyPair keyPair = rsaService.generateKeyPair();

            //打印日志
            //公钥指数、模数
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            String publicKeyExponent = RSAUtil.getPublicExponent(publicKey).toString(16);
            String publicKeyModulus = RSAUtil.getPublicModulus(publicKey).toString(16);
            //私钥指数、模数
            RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
            String privateKeyExponent = RSAUtil.getPrivateExponent(privateKey).toString(16);
            String privateKeyModulus = RSAUtil.getPrivateModulus(privateKey).toString(16);

            logger.info("初始化秘钥对:publicKeyExponent={},publicKeyModulus={},privateKeyExponent={},privateKeyModulus={}",publicKeyExponent,publicKeyModulus,privateKeyExponent,privateKeyModulus);

            Map<String,Object> mapResult = new HashMap<>();
            mapResult.put("publicKeyExponent",publicKeyExponent);
            mapResult.put("publicKeyModulus",publicKeyModulus);
            mapResult.put("privateKeyExponent",privateKeyExponent);
            mapResult.put("privateKeyModulus",privateKeyModulus);
            JsonResult result = JsonResult.createSuccess("初始化秘钥对成功");
            result.addData(mapResult);
            return result;
        }catch (Exception e){
            logger.error("初始化秘钥对出现异常",e);
            return JsonResult.createFalied("初始化秘钥对出现异常");
        }
    }

    /**
     * 签名验证
     * @param param:
     *             wid:
     *             token:
     *             timestamp:
     *             afterRsa:经过rsa算法加密后的字符串
     * @return ResultJson 结果封装类
     */
    @ResponseBody
    @RequestMapping(value = "verifyData",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public JsonResult verifyData(@RequestBody Param param){
        try {
            String wid = param.getWid();
            String accessToken = param.getToken();
            String timestamp = param.getTimestamp();
            String afterRsa= param.getAfterRsa();

            logger.info("入参：wid={},accessToken={},timestamp={},afterRsa={}",wid,accessToken,timestamp,afterRsa);

            if(!"1".equals(wid)){
                return JsonResult.createSuccess("无须验证签名");
            }
            String msg = rsaService.verify(afterRsa) ? "验证成功" : "验证失败";//验签操作
            return JsonResult.createSuccess(msg);
        } catch (Exception e) {
            logger.error("验证签名时出现异常",e);
            return JsonResult.createFalied("验证签名时出现异常");
        }
    }


}
