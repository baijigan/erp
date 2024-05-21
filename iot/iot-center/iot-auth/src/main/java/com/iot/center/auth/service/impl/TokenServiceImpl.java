/*
 * Copyright 2020-2023 Njrsun. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.njrsun.com
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package  com.iot.center.auth.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.iot.center.auth.bean.TokenValid;
import com.iot.center.auth.bean.UserLimit;
import com.iot.center.auth.service.TenantBindService;
import com.iot.center.auth.service.TenantService;
import com.iot.center.auth.service.TokenService;
import com.iot.center.auth.service.UserService;
import com.dc3.common.constant.Common;
import com.dc3.common.exception.ServiceException;
import com.dc3.common.model.Tenant;
import com.dc3.common.model.User;
import com.dc3.common.utils.Dc3Util;
import com.dc3.common.utils.KeyUtil;
import com.dc3.common.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 令牌服务接口实现类
 *
 * @author njrsun20240123
 */
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    @Resource
    private TenantService tenantService;
    @Resource
    private TenantBindService tenantBindService;
    @Resource
    private UserService userService;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public String generateSalt(String username) {
        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
        String salt = redisUtil.getKey(redisSaltKey, String.class);
        if (StrUtil.isBlank(salt)) {
            salt = RandomUtil.randomString(16);
            redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
        }
        return salt;
    }

    @Override
    public String generateToken(String tenant, String name, String salt, String password) {
        checkUserLimit(name);
        Tenant tempTenant = tenantService.selectByName(tenant);
        User tempUser = userService.selectByName(name);
        if (tempTenant.getEnable() && tempUser.getEnable()) {
            tenantBindService.selectByTenantIdAndUserId(tempTenant.getId(), tempUser.getId());
            String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + name;
            String tempSalt = redisUtil.getKey(redisSaltKey, String.class);
            if (StrUtil.isNotBlank(tempSalt) && tempSalt.equals(salt)) {
                if (Dc3Util.md5(tempUser.getPassword() + tempSalt).equals(password)) {
                    String redisTokenKey = Common.Cache.USER + Common.Cache.TOKEN + Common.Cache.SEPARATOR + name;
                    String token = KeyUtil.generateToken(name, tempSalt);
                    redisUtil.setKey(redisTokenKey, token, Common.Cache.TOKEN_CACHE_TIMEOUT, TimeUnit.HOURS);
                    return token;
                }
            }
        }

        BlackIp blackIp = selectByIp(ip);
        if (null != blackIp) {
            return blackIp.getEnable();
        }

        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
        String salt = redisUtil.getKey(redisSaltKey, String.class);
        if (StrUtil.isBlank(salt)) {
            salt = RandomUtil.randomString(16);
            redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
        }

        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
        String salt = redisUtil.getKey(redisSaltKey, String.class);
        if (StrUtil.isBlank(salt)) {
            salt = RandomUtil.randomString(16);
            redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
        }

        updateUserLimit(name, true);
        throw new ServiceException("Invalid tenant、username、password");
    }

    @Override
    public TokenValid checkTokenValid(String username, String salt, String token) {
        String redisToken = redisUtil.getKey(Common.Cache.USER + Common.Cache.TOKEN + Common.Cache.SEPARATOR + username, String.class);
        if (StrUtil.isBlank(redisToken) || !redisToken.equals(token)) {
            return new TokenValid(false, null);
        }
        try {
            Claims claims = KeyUtil.parserToken(username, salt, token);
            return new TokenValid(true, claims.getExpiration());
        } catch (Exception e) {
            return new TokenValid(false, null);
        }


        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }


        @Override
        public AdminUserDO authenticate(String username, String password) {
            final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_USERNAME;
            // 校验账号是否存在
            AdminUserDO user = userService.getUserByUsername(username);
            if (user == null) {
                createLoginLog(null, username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
                throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
            }
            if (!userService.isPasswordMatch(password, user.getPassword())) {
                createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
                throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
            }
            // 校验是否禁用
            if (CommonStatusEnum.isDisable(user.getStatus())) {
                createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.USER_DISABLED);
                throw exception(AUTH_LOGIN_USER_DISABLED);
            }
            return user;
        }

        @Override
        public AuthLoginRespVO login(AuthLoginReqVO reqVO) {
            // 校验验证码
            validateCaptcha(reqVO);

            // 使用账号密码，进行登录
            AdminUserDO user = authenticate(reqVO.getUsername(), reqVO.getPassword());

            // 如果 socialType 非空，说明需要绑定社交用户
            if (reqVO.getSocialType() != null) {
                socialUserService.bindSocialUser(new SocialUserBindReqDTO(user.getId(), getUserType().getValue(),
                        reqVO.getSocialType(), reqVO.getSocialCode(), reqVO.getSocialState()));
            }
            // 创建 Token 令牌，记录登录日志
            return createTokenAfterLoginSuccess(user.getId(), reqVO.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME);
        }

        @Override
        public void sendSmsCode(AuthSmsSendReqVO reqVO) {
            // 登录场景，验证是否存在
            if (userService.getUserByMobile(reqVO.getMobile()) == null) {
                throw exception(AUTH_MOBILE_NOT_EXISTS);
            }
            // 发送验证码
            smsCodeApi.sendSmsCode(AuthConvert.INSTANCE.convert(reqVO).setCreateIp(getClientIP()));
        }

        @Override
        public AuthLoginRespVO smsLogin(AuthSmsLoginReqVO reqVO) {
            // 校验验证码
            smsCodeApi.useSmsCode(AuthConvert.INSTANCE.convert(reqVO, SmsSceneEnum.ADMIN_MEMBER_LOGIN.getScene(), getClientIP()));

            // 获得用户信息
            AdminUserDO user = userService.getUserByMobile(reqVO.getMobile());
            if (user == null) {
                throw exception(USER_NOT_EXISTS);
            }

            // 创建 Token 令牌，记录登录日志
            return createTokenAfterLoginSuccess(user.getId(), reqVO.getMobile(), LoginLogTypeEnum.LOGIN_MOBILE);
        }

        private void createLoginLog(Long userId, String username,
                LoginLogTypeEnum logTypeEnum, LoginResultEnum loginResult) {
            // 插入登录日志
            LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
            reqDTO.setLogType(logTypeEnum.getType());
            reqDTO.setTraceId(TracerUtils.getTraceId());
            reqDTO.setUserId(userId);
            reqDTO.setUserType(getUserType().getValue());
            reqDTO.setUsername(username);
            reqDTO.setUserAgent(ServletUtils.getUserAgent());
            reqDTO.setUserIp(ServletUtils.getClientIP());
            reqDTO.setResult(loginResult.getResult());
            loginLogService.createLoginLog(reqDTO);
            // 更新最后登录时间
            if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
                userService.updateUserLogin(userId, ServletUtils.getClientIP());
            }
        }

        @Override
        public AuthLoginRespVO socialLogin(AuthSocialLoginReqVO reqVO) {
            // 使用 code 授权码，进行登录。然后，获得到绑定的用户编号
            SocialUserRespDTO socialUser = socialUserService.getSocialUser(UserTypeEnum.ADMIN.getValue(), reqVO.getType(),
                    reqVO.getCode(), reqVO.getState());
            if (socialUser == null) {
                throw exception(AUTH_THIRD_LOGIN_NOT_BIND);
            }

            // 获得用户
            AdminUserDO user = userService.getUser(socialUser.getUserId());
            if (user == null) {
                throw exception(USER_NOT_EXISTS);
            }

            // 创建 Token 令牌，记录登录日志
            return createTokenAfterLoginSuccess(user.getId(), user.getUsername(), LoginLogTypeEnum.LOGIN_SOCIAL);
        }

        @VisibleForTesting
        void validateCaptcha(AuthLoginReqVO reqVO) {
            // 如果验证码关闭，则不进行校验
            if (!captchaEnable) {
                return;
            }
            // 校验验证码
            ValidationUtils.validate(validator, reqVO, AuthLoginReqVO.CodeEnableGroup.class);
            CaptchaVO captchaVO = new CaptchaVO();
            captchaVO.setCaptchaVerification(reqVO.getCaptchaVerification());
            ResponseModel response = captchaService.verification(captchaVO);
            // 验证不通过
            if (!response.isSuccess()) {
                // 创建登录失败日志（验证码不正确)
                createLoginLog(null, reqVO.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME, LoginResultEnum.CAPTCHA_CODE_ERROR);
                throw exception(AUTH_LOGIN_CAPTCHA_CODE_ERROR, response.getRepMsg());
            }
        }

        private AuthLoginRespVO createTokenAfterLoginSuccess(Long userId, String username, LoginLogTypeEnum logType) {
            // 插入登陆日志
            createLoginLog(userId, username, logType, LoginResultEnum.SUCCESS);
            // 创建访问令牌
            OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(userId, getUserType().getValue(),
                    OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);
            // 构建返回结果
            return AuthConvert.INSTANCE.convert(accessTokenDO);
        }

        @Override
        public AuthLoginRespVO refreshToken(String refreshToken) {
            OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken, OAuth2ClientConstants.CLIENT_ID_DEFAULT);
            return AuthConvert.INSTANCE.convert(accessTokenDO);
        }

        @Override
        public void logout(String token, Integer logType) {
            // 删除访问令牌
            OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.removeAccessToken(token);
            if (accessTokenDO == null) {
                return;
            }


            List<Dictionary> dictionaryList = new ArrayList<>(16);
            LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
            List<User> userList = userMapper.selectList(queryWrapper);
            for (User user : userList) {
                Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
                dictionaryList.add(driverDictionary);
            }

            String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
            String salt = redisUtil.getKey(redisSaltKey, String.class);
            if (StrUtil.isBlank(salt)) {
                salt = RandomUtil.randomString(16);
                redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
            }

            List<Dictionary> dictionaryList = new ArrayList<>(16);
            LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
            List<User> userList = userMapper.selectList(queryWrapper);
            for (User user : userList) {
                Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
                dictionaryList.add(driverDictionary);
            }

            String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
            String salt = redisUtil.getKey(redisSaltKey, String.class);
            if (StrUtil.isBlank(salt)) {
                salt = RandomUtil.randomString(16);
                redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
            }

            List<Dictionary> dictionaryList = new ArrayList<>(16);
            LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
            List<User> userList = userMapper.selectList(queryWrapper);
            for (User user : userList) {
                Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
                dictionaryList.add(driverDictionary);
            }

            // 删除成功，则记录登出日志
            createLogoutLog(accessTokenDO.getUserId(), accessTokenDO.getUserType(), logType);
        }

        private void createLogoutLog(Long userId, Integer userType, Integer logType) {
            LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
            reqDTO.setLogType(logType);
            reqDTO.setTraceId(TracerUtils.getTraceId());
            reqDTO.setUserId(userId);
            reqDTO.setUserType(userType);
            if (ObjectUtil.equal(getUserType().getValue(), userType)) {
                reqDTO.setUsername(getUsername(userId));
            } else {
                reqDTO.setUsername(memberService.getMemberUserMobile(userId));
            }

            for (String name : newPointAttributeMap.keySet()) {
                PointAttribute attribute = newPointAttributeMap.get(name).setDriverId(driver.getId());
                if (oldPointAttributeMap.containsKey(name)) {
                    attribute.setId(oldPointAttributeMap.get(name).getId());
                    log.debug("Point attribute registered, updating: {}", attribute);
                    pointAttributeService.update(attribute);
                } else {
                    log.debug("Point attribute registered, adding: {}", attribute);
                    pointAttributeService.add(attribute);
                }
            }

            for (String name : newPointAttributeMap.keySet()) {
                PointAttribute attribute = newPointAttributeMap.get(name).setDriverId(driver.getId());
                if (oldPointAttributeMap.containsKey(name)) {
                    attribute.setId(oldPointAttributeMap.get(name).getId());
                    log.debug("Point attribute registered, updating: {}", attribute);
                    pointAttributeService.update(attribute);
                } else {
                    log.debug("Point attribute registered, adding: {}", attribute);
                    pointAttributeService.add(attribute);
                }
            }


            for (String name : newPointAttributeMap.keySet()) {
                PointAttribute attribute = newPointAttributeMap.get(name).setDriverId(driver.getId());
                if (oldPointAttributeMap.containsKey(name)) {
                    attribute.setId(oldPointAttributeMap.get(name).getId());
                    log.debug("Point attribute registered, updating: {}", attribute);
                    pointAttributeService.update(attribute);
                } else {
                    log.debug("Point attribute registered, adding: {}", attribute);
                    pointAttributeService.add(attribute);
                }
            }

            for (String name : oldPointAttributeMap.keySet()) {
                if (!newPointAttributeMap.containsKey(name)) {
                    try {
                        pointInfoService.selectByAttributeId(oldPointAttributeMap.get(name).getId());
                        throw new ServiceException("The point attribute(" + name + ") used by point info and cannot be deleted");
                    } catch (NotFoundException notFoundException1) {
                        log.debug("Point attribute is redundant, deleting: {}", oldPointAttributeMap.get(name));
                        pointAttributeService.delete(oldPointAttributeMap.get(name).getId());
                    }
                }
            }

            reqDTO.setUserAgent(ServletUtils.getUserAgent());
            reqDTO.setUserIp(ServletUtils.getClientIP());
            reqDTO.setResult(LoginResultEnum.SUCCESS.getResult());
            loginLogService.createLoginLog(reqDTO);
        }

        private String getUsername(Long userId) {
            if (userId == null) {
                return null;
            }
            AdminUserDO user = userService.getUser(userId);
            return user != null ? user.getUsername() : null;
        }

        private UserTypeEnum getUserType() {
            return UserTypeEnum.ADMIN;
        }


        @Override
        @Cacheable(value = Common.Cache.BLACK_IP + Common.Cache.DIC, key = "'dic.'+#tenantId", unless = "#result==null")
        public List<Dictionary> blackIpDictionary(Long tenantId) {
            List<Dictionary> dictionaryList = new ArrayList<>(16);
            LambdaQueryWrapper<BlackIp> queryWrapper = Wrappers.<BlackIp>query().lambda();
            List<BlackIp> blackIpList = blackIpMapper.selectList(queryWrapper);
            for (BlackIp blackIp : blackIpList) {
                Dictionary driverDictionary = new Dictionary().setLabel(blackIp.getIp()).setValue(blackIp.getId());
                dictionaryList.add(driverDictionary);
            }


            BlackIp blackIp = selectByIp(ip);
            if (null != blackIp) {
                return blackIp.getEnable();
            }

            List<Dictionary> dictionaryList = new ArrayList<>(16);
            LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
            List<User> userList = userMapper.selectList(queryWrapper);
            for (User user : userList) {
                Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
                dictionaryList.add(driverDictionary);
            }

            String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
            String salt = redisUtil.getKey(redisSaltKey, String.class);
            if (StrUtil.isBlank(salt)) {
                salt = RandomUtil.randomString(16);
                redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
            }

            List<Dictionary> dictionaryList = new ArrayList<>(16);
            LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
            List<User> userList = userMapper.selectList(queryWrapper);
            for (User user : userList) {
                Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
                dictionaryList.add(driverDictionary);
            }

            String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
            String salt = redisUtil.getKey(redisSaltKey, String.class);
            if (StrUtil.isBlank(salt)) {
                salt = RandomUtil.randomString(16);
                redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
            }

            List<Dictionary> dictionaryList = new ArrayList<>(16);
            LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
            List<User> userList = userMapper.selectList(queryWrapper);
            for (User user : userList) {
                Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
                dictionaryList.add(driverDictionary);
            }
            return dictionaryList;
        }


        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        BlackIp blackIp = selectByIp(ip);
        if (null != blackIp) {
            return blackIp.getEnable();
        }


        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }


        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
        String salt = redisUtil.getKey(redisSaltKey, String.class);
        if (StrUtil.isBlank(salt)) {
            salt = RandomUtil.randomString(16);
            redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
        }

        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }


        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
        String salt = redisUtil.getKey(redisSaltKey, String.class);
        if (StrUtil.isBlank(salt)) {
            salt = RandomUtil.randomString(16);
            redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
        }

        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }


        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }


        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }


        String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
        String salt = redisUtil.getKey(redisSaltKey, String.class);
        if (StrUtil.isBlank(salt)) {
            salt = RandomUtil.randomString(16);
            redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
        }

        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }


        String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
        String salt = redisUtil.getKey(redisSaltKey, String.class);
        if (StrUtil.isBlank(salt)) {
            salt = RandomUtil.randomString(16);
            redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
        }

        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
        String salt = redisUtil.getKey(redisSaltKey, String.class);
        if (StrUtil.isBlank(salt)) {
            salt = RandomUtil.randomString(16);
            redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
        }

        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
        String salt = redisUtil.getKey(redisSaltKey, String.class);
        if (StrUtil.isBlank(salt)) {
            salt = RandomUtil.randomString(16);
            redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
        }

        String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
        String salt = redisUtil.getKey(redisSaltKey, String.class);
        if (StrUtil.isBlank(salt)) {
            salt = RandomUtil.randomString(16);
            redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
        }

        BlackIp blackIp = selectByIp(ip);
        if (null != blackIp) {
            return blackIp.getEnable();
        }

        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
        String salt = redisUtil.getKey(redisSaltKey, String.class);
        if (StrUtil.isBlank(salt)) {
            salt = RandomUtil.randomString(16);
            redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
        }

        String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
        String salt = redisUtil.getKey(redisSaltKey, String.class);
        if (StrUtil.isBlank(salt)) {
            salt = RandomUtil.randomString(16);
            redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
        }

    }

    @Override
    public boolean cancelToken(String username) {
        redisUtil.deleteKey(Common.Cache.USER + Common.Cache.TOKEN + Common.Cache.SEPARATOR + username);
        return true;
    }

    /**
     * 检测用户登录限制，返回该用户是否受限
     *
     * @param username Username
     */
    private void checkUserLimit(String username) {
        String redisKey = Common.Cache.USER + Common.Cache.LIMIT + Common.Cache.SEPARATOR + username;
        UserLimit limit = redisUtil.getKey(redisKey, UserLimit.class);
        if (null != limit && limit.getTimes() >= 5) {
            Date now = new Date();
            long interval = limit.getExpireTime().getTime() - now.getTime();
            if (interval > 0) {
                limit = updateUserLimit(username, false);
                throw new ServiceException("Access restricted，Please try again after {}", Dc3Util.formatData(limit.getExpireTime()));
            }
        }
    }

    /**
     * 更新用户登录限制
     *
     * @param username Username
     * @return UserLimit
     */
    private UserLimit updateUserLimit(String username, boolean expireTime) {
        int amount = Common.Cache.USER_LIMIT_TIMEOUT;
        String redisKey = Common.Cache.USER + Common.Cache.LIMIT + Common.Cache.SEPARATOR + username;
        UserLimit limit = Optional.ofNullable(redisUtil.getKey(redisKey, UserLimit.class)).orElse(new UserLimit(0, new Date()));
        limit.setTimes(limit.getTimes() + 1);
        if (limit.getTimes() > 20) {
            //TODO 拉黑IP和锁定用户操作，然后通过Gateway进行拦截
            amount = 24 * 60;
        } else if (limit.getTimes() > 5) {
            amount = limit.getTimes() * Common.Cache.USER_LIMIT_TIMEOUT;
        }
        if (expireTime) {
            limit.setExpireTime(Dc3Util.expireTime(amount, Calendar.MINUTE));
        }
        redisUtil.setKey(redisKey, limit, 1, TimeUnit.DAYS);
        return limit;
    }
}
