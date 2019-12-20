package com.leyou.order.interceptor;

import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JwtUtils;
import com.leyou.config.JwtProperties;
import com.leyou.util.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: 98050
 * @Time: 2018-10-25 18:17
 * @Feature: 登录拦截器
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private JwtProperties prop;

    public LoginInterceptor(JwtProperties prop) {
        this.prop = prop;
    }

    // threadLocal是一个map结构，key是thread，value是存储的值
    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        try {
            // 解析token -- 解析token要先获得cookie
            String token = CookieUtils.getCookieValue(request, prop.getCookieName());
            UserInfo user = JwtUtils.getInfoFromToken(token, prop.getPublicKey());

            // 保存user -- request和thread是“共享”的，所以可以把user放到这两个中
            tl.set(user); // key是不需要自己给定的，会自己获取

            return true;

        } catch (Exception e) {
            log.error("[购物车异常] 用户身份解析失败！", e);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        tl.remove();// 用完之后要删除
    }

    public static UserInfo getUser(){
        return tl.get();
    }
}
