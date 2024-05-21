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

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iot.center.auth.mapper.UserMapper;
import com.iot.center.auth.service.UserService;
import com.dc3.common.bean.Pages;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.UserDto;
import com.dc3.common.exception.DuplicateException;
import com.dc3.common.exception.NotFoundException;
import com.dc3.common.exception.ServiceException;
import com.dc3.common.model.User;
import com.dc3.common.utils.Dc3Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 用户服务接口实现类
 *
 * @author njrsun20240123
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.USER + Common.Cache.ID, key = "#user.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.USER + Common.Cache.NAME, key = "#user.name", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.USER + Common.Cache.PHONE, key = "#user.phone", condition = "#result!=null&&#user.phone!=null"),
                    @CachePut(value = Common.Cache.USER + Common.Cache.EMAIL, key = "#user.email", condition = "#result!=null&&#user.email!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.USER + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.USER + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public User add(User user) {
        // 判断用户是否存在
        if (null != selectByName(user.getName())) {
            throw new DuplicateException("The user already exists with username {}", user.getName());
        }

        // 判断 phone 是否存在
        if (StrUtil.isNotBlank(user.getPhone())) {
            if (null != selectByPhone(user.getPhone())) {
                throw new DuplicateException("The user already exists with phone {}", user.getPhone());
            }
        } else {
            user.setPhone(null);
        }


        // 判断 email 是否存在
        if (StrUtil.isNotBlank(user.getEmail())) {
            if (null != selectByEmail(user.getEmail())) {
                throw new DuplicateException("The user already exists with email {}", user.getEmail());
            }
        } else {
            user.setEmail(null);
        }


        if (userMapper.insert(user.setPassword(Dc3Util.md5(user.getPassword()))) > 0) {
            return userMapper.selectById(user.getId());
        }

        throw new ServiceException("The user add failed");
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.USER + Common.Cache.ID, key = "#id", condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.USER + Common.Cache.NAME, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.USER + Common.Cache.PHONE, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.USER + Common.Cache.EMAIL, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.USER + Common.Cache.DIC, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.USER + Common.Cache.LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean delete(Long id) {
        User user = selectById(id);
        if (null == user) {
            throw new NotFoundException("The user does not exist");
        }
        return userMapper.deleteById(id) > 0;
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.USER + Common.Cache.ID, key = "#user.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.USER + Common.Cache.NAME, key = "#user.name", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.USER + Common.Cache.PHONE, key = "#user.phone", condition = "#result!=null&&#user.phone!=null"),
                    @CachePut(value = Common.Cache.USER + Common.Cache.EMAIL, key = "#user.email", condition = "#result!=null&&#user.email!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.USER + Common.Cache.PHONE, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.USER + Common.Cache.EMAIL, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.USER + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.USER + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public User update(User user) {
        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        User byId = selectById(user.getId());
        // 判断 phone 是否修改
        if (StrUtil.isNotBlank(user.getPhone())) {
            if (null == byId.getPhone() || !byId.getPhone().equals(user.getPhone())) {
                if (null != selectByPhone(user.getPhone())) {
                    throw new DuplicateException("The user already exists with phone {}", user.getPhone());
                }
            }
        } else {
            user.setPhone(null);
        }

        // 判断 email 是否修改
        if (StrUtil.isNotBlank(user.getEmail())) {
            if (null == byId.getEmail() || !byId.getEmail().equals(user.getEmail())) {
                if (null != selectByEmail(user.getEmail())) {
                    throw new DuplicateException("The user already exists with email {}", user.getEmail());
                }
            }
        } else {
            user.setEmail(null);
        }

        user.setName(null).setUpdateTime(null);
        if (userMapper.updateById(user) > 0) {
            User select = userMapper.selectById(user.getId());
            user.setName(select.getName());
            return select;
        }
        throw new ServiceException("The user update failed");
    }

    @Override
    @Cacheable(value = Common.Cache.USER + Common.Cache.ID, key = "#id", unless = "#result==null")
    public User selectById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    @Cacheable(value = Common.Cache.USER + Common.Cache.NAME, key = "#name", unless = "#result==null")
    public User selectByName(String name) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        queryWrapper.eq(User::getName, name);
        User user = userMapper.selectOne(queryWrapper);

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

        if (null == user) {
            throw new NotFoundException("The user does not exist");
        }
        return user;
    }

    @Override
    @Cacheable(value = Common.Cache.USER + Common.Cache.PHONE, key = "#phone", unless = "#result==null")
    public User selectByPhone(String phone) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        queryWrapper.eq(User::getPhone, phone);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    @Cacheable(value = Common.Cache.USER + Common.Cache.EMAIL, key = "#email", unless = "#result==null")
    public User selectByEmail(String email) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        queryWrapper.eq(User::getEmail, email);


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
            reqDTO.setUserAgent(ServletUtils.getUserAgent());
            reqDTO.setUserIp(ServletUtils.getClientIP());
            reqDTO.setResult(LoginResultEnum.SUCCESS.getResult());
            loginLogService.createLoginLog(reqDTO);
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

        return userMapper.selectOne(queryWrapper);
    }

    @Override
    @Cacheable(value = Common.Cache.USER + Common.Cache.LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<User> list(UserDto userDto) {
        if (!Optional.ofNullable(userDto.getPage()).isPresent()) {
            userDto.setPage(new Pages());
        }
        return userMapper.selectPage(userDto.getPage().convert(), fuzzyQuery(userDto));
    }

    @Override
    public boolean checkUserValid(String name) {
        User user = selectByName(name);
        if (null != user) {
            return user.getEnable();
        }

        user = selectByPhone(name);
        if (null != user) {
            return user.getEnable();
        }

        user = selectByEmail(name);
        if (null != user) {
            return user.getEnable();
        }

        return false;
    }

    @Override
    public boolean restPassword(Long id) {
        User user = selectById(id);
        if (null != user) {
            user.setPassword(Dc3Util.md5(Common.DEFAULT_PASSWORD));
            return null != update(user);
        }
        return false;
    }

    @Override
    public LambdaQueryWrapper<User> fuzzyQuery(UserDto userDto) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        if (null != userDto) {
            if (StrUtil.isNotBlank(userDto.getName())) {
                queryWrapper.like(User::getName, userDto.getName());
            }
        }
        return queryWrapper;
    }

}
