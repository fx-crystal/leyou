import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JwtUtils;
import com.leyou.common.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {
    private static String PUBLIC_KEY_PATH="D:\\project_liu\\mall_online\\code\\rsa\\rsa.pub";
    private static String PRIVATE_KEY_PATH="D:\\project_liu\\mall_online\\code\\rsa\\rsa.pri";
    private PublicKey publicKey;
    private PrivateKey privateKey;

    /**
     * 生成公钥和私钥
     */
    @Test
    public void testRsa() throws Exception {
        //根据密文"234"，生存rsa公钥和私钥,并写入指定文件
        RsaUtils.generateKey(PUBLIC_KEY_PATH, PRIVATE_KEY_PATH, "234");
    }

    /**
     * 读取公钥和私钥
     */
    @Before
    public void testGetRsa() throws Exception {
        //从文件中读取公钥
        this.publicKey = RsaUtils.getPublicKey(PUBLIC_KEY_PATH);
        //从文件中读取私钥
        this.privateKey = RsaUtils.getPrivateKey(PRIVATE_KEY_PATH);
    }

    /**
     * 生成token
     */
    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(1L, "Lee34"), privateKey, 5);
        System.out.println("token = " + token);
    }

    /**
     * 解析token
     */
    @Test
    public void testParseToken() throws Exception {
        //上边生成的token
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJMZWUzNCIsImV4cCI6MTU3NjU5NjA1N30.aObrsLTp919q-oRAmcetCepnZaA07s1LkA8c2Cpi8_zfhmYCeC7i_S70srLm7v466rDL3wSYZB8RN96mhENrpqCnl7UggKRyrCRVtnWFhgqnoi9HDbjl7vlvizKLAn-AVe4J8bkKwbQvKBT1_IETBk9dIUM_Oaf09puYYOPfO4I";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
