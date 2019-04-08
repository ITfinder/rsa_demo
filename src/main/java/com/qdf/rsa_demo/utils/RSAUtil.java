package com.qdf.rsa_demo.utils;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA实现工具类之保存到redis
 */
public class RSAUtil {
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

        //判断redis中是否存在，存在则使用，不存在则创建
        KeyPair redisKeyPair = RedisUtil.get("keyPair",KeyPair.class);
        if(redisKeyPair != null){
            return redisKeyPair;
        }

        //利用java自带的秘钥对生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(KEY_LENGTH);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        saveKeyPair(keyPair);
        return keyPair;
    }

    /**
     * 保存秘钥对到redis中
     * @param kp 秘钥对
     * @throws Exception
     */
    public static void saveKeyPair(KeyPair kp){
        RedisUtil.set("keyPair",kp);
    }

    /**
     * 获取秘钥对
     * @return
     * @throws Exception
     */
    public static KeyPair getKeyPair() throws Exception {
        return genKeyPair();
    }

    /**
     * 获取私钥
     * @return
     */
    public static PrivateKey getPrivateKey()throws Exception {
        return getKeyPair().getPrivate();
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
     * @return  验证结果  true 成功  false 失败
     * @throws Exception
     */
    public static boolean verify(String afterRsa) throws Exception{
        String data = RSAUtil.RSA_TOKEN;
        RSAPublicKey rsaPublicKey = (RSAPublicKey)getKeyPair().getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) getKeyPair().getPrivate();
        String encryptStr = encrypt(data,rsaPublicKey);
        String decryptStr = decrypt(encryptStr,rsaPrivateKey);
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


    public static void printParam()throws Exception {
        RSAPublicKey pk = (RSAPublicKey)getKeyPair().getPublic();
        BigInteger publicModulus = pk.getModulus();//模数
        BigInteger publicExponent = pk.getPublicExponent();//指数

        System.out.println("publicModulus===="+publicModulus);
        System.out.println("publicExponent===="+publicExponent);

        RSAPrivateKey privateKey = (RSAPrivateKey)getKeyPair().getPrivate();
        BigInteger privateModulus = privateKey.getModulus();
        BigInteger privateExponent = privateKey.getPrivateExponent();

        System.out.println("privateModulus===="+privateModulus);
        System.out.println("privateExponent===="+privateExponent);

        System.out.println(publicModulus.multiply(publicExponent) == privateExponent);
    }

    public static void main(String[] args) throws Exception {
        genKeyPair();
        printParam();

        String data = RSAUtil.RSA_TOKEN;

        RSAPublicKey rsaPublicKey = (RSAPublicKey)getKeyPair().getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) getKeyPair().getPrivate();

        //注意：中间加密后，如果要打印出来，必须以十六进制或者BCD码的形式打印，不能new String（byte[]）后，再从这个String里getbytes（），
        // 也不要用base64，不然会破坏原数据。
        String encryptStr = encrypt(data,rsaPublicKey);
        System.out.println("公钥加密，并转换成十六进制字符串结果：" + encryptStr);

        String decryptStr = decrypt(encryptStr,rsaPrivateKey);
        System.out.println("私钥解密，并转换成十六进制字符串结果：" + decryptStr);
    }

}
