package com.leyou.service;

import com.leyou.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import com.leyou.util.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String KEY_PREFIX="user:verify:";


     static Logger logger= LoggerFactory.getLogger(UserService.class);
    //data,要校验的数据，type要校验的类型1、用户名2、手机
    public Boolean checkUser(String data, Integer type) {
        User user = new User();
        if (type == 1) {
            user.setUsername(data);
        } else if (type == 2) {
            user.setPhone(data);
        } else {
            return null;
        }
        return this.userMapper.selectCount(user) == 0;
    }

    public Boolean sendVerifyCode(String phone) {
        if(StringUtils.isBlank(phone)){
            return null;
        }
        //生成验证码
        String code = NumberUtils.generateCode(5);
        try {
            //发送消息通知 发验证码短信
            Map<String, String> msg = new HashMap<>();
            msg.put("phone",phone);
            msg.put("code",code);
            //发送消息到rabbitmq
            this.amqpTemplate.convertAndSend("MALL.SMS.EXCHANGE","verifycode.sms",msg);
            //将验证码存入redis,设置失效时间
            this.redisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
            return true;
        } catch (AmqpException e) {
            logger.error("发送短信失败,phone：{}， code：{}", phone, code);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 用户注册
     * 步骤：
     * 查询redis的验证码
     * 1.校验验证码
     *  2.生成盐
     *  3.加盐加密
     *  4.新增用户
     * @param user
     * @param code
     * @return
     */
    public Boolean register(User user, String code) {
        try {
            //判断传过来的code是否为空
            if(StringUtils.isBlank(code)){
                return false;
            }
            //校验验证码是否正确
            String storeCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
            if(!code.equals(storeCode)){
               return false;
            }
            user.setCreated(new Date());
            String password = user.getPassword();
            //生成盐值
            String salt = CodecUtils.generateSalt();
            user.setSalt(salt);
            //给密码加盐
            password = CodecUtils.md5Hex(password, salt);
            user.setPassword(password);
            Boolean result=this.userMapper.insertSelective(user)==1;
            if(result){
                this.redisTemplate.delete(KEY_PREFIX + user.getPhone());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User login(String username, String password) {
        if(StringUtils.isBlank(password)||StringUtils.isBlank(username)){
            return null;
        }
        User queryUser = new User();
        queryUser.setUsername(username);
        User storeUser = this.userMapper.selectOne(queryUser);
        if(storeUser==null){
            return null;
        }
        String salt = storeUser.getSalt();
        if(!storeUser.getPassword().equals(CodecUtils.md5Hex(password, salt))) {
            return null;
        }
        return storeUser;
    }

    /**
     * 用户查询
     * @param username
     * @param password
     * @return
     */
    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        //判断user是否为空
        if(user == null){
            return  null;
        }
        //获取盐，对用户输入的密码加盐加密
        password = CodecUtils.md5Hex(password, user.getSalt());
        //和数据库中的密码比较
        if(StringUtils.equals(password,user.getPassword())){
            return user;
        }
        return null;
    }
}
