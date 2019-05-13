package com.qdf.rsa_demo.controller;

import com.qdf.rsa_demo.entity.JsonResult;
import com.qdf.rsa_demo.service.RSAService;
import com.qdf.rsa_demo.utils.RSAUtil;
import com.qdf.rsa_demo.utils.TreeMapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.KeyPair;
import java.util.Map;


/**
 * 流程：
 *
 * 一、服务器生成秘钥对并保存到本地文件中
 *
 * 二、加密解密过程：
 * 1.客户端拿到公钥文件RSAKey.txt
 * 2.将参数的key按照升序进行排序后，取到参数值直接拼接在一起，通过公钥进行加密得到sign
 *      如：{
 * 	            "wid":"1",
 * 	            "rsa":"rsa",
 * 	            "asp":"asp",
 * 	            "php":"php",
 * 	            "java":"java"
 *          }
 *     经过加密后得到sign="2171C477FB4714EE9C1B4F3989B4C823D4A51FBE7B8E195A1F216D27459C6970A2E6B631FEE692D61E4F213345CFA464B792B7C6720096E6894F61327B3E580B003BC7E949B5999F70C41C59E922350DCEA4E84D40B80865AD4E156638ECDA554FF960D12868A6311A5E5DF14952DE0405DAE116F78EC84AEF83FB0D753DAEAC"
 *     则最终请求参数为：
 *      如：{
 * 	            "wid":"1",
 * 	            "rsa":"rsa",
 * 	            "asp":"asp",
 * 	            "php":"php",
 * 	            "java":"java",
 * 	            "sign":"2171C477FB4714EE9C1B4F3989B4C823D4A51FBE7B8E195A1F216D27459C6970A2E6B631FEE692D61E4F213345CFA464B792B7C6720096E6894F61327B3E580B003BC7E949B5999F70C41C59E922350DCEA4E84D40B80865AD4E156638ECDA554FF960D12868A6311A5E5DF14952DE0405DAE116F78EC84AEF83FB0D753DAEAC"
 *          }
 *
 * 3.服务器取到wid值，判断为1则进行验签操作
 *
 * 4.取出sign，并从原来的param参数集合中移除sign，调用verify(Map<String, Object> param, String sign)方法
 *
 * 5.返回验签结果
 */
@Controller
@RequestMapping("/rsa")
public class RSAController {

    private static Logger logger = LoggerFactory.getLogger(RSAController.class);

    @Autowired
    private RSAService rsaService;

    /**
     * 测试连接
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "testConnect",method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public JsonResult testConnect(){
        try {
        KeyPair keyPair = rsaService.testConnect();
        return RSAUtil.printLogInfo(keyPair,"测试连接");
        }catch (Exception e){
            logger.error("测试连接出现异常",e);
            return JsonResult.createFalied("测试连接出现异常");
        }
    }

//    /**
//     * 初始化秘钥对
//     * @return ResultJson 结果封装类
//     */
//    @ResponseBody
//    @RequestMapping(value = "generateKeyPair",method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
//    public JsonResult generateKeyPair(){
//        try {
//            logger.info("初始化秘钥对开始执行.....");
//            //调用初始化秘钥对方法
//            KeyPair keyPair = rsaService.generateKeyPair();
//            //日志
//            JsonResult result = printLogInfo(keyPair,"初始化秘钥对");
//            return result;
//        }catch (Exception e){
//            logger.error("初始化秘钥对出现异常",e);
//            return JsonResult.createFalied("初始化秘钥对出现异常");
//        }
//    }

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
    public JsonResult verifyData(@RequestBody Map<String,Object> param){
        try {

            String wid = (String)param.get("wid");
            if(!"1".equals(wid)){
                return JsonResult.createSuccess("无须验证签名");
            }
            String sign= (String)param.get("sign");
            param.remove("sign");
            param = TreeMapUtils.sortMapByKey(param);

            String msg = rsaService.verify(param,sign) ? "验证成功" : "验证失败";//验签操作
            return JsonResult.createSuccess(msg);
        } catch (Exception e) {
            logger.error("验证签名时出现异常",e);
            return JsonResult.createFalied("验证签名时出现异常");
        }
    }



}
