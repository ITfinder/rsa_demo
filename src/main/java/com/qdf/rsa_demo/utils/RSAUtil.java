package com.qdf.rsa_demo.utils;

import com.qdf.rsa_demo.entity.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA实现工具类
 */

@Component
public class RSAUtil {

    private static Logger logger = LoggerFactory.getLogger(RSAUtil.class);
    private static String RSA_KEY_STORE = "D:/RSAKey.txt";
    private static String PUBLIC_KEY_STORE = "D:/publicKey.txt";

    /**
     * 秘钥对长度，使用2048加密解密过程所用时间过长，1024已经足够安全
     */
    private static final int KEY_LENGTH = 1024;
    /**
     * 加密实例
     */
    private static final String RSA_TOKEN = "rsa_token";

    /**
     * 生成keypair:使用KeyPairGenerator生成秘钥对
     * @return KeyPair 秘钥对
     * @throws Exception
     */
    public static KeyPair genKeyPair() throws Exception {
        //利用java自带的秘钥对生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(KEY_LENGTH);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        saveKeyPairAndPublicKey(keyPair);
        return keyPair;
    }

    public static void saveKeyPairAndPublicKey(KeyPair kp) throws Exception {
        RSAPublicKey pk = (RSAPublicKey)kp.getPublic();
        BigInteger publicModulus = pk.getModulus();//模数
        BigInteger publicExponent = pk.getPublicExponent();//指数
        String publicModulusStr = Base64Util.encryptBASE64((publicModulus+"").getBytes()).replaceAll("[\\s*\t\n\r]", "");//去掉换行符
        String publicExponentStr = Base64Util.encryptBASE64((publicExponent+"").getBytes()).replaceAll("[\\s*\t\n\r]", "");
        logger.info("Base64转码后的publicModulus===={}",publicModulusStr);
        logger.info("Base64转码后的publicExponent===={}",publicExponentStr);
        String result = "publicModulus=" + publicModulusStr + ",publicExponent=" + publicExponentStr;
        savePublicKey(result);
        saveKeyPair(kp);
    }

    private static void saveKeyPair(KeyPair kp) throws IOException {
        FileOutputStream fos = new FileOutputStream(RSA_KEY_STORE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        // 生成密钥
        oos.writeObject(kp);
        oos.close();
        fos.close();
    }

    private static void savePublicKey(String result) throws IOException {
        File file = new File(PUBLIC_KEY_STORE);
        if(!file.exists()){
            file.createNewFile();
        }

        PrintWriter pw = new PrintWriter(new FileWriter(PUBLIC_KEY_STORE));
        pw.println(result);
        pw.close();
    }

    /**
     * 获取秘钥对
     * @return
     * @throws Exception
     */
    public static KeyPair getKeyPair() throws Exception {
        FileInputStream fis = new FileInputStream(RSA_KEY_STORE);
        ObjectInputStream oos = new ObjectInputStream(fis);
        KeyPair kp = (KeyPair) oos.readObject();
        oos.close();
        fis.close();
        return kp;
    }

    /**
     * 获取公钥
     * @return
     */
    public static PublicKey getPublicKey()throws Exception {
        return getKeyPair().getPublic();
    }

    /**
     * 根据公钥的模数和指数获取公钥实例
     * @param modulusStr            base64编码后的公钥模数字符串
     * @param publicExponentStr     base64编码后的公钥指数字符串
     * @return PublicKey实例
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublicKey(String modulusStr,String publicExponentStr) throws Exception {
        modulusStr = new String(Base64Util.decryptBASE64(modulusStr));
        publicExponentStr = new String(Base64Util.decryptBASE64(publicExponentStr));
        BigInteger modulus = new BigInteger(modulusStr);//模数
        BigInteger publicExponent = new BigInteger(publicExponentStr);//指数
        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus,publicExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(rsaPublicKeySpec);
    }

    /**
     * 根据私钥的模数和指数获取私钥实例
     * @param modulusStr            base64编码后的私钥模数字符串
     * @param privateExponentStr    base64编码后的私钥指数字符串
     * @return PrivateKey实例
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivateKey(String modulusStr,String privateExponentStr) throws Exception{
        modulusStr = new String(Base64Util.decryptBASE64(modulusStr));
        privateExponentStr = new String(Base64Util.decryptBASE64(privateExponentStr));
        BigInteger modulus = new BigInteger(modulusStr);//模数
        BigInteger privateExponent = new BigInteger(privateExponentStr);//指数
        RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus,privateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(rsaPrivateKeySpec);
    }


    /**
     * 公钥加密，并转换成十六进制字符串打印出来
     * @param content            待加密字符串
     * @param publicKey          公钥实例
     * @return PrivateKey实例
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static String encrypt(String content, PublicKey publicKey) throws Exception{
        Cipher cipher=Cipher.getInstance("RSA");//java默认"RSA"="RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int splitLength=((RSAPublicKey)publicKey).getModulus().bitLength()/8-11;
        byte[][] arrays=splitBytes(content.getBytes(), splitLength);
        StringBuffer sb=new StringBuffer();
        for(byte[] array : arrays){
            sb.append(bytesToHexString(cipher.doFinal(array)));
        }
        return sb.toString();
    }

    /**
     * 私钥解密，并转换成十六进制字符串打印出来
     * @param content           待解密字符串
     * @param privateKey        私钥实例
     * @return 解密后对应的字符串
     * @throws Exception
     */
    public static String decrypt(String content, PrivateKey privateKey) throws Exception{
        Cipher cipher=Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        int splitLength=((RSAPrivateKey)privateKey).getModulus().bitLength()/8;
        byte[] contentBytes=hexString2Bytes(content);
        byte[][] arrays=splitBytes(contentBytes, splitLength);
        StringBuffer sb=new StringBuffer();
        for(byte[] array : arrays){
            sb.append(new String(cipher.doFinal(array)));
        }
        return sb.toString();
    }

    /**
     * 签名验证
     * @param afterRsa      经过rsa算法加密后的字符串
     * @param sign          接口调用者参数拼接后经过rsa加密的签名
     * @return  验证结果  true 成功  false 失败
     * @throws Exception
     */
    public static boolean verify(String afterRsa,String sign) throws Exception{
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) getKeyPair().getPrivate();
        String decryptStr = decrypt(sign,rsaPrivateKey);
        return decryptStr.equals(decrypt(afterRsa,rsaPrivateKey));
    }

    //拆分byte数组
    private static byte[][] splitBytes(byte[] bytes, int splitLength){
        int x; //商，数据拆分的组数，余数不为0时+1
        int y; //余数
        y=bytes.length%splitLength;
        if(y!=0){
            x=bytes.length/splitLength+1;
        }else{
            x=bytes.length/splitLength;
        }
        byte[][] arrays=new byte[x][];
        byte[] array;
        for(int i=0; i<x; i++){
            if(i==x-1 && bytes.length%splitLength!=0){
                array=new byte[bytes.length%splitLength];
                System.arraycopy(bytes, i*splitLength, array, 0, bytes.length%splitLength);
            }else{
                array=new byte[splitLength];
                System.arraycopy(bytes, i*splitLength, array, 0, splitLength);
            }
            arrays[i]=array;
        }
        return arrays;
    }
    //byte数组转十六进制字符串
    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;
        for (int i = 0; i < bytes.length; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }
    //十六进制字符串转byte数组
    private static byte[] hexString2Bytes(String hex) {
        int len = (hex.length() / 2);
        hex=hex.toUpperCase();
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }
    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static BigInteger getPublicModulus(RSAPublicKey pk){
        return pk.getModulus();
    }

    public static BigInteger getPrivateModulus(RSAPrivateKey pk){
        return pk.getModulus();
    }

    public static BigInteger getPublicExponent(RSAPublicKey pk){
        return pk.getPublicExponent();
    }

    public static BigInteger getPrivateExponent(RSAPrivateKey pk){
        return pk.getPrivateExponent();
    }

    public static JsonResult printLogInfo(KeyPair keyPair, String msg) throws Exception {
        //打印日志
        //公钥指数、模数
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyExponent = Base64Util.encryptBASE64((RSAUtil.getPublicExponent(publicKey)+"").getBytes()).replaceAll("[\\s*\t\n\r]", "");
        String publicKeyModulus = Base64Util.encryptBASE64((RSAUtil.getPublicModulus(publicKey)+"").getBytes()).replaceAll("[\\s*\t\n\r]", "");
        //私钥指数、模数
        RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
        String privateKeyExponent = Base64Util.encryptBASE64((RSAUtil.getPrivateExponent(privateKey)+"").getBytes()).replaceAll("[\\s*\t\n\r]", "");
        String privateKeyModulus =  Base64Util.encryptBASE64((RSAUtil.getPrivateModulus(privateKey)+"").getBytes()).replaceAll("[\\s*\t\n\r]", "");
        logger.info(msg);
        logger.info("publicKeyExponent=【{}】",publicKeyExponent);
        logger.info("publicKeyModulus=【{}】",publicKeyModulus);
        logger.info("privateKeyExponent=【{}】",privateKeyExponent);
        logger.info("privateKeyModulus=【{}】",privateKeyModulus);
        Map<String,Object> mapResult = new HashMap<>();
        mapResult.put("publicKeyExponent",publicKeyExponent);
        mapResult.put("publicKeyModulus",publicKeyModulus);
//        mapResult.put("privateKeyExponent",privateKeyExponent);
//        mapResult.put("privateKeyModulus",privateKeyModulus);
        JsonResult result = JsonResult.createSuccess(msg);
        result.addData(mapResult);
        return result;
    }

    public static void main(String[] args) throws Exception {
        genKeyPair();
//        String publicModulus = "MTAzMTA3ODg4ODA0MjY0MDkzNTIxNjYxNjI1MzY4NDY5MTE5NTQ5MDEzNjE2MzkwMTIxMTk0NTk1" +
//                "ODIzOTg1NTg4MzU4MDMwNjMxMzIxMjc3NjIwNDQxNTY0MzA1MDQ4MDU1MDAyMjQ1NDI2NTI3ODg0" +
//                "MDA1ODkwOTEzNzY5NzQzMzMzNDIxMTY3NzUxMTk2OTI4OTU5MTgyNDU2NzU4NzYzMzA2NTA2NzA3" +
//                "NzIxODUxOTg2NDA3MTg4MTg3MTUxOTc1MTY3OTMwMDkzMTIxMTk2Mzk0MDA2MTExMjY0NzI4NTUz" +
//                "MjYxNDEwNjQ5MjUyNTczMTU2ODAwMTExNTMyNjYzMjYxMzk5NDUwOTIxMDgzMzk4NDczODQ1NTY3" +
//                "NjkyMTc3MzAwOTc0OTE3NjA1MDE0MTcx";
//
//        String publicExponent = "NjU1Mzc=";
//
//        String privateModulus = "MTAzMTA3ODg4ODA0MjY0MDkzNTIxNjYxNjI1MzY4NDY5MTE5NTQ5MDEzNjE2MzkwMTIxMTk0NTk1" +
//                "ODIzOTg1NTg4MzU4MDMwNjMxMzIxMjc3NjIwNDQxNTY0MzA1MDQ4MDU1MDAyMjQ1NDI2NTI3ODg0" +
//                "MDA1ODkwOTEzNzY5NzQzMzMzNDIxMTY3NzUxMTk2OTI4OTU5MTgyNDU2NzU4NzYzMzA2NTA2NzA3" +
//                "NzIxODUxOTg2NDA3MTg4MTg3MTUxOTc1MTY3OTMwMDkzMTIxMTk2Mzk0MDA2MTExMjY0NzI4NTUz" +
//                "MjYxNDEwNjQ5MjUyNTczMTU2ODAwMTExNTMyNjYzMjYxMzk5NDUwOTIxMDgzMzk4NDczODQ1NTY3" +
//                "NjkyMTc3MzAwOTc0OTE3NjA1MDE0MTcx";
//
//        String privateExponent = "NTQ3MTA3MjU3NDUyNzc5OTMzODEwNzkxMzEyMTEyMDE1MTQxNDE4ODg1Mjg3Njk0OTYwNzkxOTI5" +
//                "NzE1OTAwNzYzNzEzNzA2MDI5Mjk2MDM1NzEyNzgxMzkwNDY3NjgxNzg0NzQ4MDE3ODY4OTE0ODM2" +
//                "ODU2MjU3NzY2ODEwMDE5NDQyNDA5ODAwMzQ5MDk2NDE5NTExOTY1NjM5Mzc4Mzk3NDQwMDY0MTU2" +
//                "NjQyMjc2OTY2NzQxMzMxMjc4OTc2NjIwODU5ODIyNTYwOTIzODI2NDMxMzc2MDYyOTQ4MTYzMzk4" +
//                "NjQ2MzcyMDMxOTAyMjM0OTE1ODg4NjE3NTE4OTQ4OTE2Njc3ODQ1MDcwMjEyNDE1NDE0MjQyNDQ0" +
//                "MjI3MjcyMDUzNDczNjQzOTU3ODMyNzM=";
//
//
//        Map<String,Object> param = new HashMap<>();
//        param.put("wid",1);
//        param.put("rsa","rsa");
//        param.put("asp","asp");
//        param.put("php","php");
//        param.put("java","java");
//
//        param = TreeMapUtils.sortMapByKey(param);
//        StringBuffer sb = new StringBuffer();
//        for(Map.Entry<String, Object> entry:param.entrySet()){
//            System.out.println("key:"+entry.getKey()+"--value:"+entry.getValue());
//            sb.append(entry.getValue());
//        }
//        String sign = RSAUtil.encrypt(sb.toString(),RSAUtil.getPublicKey());
//
//        logger.info(sign);
    }

}
