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
import com.iot.center.auth.mapper.BlackIpMapper;
import com.iot.center.auth.service.BlackIpService;
import com.dc3.common.bean.Pages;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.BlackIpDto;
import com.dc3.common.exception.ServiceException;
import com.dc3.common.model.BlackIp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户服务接口实现类
 *
 * @author njrsun20240123
 */
@Slf4j
@Service
public class BlackIpServiceImpl implements BlackIpService {

    @Resource
    private BlackIpMapper blackIpMapper;

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.BLACK_IP + Common.Cache.ID, key = "#blackIp.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.BLACK_IP + Common.Cache.IP, key = "#blackIp.ip", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.BLACK_IP + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public BlackIp add(BlackIp blackIp) {
        BlackIp select = selectByIp(blackIp.getIp());
        if (null != select) {
            throw new ServiceException("The ip already exists in the blacklist");
        }
        if (blackIpMapper.insert(blackIp) > 0) {
            return blackIpMapper.selectById(blackIp.getId());
        }
        throw new ServiceException("The ip add to the blacklist failed");
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.BLACK_IP + Common.Cache.ID, key = "#id", condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.BLACK_IP + Common.Cache.IP, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.BLACK_IP + Common.Cache.LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean delete(Long id) {
        BlackIp blackIp = selectById(id);
        if (null == blackIp) {
            throw new ServiceException("The ip does not exist in the blacklist");
        }
        return blackIpMapper.deleteById(id) > 0;
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.BLACK_IP + Common.Cache.ID, key = "#blackIp.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.BLACK_IP + Common.Cache.IP, key = "#blackIp.ip", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.BLACK_IP + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public BlackIp update(BlackIp blackIp) {
        blackIp.setIp(null).setUpdateTime(null);
        if (blackIpMapper.updateById(blackIp) > 0) {
            BlackIp select = blackIpMapper.selectById(blackIp.getId());
            blackIp.setIp(select.getIp());
            return select;
        }
        throw new ServiceException("The ip update failed in the blacklist");
    }

    @Override
    @Cacheable(value = Common.Cache.BLACK_IP + Common.Cache.ID, key = "#id", unless = "#result==null")
    public BlackIp selectById(Long id) {
        return blackIpMapper.selectById(id);
    }

    @Override
    @Cacheable(value = Common.Cache.BLACK_IP + Common.Cache.IP, key = "#ip", unless = "#result==null")
    public BlackIp selectByIp(String ip) {
        List<Dictionary> dictionaryList = new ArrayList<>(16);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList) {
            Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
            dictionaryList.add(driverDictionary);
        }

        LambdaQueryWrapper<BlackIp> queryWrapper = Wrappers.<BlackIp>query().lambda();
        queryWrapper.eq(BlackIp::getIp, ip);
        return blackIpMapper.selectOne(queryWrapper);
    }

    @Override
    @Cacheable(value = Common.Cache.BLACK_IP + Common.Cache.LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<BlackIp> list(BlackIpDto blackIpDto) {
        if (null == blackIpDto.getPage()) {
            blackIpDto.setPage(new Pages());
        }
        return blackIpMapper.selectPage(blackIpDto.getPage().convert(), fuzzyQuery(blackIpDto));
    }

    @Override
    public boolean checkBlackIpValid(String ip) {
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

        return false;
    }

    @Override
    public LambdaQueryWrapper<BlackIp> fuzzyQuery(BlackIpDto blackIpDto) {
        LambdaQueryWrapper<BlackIp> queryWrapper = Wrappers.<BlackIp>query().lambda();
        if (null != blackIpDto) {
            if (StrUtil.isNotBlank(blackIpDto.getIp())) {
                queryWrapper.like(BlackIp::getIp, blackIpDto.getIp());
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
        return queryWrapper;
    }

}
