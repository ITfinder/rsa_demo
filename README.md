# rsa_demo
/**
 * 流程：
 *
 * 一、服务器生成秘钥对并保存到本地文件中
 * 考虑到秘钥对可能会经过用户去修改，动态修改.properties文件的方法查资料没查到，因此改为保存到本地文件
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
