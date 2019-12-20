package com.leyou.gateway.config;

import com.leyou.common.utils.RsaUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * 保存此授权中心微服务的公钥和私钥以及全局配置
 */
@ConfigurationProperties(prefix = "mall.jwt")
@Getter
@Setter
public class JwtProperties {

    private String pubKeyPath;// 公钥

    private PublicKey publicKey; // 公钥

    private String cookieName;

    private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);

    /**
     * 在构造函数后(即从配置文件获取到属性后)读取私钥、公钥
     * 构造函数 -> 自动注入@Autowired -> @PostConstruct -> InitializingBean -> xml中配置的init-method="init"方法
     */
    @PostConstruct
    public void init(){
        try {
            // 从文件读取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            logger.error("初始化公钥和私钥失败！", e);
            throw new RuntimeException();
        }
    }

}