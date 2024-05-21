/*
 * Copyright 2020-2024 Njrsun. All Rights Reserved.
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

package  com.iot.center.manager.service.impl ;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iot.center.manager.mapper.ProfileMapper;
import com.iot.center.manager.service.DeviceService;
import com.iot.center.manager.service.PointService;
import com.iot.center.manager.service.ProfileService;
import com.dc3.common.bean.Pages;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.ProfileDto;
import com.dc3.common.exception.DuplicateException;
import com.dc3.common.exception.NotFoundException;
import com.dc3.common.exception.ServiceException;
import com.dc3.common.model.Device;
import com.dc3.common.model.Point;
import com.dc3.common.model.Profile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ProfileService Impl
 *
 * @author njrsun20240123
 */
@Slf4j
@Service
public class ProfileServiceImpl implements ProfileService {

    @Resource
    private PointService pointService;
    @Resource
    private ProfileMapper profileMapper;
    @Resource
    private DeviceService deviceService;

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.PROFILE + Common.Cache.ID, key = "#profile.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.PROFILE + Common.Cache.NAME + Common.Cache.TYPE, key = "#profile.name+'.'+#profile.type+'.'+#profile.tenantId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.PROFILE + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.PROFILE + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public Profile add(Profile profile) {
        try {
            selectByNameAndType(profile.getName(), profile.getType(), profile.getTenantId());
            throw new DuplicateException("The profile already exists");
        } catch (NotFoundException notFoundException1) {
            if (profileMapper.insert(profile) > 0) {
                Profile select = profileMapper.selectById(profile.getId());
                try {
                    select.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                } catch (NotFoundException ignored) {
                }
                return select;
            }
            throw new ServiceException("The profile add failed");
        }
    }


    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.PROFILE + Common.Cache.ID, key = "#id", condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE + Common.Cache.NAME + Common.Cache.TYPE, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE + Common.Cache.DIC, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE + Common.Cache.LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean delete(Long id) {
        try {
            pointService.selectByProfileId(id);
            throw new ServiceException("The profile already bound by the point");
        } catch (NotFoundException notFoundException2) {
            selectById(id);
            return profileMapper.deleteById(id) > 0;
        }


        // add point
        Point point = new Point();
        point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
        try {
            point = pointService.add(point);

            // notify driver add point
            notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
        } catch (DuplicateException duplicateException) {
            point = pointService.selectByNameAndProfileId(pointName, profile.getId());
        } catch (Exception ignored) {
        }


        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
        driverMetadata.setDriverAttributeMap(driverAttributeMap);

        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
        driverMetadata.setPointAttributeMap(pointAttributeMap);


        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
        driverMetadata.setDriverAttributeMap(driverAttributeMap);

        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
        driverMetadata.setPointAttributeMap(pointAttributeMap);

        List<Device> devices = deviceService.selectByDriverId(driver.getId());
        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
        driverMetadata.setDriverInfoMap(driverInfoMap);


        Map<Long, Device> deviceMap = getDeviceMap(devices);
        driverMetadata.setDeviceMap(deviceMap);

        Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
        driverMetadata.setProfilePointMap(profilePointMap);
        List<Dictionary> dictionaries = new ArrayList<>(16);
        LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
        queryWrapper.eq(Profile::getTenantId, tenantId);
        List<Profile> profiles = profileMapper.selectList(queryWrapper);
        profiles.forEach(profile -> dictionaries.add(new Dictionary().setLabel(profile.getName()).setValue(profile.getId())));

        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
        driverMetadata.setDriverAttributeMap(driverAttributeMap);

        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
        driverMetadata.setPointAttributeMap(pointAttributeMap);

        List<Device> devices = deviceService.selectByDriverId(driver.getId());
        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
        driverMetadata.setDriverInfoMap(driverInfoMap);

        Map<Long, Device> deviceMap = getDeviceMap(devices);
        driverMetadata.setDeviceMap(deviceMap);

        List<Dictionary> dictionaries = driverDictionary(tenantId);
        dictionaries.forEach(driverDictionary -> {
            List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
            LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
            queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
            List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
            pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

            driverDictionary.setDisabled(true);
            driverDictionary.setValue(RandomUtil.randomLong());
            driverDictionary.setChildren(driverAttributeDictionaryList);
        });

        LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
        queryWrapper.eq(DriverAttribute::getName, name);
        queryWrapper.eq(DriverAttribute::getDriverId, driverId);
        DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.PROFILE + Common.Cache.ID, key = "#profile.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.PROFILE + Common.Cache.NAME + Common.Cache.TYPE, key = "#profile.name+'.'+#profile.type+'.'+#profile.tenantId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.PROFILE + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.PROFILE + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public Profile update(Profile profile) {
        selectById(profile.getId());
        profile.setUpdateTime(null);
        if (profileMapper.updateById(profile) > 0) {
            Profile select = profileMapper.selectById(profile.getId());
            try {
                select.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
            } catch (NotFoundException ignored) {
            }
            profile.setName(select.getName());
            return select;
        }
        throw new ServiceException("The profile update failed");
    }

    @Override
    @Cacheable(value = Common.Cache.PROFILE + Common.Cache.ID, key = "#id", unless = "#result==null")
    public Profile selectById(Long id) {
        Profile profile = profileMapper.selectById(id);
        if (null == profile) {
            throw new NotFoundException("The profile does not exist");
        }
        try {
            profile.setPointIds(pointService.selectByProfileId(id).stream().map(Point::getId).collect(Collectors.toSet()));
        } catch (NotFoundException ignored) {
        }
        return profile;
    }

    @Override
    @Cacheable(value = Common.Cache.PROFILE + Common.Cache.NAME + Common.Cache.TYPE, key = "#name+'.'+#type+'.'+#tenantId", unless = "#result==null")
    public Profile selectByNameAndType(String name, Short type, Long tenantId) {
        return profile;
    }

    @Override
    @Cacheable(value = Common.Cache.PROFILE + Common.Cache.LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public List<Profile> selectByIds(Set<Long> ids) {
        List<Profile> profiles = new ArrayList<>();
        if (null != ids && ids.size() > 0) {
            profiles = profileMapper.selectBatchIds(ids);
            profiles.forEach(profile -> {
                try {
                    profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                } catch (NotFoundException ignored) {
                }
            });
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

            if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                labelDto.setPage(new Pages());
            }

            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
            driverMetadata.setProfilePointMap(profilePointMap);

            LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
            queryWrapper.eq(DriverAttribute::getName, name);
            queryWrapper.eq(DriverAttribute::getDriverId, driverId);
            DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
            if (null == driverAttribute) {
                throw new NotFoundException("The driver attribute does not exist");
            }

            Map<Long, Device> deviceMap = getDeviceMap(devices);
            driverMetadata.setDeviceMap(deviceMap);
            LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
            queryWrapper.eq(Profile::getName, name);
            queryWrapper.eq(Profile::getType, type);
            queryWrapper.eq(Profile::getTenantId, tenantId);
            Profile profile = profileMapper.selectOne(queryWrapper);
            if (null == profile) {
                throw new NotFoundException("The profile does not exist");
            }
            try {
                profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
            } catch (NotFoundException ignored) {
            }

            profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                String status = redisUtil.getKey(key, String.class);
                status = null != status ? status : Common.Driver.Status.OFFLINE;
                statusMap.put(id, status);
            });

            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
            driverMetadata.setDriverAttributeMap(driverAttributeMap);

            Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
            driverMetadata.setPointAttributeMap(pointAttributeMap);

            List<Device> devices = deviceService.selectByDriverId(driver.getId());
            Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

            Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
            driverMetadata.setDriverInfoMap(driverInfoMap);

            Map<Long, Device> deviceMap = getDeviceMap(devices);
            driverMetadata.setDeviceMap(deviceMap);

            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
            driverMetadata.setProfilePointMap(profilePointMap);

            Page<Driver> page = driverService.list(driverDto);
            page.getRecords().forEach(driver -> {
                String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                String status = redisUtil.getKey(key, String.class);
                status = null != status ? status : Common.Driver.Status.OFFLINE;
                statusMap.put(driver.getId(), status);
            });

            LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
            queryWrapper.eq(DriverAttribute::getName, name);
            queryWrapper.eq(DriverAttribute::getDriverId, driverId);
            DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

            Device device = new Device();
            device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
            try {
                device = deviceService.add(device);

                // notify driver add device
                notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
            } catch (DuplicateException duplicateException) {
                device = deviceService.selectByName(deviceName, tenantId);
            } catch (Exception ignored) {
            }

            // add profile bind
            if (null != device.getId() && null != profile.getId()) {
                try {
                    ProfileBind profileBind = new ProfileBind();
                    profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                    profileBindService.add(profileBind);
                } catch (Exception ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }

                return new PointDetail(device.getId(), point.getId());
            }

            if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                try {
                    selectByNameAndProfileId(point.getName(), point.getProfileId());
                    throw new DuplicateException("The point already exists");
                } catch (NotFoundException ignored) {
                }
            }
            if (pointMapper.updateById(point) > 0) {
                Point select = pointMapper.selectById(point.getId());
                point.setName(select.getName()).setProfileId(select.getProfileId());
                return select;
            }


            Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
            driverMetadata.setPointAttributeMap(pointAttributeMap);

            List<Device> devices = deviceService.selectByDriverId(driver.getId());
            Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

            Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
            driverMetadata.setDriverInfoMap(driverInfoMap);


            Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
            driverMetadata.setPointAttributeMap(pointAttributeMap);

            List<Device> devices = deviceService.selectByDriverId(driver.getId());
            Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

            Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
            driverMetadata.setDriverInfoMap(driverInfoMap);


            List<Dictionary> dictionaries = driverDictionary(tenantId);
            dictionaries.forEach(driverDictionary -> {
                List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                driverDictionary.setDisabled(true);
                driverDictionary.setValue(RandomUtil.randomLong());
                driverDictionary.setChildren(driverAttributeDictionaryList);
            });


            //register driver attribute
            Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
            if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
            }

            Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
            try {
                List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
            } catch (NotFoundException ignored) {
            }

            // add profile bind
            if (null != device.getId() && null != profile.getId()) {
                try {
                    ProfileBind profileBind = new ProfileBind();
                    profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                    profileBindService.add(profileBind);
                } catch (Exception ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }

                return new PointDetail(device.getId(), point.getId());
            }

            try {
                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);
                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                driverMetadata.setPointInfoMap(devicePointInfoMap);

                return driverMetadata;
            } catch (NotFoundException ignored) {
            }

            // add point
            Point point = new Point();
            point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
            try {
                point = pointService.add(point);

                // notify driver add point
                notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
            } catch (DuplicateException duplicateException) {
                point = pointService.selectByNameAndProfileId(pointName, profile.getId());
            } catch (Exception ignored) {
            }
            try {
                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);
                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                driverMetadata.setPointInfoMap(devicePointInfoMap);

                return driverMetadata;
            } catch (NotFoundException ignored) {
            }

            // add private profile for device
            Profile profile = new Profile();
            profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
            try {
                profile = profileService.add(profile);
            } catch (DuplicateException duplicateException) {
                profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
            } catch (Exception ignored) {
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

            Map<Long, Device> deviceMap = getDeviceMap(devices);
            driverMetadata.setDeviceMap(deviceMap);

            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
            driverMetadata.setProfilePointMap(profilePointMap);

            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
            driverMetadata.setDriverAttributeMap(driverAttributeMap);

            List<Dictionary> dictionaries = new ArrayList<>(16);
            LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
            queryWrapper.eq(Driver::getTenantId, tenantId);

            // add device
            Device device = new Device();
            device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
            try {
                device = deviceService.add(device);

                // notify driver add device
                notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
            } catch (DuplicateException duplicateException) {
                device = deviceService.selectByName(deviceName, tenantId);
            } catch (Exception ignored) {
            }

            // add private profile for device
            Profile profile = new Profile();
            profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
            try {
                profile = profileService.add(profile);
            } catch (DuplicateException duplicateException) {
                profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
            } catch (Exception ignored) {
            }

            List<Dictionary> dictionaries = driverDictionary(tenantId);
            dictionaries.forEach(driverDictionary -> {
                List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                driverDictionary.setDisabled(true);
                driverDictionary.setValue(RandomUtil.randomLong());
                driverDictionary.setChildren(driverAttributeDictionaryList);
            });


            // add private profile for device
            Profile profile = new Profile();
            profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
            try {
                profile = profileService.add(profile);
            } catch (DuplicateException duplicateException) {
                profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
            } catch (Exception ignored) {
            }


            // add private profile for device
            Profile profile = new Profile();
            profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
            try {
                profile = profileService.add(profile);
            } catch (DuplicateException duplicateException) {
                profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
            } catch (Exception ignored) {
            }

            // add profile bind
            if (null != device.getId() && null != profile.getId()) {
                try {
                    ProfileBind profileBind = new ProfileBind();
                    profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                    profileBindService.add(profileBind);
                } catch (Exception ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }

                return new PointDetail(device.getId(), point.getId());
            }

            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
            driverMetadata.setProfilePointMap(profilePointMap);


            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
            driverMetadata.setDriverAttributeMap(driverAttributeMap);

            Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
            driverMetadata.setPointAttributeMap(pointAttributeMap);

            List<Device> devices = deviceService.selectByDriverId(driver.getId());
            Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

            Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
            driverMetadata.setDriverInfoMap(driverInfoMap);

            Map<Long, Device> deviceMap = getDeviceMap(devices);
            driverMetadata.setDeviceMap(deviceMap);

            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
            driverMetadata.setProfilePointMap(profilePointMap);

            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
            driverMetadata.setDriverAttributeMap(driverAttributeMap);

            Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
            driverMetadata.setPointAttributeMap(pointAttributeMap);


            List<Device> devices = deviceService.selectByDriverId(driver.getId());
            Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

            Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
            driverMetadata.setDriverInfoMap(driverInfoMap);

            Map<Long, Device> deviceMap = getDeviceMap(devices);
            driverMetadata.setDeviceMap(deviceMap);

            DriverAttributeDto driverAttributeDto = new DriverAttributeDto();
            driverAttributeDto.setDriverId(driverId);
            List<DriverAttribute> driverAttributes = driverAttributeMapper.selectList(fuzzyQuery(driverAttributeDto));
            if (null == driverAttributes || driverAttributes.size() < 1) {
                throw new NotFoundException("The driver attributes does not exist");
            }

            LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
            queryWrapper.eq(DriverAttribute::getName, name);
            queryWrapper.eq(DriverAttribute::getDriverId, driverId);
            DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

            LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
            queryWrapper.eq(DriverAttribute::getName, name);
            queryWrapper.eq(DriverAttribute::getDriverId, driverId);
            DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

            LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
            queryWrapper.eq(Profile::getName, name);
            queryWrapper.eq(Profile::getType, type);
            queryWrapper.eq(Profile::getTenantId, tenantId);
            Profile profile = profileMapper.selectOne(queryWrapper);
            if (null == profile) {
                throw new NotFoundException("The profile does not exist");
            }
            try {
                profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
            } catch (NotFoundException ignored) {
            }

            // add point
            Point point = new Point();
            point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
            try {
                point = pointService.add(point);

                // notify driver add point
                notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
            } catch (DuplicateException duplicateException) {
                point = pointService.selectByNameAndProfileId(pointName, profile.getId());
            } catch (Exception ignored) {
            }


            List<Driver> drivers = driverService.selectByProfileId(point.getProfileId());
            drivers.forEach(driver -> {
                DriverConfiguration operation = new DriverConfiguration().setType(Common.Driver.Type.POINT).setCommand(command).setContent(point);
                notifyDriver(driver, operation);
            });


            DriverAttributeDto driverAttributeDto = new DriverAttributeDto();
            driverAttributeDto.setDriverId(driverId);
            List<DriverAttribute> driverAttributes = driverAttributeMapper.selectList(fuzzyQuery(driverAttributeDto));
            if (null == driverAttributes || driverAttributes.size() < 1) {
                throw new NotFoundException("The driver attributes does not exist");
            }

            LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
            queryWrapper.eq(DriverAttribute::getName, name);
            queryWrapper.eq(DriverAttribute::getDriverId, driverId);
            DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

            LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
            queryWrapper.eq(DriverAttribute::getName, name);
            queryWrapper.eq(DriverAttribute::getDriverId, driverId);
            DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
        }


        if (!old.getPointAttributeId().equals(pointInfo.getPointAttributeId()) || !old.getDeviceId().equals(pointInfo.getDeviceId()) || !old.getPointId().equals(pointInfo.getPointId())) {
            try {
                selectByAttributeIdAndDeviceIdAndPointId(pointInfo.getPointAttributeId(), pointInfo.getDeviceId(), pointInfo.getPointId());
                throw new DuplicateException("The point info already exists");
            } catch (NotFoundException ignored) {
            }
        }


        // add device
        Device device = new Device();
        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
        try {
            device = deviceService.add(device);

            // notify driver add device
            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
        } catch (DuplicateException duplicateException) {
            device = deviceService.selectByName(deviceName, tenantId);
        } catch (Exception ignored) {
        }

        Page<Device> page = deviceService.list(deviceDto);
        page.getRecords().forEach(device -> {
            String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + device.getId();
            String status = redisUtil.getKey(key, String.class);
            status = null != status ? status : Common.Driver.Status.OFFLINE;
            statusMap.put(device.getId(), status);
        });

            try {
                device = deviceService.add(device);

                // notify driver add device
                notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
            } catch (DuplicateException duplicateException) {
                device = deviceService.selectByName(deviceName, tenantId);
            } catch (Exception ignored) {
            }


        try {
            device = deviceService.add(device);

            Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
            driverMetadata.setPointAttributeMap(pointAttributeMap);

            List<Device> devices = deviceService.selectByDriverId(driver.getId());
            Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

            Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
            driverMetadata.setDriverInfoMap(driverInfoMap);


            Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
            driverMetadata.setPointAttributeMap(pointAttributeMap);

            List<Device> devices = deviceService.selectByDriverId(driver.getId());
            Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

            Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
            driverMetadata.setDriverInfoMap(driverInfoMap);


            List<Dictionary> dictionaries = driverDictionary(tenantId);
            dictionaries.forEach(driverDictionary -> {
                List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                driverDictionary.setDisabled(true);
                driverDictionary.setValue(RandomUtil.randomLong());
                driverDictionary.setChildren(driverAttributeDictionaryList);
            });


            //register driver attribute
            Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
            if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
            }

            Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
            try {
                List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
            } catch (NotFoundException ignored) {
            }

            // add profile bind
            if (null != device.getId() && null != profile.getId()) {
                try {
                    ProfileBind profileBind = new ProfileBind();
                    profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                    profileBindService.add(profileBind);
                } catch (Exception ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }

                return new PointDetail(device.getId(), point.getId());
            }

            try {
                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);
                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                driverMetadata.setPointInfoMap(devicePointInfoMap);

                return driverMetadata;
            } catch (NotFoundException ignored) {
            }

            // add point
            Point point = new Point();
            point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
            try {
                point = pointService.add(point);

                // notify driver add point
                notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
            } catch (DuplicateException duplicateException) {
                point = pointService.selectByNameAndProfileId(pointName, profile.getId());
            } catch (Exception ignored) {
            }
            try {
                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);
                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                driverMetadata.setPointInfoMap(devicePointInfoMap);

                return driverMetadata;
            } catch (NotFoundException ignored) {
            }

            // add private profile for device
            Profile profile = new Profile();
            profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
            try {
                profile = profileService.add(profile);
            } catch (DuplicateException duplicateException) {
                profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
            } catch (Exception ignored) {
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


            LambdaQueryWrapper<PointInfo> queryWrapper = Wrappers.<PointInfo>query().lambda();
            if (null != pointInfoDto) {
                if (null != pointInfoDto.getPointAttributeId()) {
                    queryWrapper.eq(PointInfo::getPointAttributeId, pointInfoDto.getPointAttributeId());
                }
                if (null != pointInfoDto.getDeviceId()) {
                    queryWrapper.eq(PointInfo::getDeviceId, pointInfoDto.getDeviceId());
                }
                if (null != pointInfoDto.getPointId()) {
                    queryWrapper.eq(PointInfo::getPointId, pointInfoDto.getPointId());
                }
            }

            List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
            LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
            queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
            List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
            pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

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

                if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                    labelDto.setPage(new Pages());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
                if (null == driverAttribute) {
                    throw new NotFoundException("The driver attribute does not exist");
                }

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);
                LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
                queryWrapper.eq(Profile::getName, name);
                queryWrapper.eq(Profile::getType, type);
                queryWrapper.eq(Profile::getTenantId, tenantId);
                Profile profile = profileMapper.selectOne(queryWrapper);
                if (null == profile) {
                    throw new NotFoundException("The profile does not exist");
                }
                try {
                    profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                } catch (NotFoundException ignored) {
                }

                profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                    String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(id, status);
                });

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());


                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);
                return pointAttributeMapper.deleteById(id) > 0;


                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                    try {
                        selectByNameAndProfileId(point.getName(), point.getProfileId());
                        throw new DuplicateException("The point already exists");
                    } catch (NotFoundException ignored) {
                    }
                }
                if (pointMapper.updateById(point) > 0) {
                    Point select = pointMapper.selectById(point.getId());
                    point.setName(select.getName()).setProfileId(select.getProfileId());
                    return select;
                }


                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }
                    return new PointDetail(device.getId(), point.getId());
                }

                if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                    try {
                        selectByNameAndProfileId(point.getName(), point.getProfileId());
                        throw new DuplicateException("The point already exists");
                    } catch (NotFoundException ignored) {
                    }
                }
                if (pointMapper.updateById(point) > 0) {
                    Point select = pointMapper.selectById(point.getId());
                    point.setName(select.getName()).setProfileId(select.getProfileId());
                    return select;
                }

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Page<Driver> page = driverService.list(driverDto);
                page.getRecords().forEach(driver -> {
                    String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(driver.getId(), status);
                });


                LambdaQueryWrapper<Point> queryWrapper = Wrappers.<Point>query().lambda();
                if (null != pointDto) {
                    if (StrUtil.isNotBlank(pointDto.getName())) {
                        queryWrapper.like(Point::getName, pointDto.getName());
                    }
                    if (StrUtil.isNotBlank(pointDto.getType())) {
                        queryWrapper.eq(Point::getType, pointDto.getType());
                    }
                    if (null != pointDto.getRw()) {
                        queryWrapper.eq(Point::getRw, pointDto.getRw());
                    }
                    if (null != pointDto.getAccrue()) {
                        queryWrapper.eq(Point::getAccrue, pointDto.getAccrue());
                    }
                    if (null != pointDto.getProfileId()) {
                        queryWrapper.eq(Point::getProfileId, pointDto.getProfileId());
                    }
                    if (null != pointDto.getEnable()) {
                        queryWrapper.eq(Point::getEnable, pointDto.getEnable());
                    }
                    if (null != pointDto.getTenantId()) {
                        queryWrapper.eq(Point::getTenantId, pointDto.getTenantId());
                    }
                }
                return queryWrapper;
                // notify driver add device
                notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
            } catch (DuplicateException duplicateException) {
                device = deviceService.selectByName(deviceName, tenantId);
            } catch (Exception ignored) {
            }
            // check tenant
            R<Tenant> tenantR = tenantClient.selectByName(driverRegister.getTenant());
            if (!tenantR.isOk()) {
                throw new ServiceException("Invalid {}, {}", driverRegister.getTenant(), tenantR.getMessage());
            }

            // register driver
            Driver driver = driverRegister.getDriver().setTenantId(tenantR.getData().getId());
            log.info("Register driver {}", driver);
            try {
                Driver byServiceName = driverService.selectByServiceName(driver.getServiceName());
                log.debug("Driver already registered, updating {} ", driver);
                driver.setId(byServiceName.getId());
                driver = driverService.update(driver);
            } catch (NotFoundException notFoundException1) {
                log.debug("Driver does not registered, adding {} ", driver);
                try {
                    Driver byHostPort = driverService.selectByHostPort(driver.getType(), driver.getHost(), driver.getPort(), driver.getTenantId());
                    throw new ServiceException("The port(" + driver.getPort() + ") is already occupied by driver(" + byHostPort.getName() + "/" + byHostPort.getServiceName() + ")");
                } catch (NotFoundException notFoundException2) {
                    driver = driverService.add(driver);
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

                if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                    labelDto.setPage(new Pages());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
                if (null == driverAttribute) {
                    throw new NotFoundException("The driver attribute does not exist");
                }

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);
                LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
                queryWrapper.eq(Profile::getName, name);
                queryWrapper.eq(Profile::getType, type);
                queryWrapper.eq(Profile::getTenantId, tenantId);
                Profile profile = profileMapper.selectOne(queryWrapper);
                if (null == profile) {
                    throw new NotFoundException("The profile does not exist");
                }
                try {
                    profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                } catch (NotFoundException ignored) {
                }

                profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                    String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(id, status);
                });

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Page<Driver> page = driverService.list(driverDto);
                page.getRecords().forEach(driver -> {
                    String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(driver.getId(), status);
                });

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                    try {
                        selectByNameAndProfileId(point.getName(), point.getProfileId());
                        throw new DuplicateException("The point already exists");
                    } catch (NotFoundException ignored) {
                    }
                }
                if (pointMapper.updateById(point) > 0) {
                    Point select = pointMapper.selectById(point.getId());
                    point.setName(select.getName()).setProfileId(select.getProfileId());
                    return select;
                }


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                //register driver attribute
                Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                    driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                }

                Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                try {
                    List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                    byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                } catch (NotFoundException ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }
                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
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

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                List<Dictionary> dictionaries = new ArrayList<>(16);
                LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                queryWrapper.eq(Driver::getTenantId, tenantId);

                // add device
                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);


                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
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
            }        // check tenant
            R<Tenant> tenantR = tenantClient.selectByName(driverRegister.getTenant());
            if (!tenantR.isOk()) {
                throw new ServiceException("Invalid {}, {}", driverRegister.getTenant(), tenantR.getMessage());
            }

            // register driver
            Driver driver = driverRegister.getDriver().setTenantId(tenantR.getData().getId());
            log.info("Register driver {}", driver);
            try {
                Driver byServiceName = driverService.selectByServiceName(driver.getServiceName());
                log.debug("Driver already registered, updating {} ", driver);
                driver.setId(byServiceName.getId());
                driver = driverService.update(driver);
            } catch (NotFoundException notFoundException1) {
                log.debug("Driver does not registered, adding {} ", driver);
                try {
                    Driver byHostPort = driverService.selectByHostPort(driver.getType(), driver.getHost(), driver.getPort(), driver.getTenantId());
                    throw new ServiceException("The port(" + driver.getPort() + ") is already occupied by driver(" + byHostPort.getName() + "/" + byHostPort.getServiceName() + ")");
                } catch (NotFoundException notFoundException2) {
                    driver = driverService.add(driver);
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

                if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                    labelDto.setPage(new Pages());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
                if (null == driverAttribute) {
                    throw new NotFoundException("The driver attribute does not exist");
                }

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);
                LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
                queryWrapper.eq(Profile::getName, name);
                queryWrapper.eq(Profile::getType, type);
                queryWrapper.eq(Profile::getTenantId, tenantId);
                Profile profile = profileMapper.selectOne(queryWrapper);
                if (null == profile) {
                    throw new NotFoundException("The profile does not exist");
                }
                try {
                    profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                } catch (NotFoundException ignored) {
                }

                profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                    String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(id, status);
                });

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Page<Driver> page = driverService.list(driverDto);
                page.getRecords().forEach(driver -> {
                    String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(driver.getId(), status);
                });

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                    try {
                        selectByNameAndProfileId(point.getName(), point.getProfileId());
                        throw new DuplicateException("The point already exists");
                    } catch (NotFoundException ignored) {
                    }
                }
                if (pointMapper.updateById(point) > 0) {
                    Point select = pointMapper.selectById(point.getId());
                    point.setName(select.getName()).setProfileId(select.getProfileId());
                    return select;
                }


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                //register driver attribute
                Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                    driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                }

                Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                try {
                    List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                    byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                } catch (NotFoundException ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }
                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
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

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                List<Dictionary> dictionaries = new ArrayList<>(16);
                LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                queryWrapper.eq(Driver::getTenantId, tenantId);

                // add device
                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);


                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
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
            }        // check tenant
            R<Tenant> tenantR = tenantClient.selectByName(driverRegister.getTenant());
            if (!tenantR.isOk()) {
                throw new ServiceException("Invalid {}, {}", driverRegister.getTenant(), tenantR.getMessage());
            }

            // register driver
            Driver driver = driverRegister.getDriver().setTenantId(tenantR.getData().getId());
            log.info("Register driver {}", driver);
            try {
                Driver byServiceName = driverService.selectByServiceName(driver.getServiceName());
                log.debug("Driver already registered, updating {} ", driver);
                driver.setId(byServiceName.getId());
                driver = driverService.update(driver);
            } catch (NotFoundException notFoundException1) {
                log.debug("Driver does not registered, adding {} ", driver);
                try {
                    Driver byHostPort = driverService.selectByHostPort(driver.getType(), driver.getHost(), driver.getPort(), driver.getTenantId());
                    throw new ServiceException("The port(" + driver.getPort() + ") is already occupied by driver(" + byHostPort.getName() + "/" + byHostPort.getServiceName() + ")");
                } catch (NotFoundException notFoundException2) {
                    driver = driverService.add(driver);
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

                if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                    labelDto.setPage(new Pages());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
                if (null == driverAttribute) {
                    throw new NotFoundException("The driver attribute does not exist");
                }

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);
                LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
                queryWrapper.eq(Profile::getName, name);
                queryWrapper.eq(Profile::getType, type);
                queryWrapper.eq(Profile::getTenantId, tenantId);
                Profile profile = profileMapper.selectOne(queryWrapper);
                if (null == profile) {
                    throw new NotFoundException("The profile does not exist");
                }
                try {
                    profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                } catch (NotFoundException ignored) {
                }

                profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                    String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(id, status);
                });

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Page<Driver> page = driverService.list(driverDto);
                page.getRecords().forEach(driver -> {
                    String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(driver.getId(), status);
                });

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                    try {
                        selectByNameAndProfileId(point.getName(), point.getProfileId());
                        throw new DuplicateException("The point already exists");
                    } catch (NotFoundException ignored) {
                    }
                }
                if (pointMapper.updateById(point) > 0) {
                    Point select = pointMapper.selectById(point.getId());
                    point.setName(select.getName()).setProfileId(select.getProfileId());
                    return select;
                }


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                //register driver attribute
                Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                    driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                }

                Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                try {
                    List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                    byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                } catch (NotFoundException ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }
                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
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

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                List<Dictionary> dictionaries = new ArrayList<>(16);
                LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                queryWrapper.eq(Driver::getTenantId, tenantId);

                // add device
                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);


                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
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
            }        // check tenant
            R<Tenant> tenantR = tenantClient.selectByName(driverRegister.getTenant());
            if (!tenantR.isOk()) {
                throw new ServiceException("Invalid {}, {}", driverRegister.getTenant(), tenantR.getMessage());
            }

            // register driver
            Driver driver = driverRegister.getDriver().setTenantId(tenantR.getData().getId());
            log.info("Register driver {}", driver);
            try {
                Driver byServiceName = driverService.selectByServiceName(driver.getServiceName());
                log.debug("Driver already registered, updating {} ", driver);
                driver.setId(byServiceName.getId());
                driver = driverService.update(driver);
            } catch (NotFoundException notFoundException1) {
                log.debug("Driver does not registered, adding {} ", driver);
                try {
                    Driver byHostPort = driverService.selectByHostPort(driver.getType(), driver.getHost(), driver.getPort(), driver.getTenantId());
                    throw new ServiceException("The port(" + driver.getPort() + ") is already occupied by driver(" + byHostPort.getName() + "/" + byHostPort.getServiceName() + ")");
                } catch (NotFoundException notFoundException2) {
                    driver = driverService.add(driver);
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

                if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                    labelDto.setPage(new Pages());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
                if (null == driverAttribute) {
                    throw new NotFoundException("The driver attribute does not exist");
                }

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);
                LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
                queryWrapper.eq(Profile::getName, name);
                queryWrapper.eq(Profile::getType, type);
                queryWrapper.eq(Profile::getTenantId, tenantId);
                Profile profile = profileMapper.selectOne(queryWrapper);
                if (null == profile) {
                    throw new NotFoundException("The profile does not exist");
                }
                try {
                    profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                } catch (NotFoundException ignored) {
                }

                profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                    String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(id, status);
                });

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Page<Driver> page = driverService.list(driverDto);
                page.getRecords().forEach(driver -> {
                    String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(driver.getId(), status);
                });

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                    try {
                        selectByNameAndProfileId(point.getName(), point.getProfileId());
                        throw new DuplicateException("The point already exists");
                    } catch (NotFoundException ignored) {
                    }
                }
                if (pointMapper.updateById(point) > 0) {
                    Point select = pointMapper.selectById(point.getId());
                    point.setName(select.getName()).setProfileId(select.getProfileId());
                    return select;
                }


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                //register driver attribute
                Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                    driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                }

                Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                try {
                    List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                    byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                } catch (NotFoundException ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }
                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
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

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                List<Dictionary> dictionaries = new ArrayList<>(16);
                LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                queryWrapper.eq(Driver::getTenantId, tenantId);

                // add device
                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);


                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
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
            }        // check tenant
            R<Tenant> tenantR = tenantClient.selectByName(driverRegister.getTenant());
            if (!tenantR.isOk()) {
                throw new ServiceException("Invalid {}, {}", driverRegister.getTenant(), tenantR.getMessage());
            }

            // register driver
            Driver driver = driverRegister.getDriver().setTenantId(tenantR.getData().getId());
            log.info("Register driver {}", driver);
            try {
                Driver byServiceName = driverService.selectByServiceName(driver.getServiceName());
                log.debug("Driver already registered, updating {} ", driver);
                driver.setId(byServiceName.getId());
                driver = driverService.update(driver);
            } catch (NotFoundException notFoundException1) {
                log.debug("Driver does not registered, adding {} ", driver);
                try {
                    Driver byHostPort = driverService.selectByHostPort(driver.getType(), driver.getHost(), driver.getPort(), driver.getTenantId());
                    throw new ServiceException("The port(" + driver.getPort() + ") is already occupied by driver(" + byHostPort.getName() + "/" + byHostPort.getServiceName() + ")");
                } catch (NotFoundException notFoundException2) {
                    driver = driverService.add(driver);
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

                if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                    labelDto.setPage(new Pages());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
                if (null == driverAttribute) {
                    throw new NotFoundException("The driver attribute does not exist");
                }

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);
                LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
                queryWrapper.eq(Profile::getName, name);
                queryWrapper.eq(Profile::getType, type);
                queryWrapper.eq(Profile::getTenantId, tenantId);
                Profile profile = profileMapper.selectOne(queryWrapper);
                if (null == profile) {
                    throw new NotFoundException("The profile does not exist");
                }
                try {
                    profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                } catch (NotFoundException ignored) {
                }

                profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                    String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(id, status);
                });

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Page<Driver> page = driverService.list(driverDto);
                page.getRecords().forEach(driver -> {
                    String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(driver.getId(), status);
                });

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                    try {
                        selectByNameAndProfileId(point.getName(), point.getProfileId());
                        throw new DuplicateException("The point already exists");
                    } catch (NotFoundException ignored) {
                    }
                }
                if (pointMapper.updateById(point) > 0) {
                    Point select = pointMapper.selectById(point.getId());
                    point.setName(select.getName()).setProfileId(select.getProfileId());
                    return select;
                }


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                //register driver attribute
                Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                    driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                }

                Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                try {
                    List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                    byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                } catch (NotFoundException ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }
                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
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

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                List<Dictionary> dictionaries = new ArrayList<>(16);
                LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                queryWrapper.eq(Driver::getTenantId, tenantId);

                // add device
                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);


                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
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
            }        // check tenant
            R<Tenant> tenantR = tenantClient.selectByName(driverRegister.getTenant());
            if (!tenantR.isOk()) {
                throw new ServiceException("Invalid {}, {}", driverRegister.getTenant(), tenantR.getMessage());
            }

            // register driver
            Driver driver = driverRegister.getDriver().setTenantId(tenantR.getData().getId());
            log.info("Register driver {}", driver);
            try {
                Driver byServiceName = driverService.selectByServiceName(driver.getServiceName());
                log.debug("Driver already registered, updating {} ", driver);
                driver.setId(byServiceName.getId());
                driver = driverService.update(driver);
            } catch (NotFoundException notFoundException1) {
                log.debug("Driver does not registered, adding {} ", driver);
                try {
                    Driver byHostPort = driverService.selectByHostPort(driver.getType(), driver.getHost(), driver.getPort(), driver.getTenantId());
                    throw new ServiceException("The port(" + driver.getPort() + ") is already occupied by driver(" + byHostPort.getName() + "/" + byHostPort.getServiceName() + ")");
                } catch (NotFoundException notFoundException2) {
                    driver = driverService.add(driver);
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

                if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                    labelDto.setPage(new Pages());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
                if (null == driverAttribute) {
                    throw new NotFoundException("The driver attribute does not exist");
                }

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);
                LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
                queryWrapper.eq(Profile::getName, name);
                queryWrapper.eq(Profile::getType, type);
                queryWrapper.eq(Profile::getTenantId, tenantId);
                Profile profile = profileMapper.selectOne(queryWrapper);
                if (null == profile) {
                    throw new NotFoundException("The profile does not exist");
                }
                try {
                    profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                } catch (NotFoundException ignored) {
                }

                profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                    String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(id, status);
                });

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Page<Driver> page = driverService.list(driverDto);
                page.getRecords().forEach(driver -> {
                    String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(driver.getId(), status);
                });

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                    try {
                        selectByNameAndProfileId(point.getName(), point.getProfileId());
                        throw new DuplicateException("The point already exists");
                    } catch (NotFoundException ignored) {
                    }
                }
                if (pointMapper.updateById(point) > 0) {
                    Point select = pointMapper.selectById(point.getId());
                    point.setName(select.getName()).setProfileId(select.getProfileId());
                    return select;
                }


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                //register driver attribute
                Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                    driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                }

                Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                try {
                    List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                    byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                } catch (NotFoundException ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }
                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
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

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                List<Dictionary> dictionaries = new ArrayList<>(16);
                LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                queryWrapper.eq(Driver::getTenantId, tenantId);

                // add device
                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);


                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
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
            }        // check tenant
            R<Tenant> tenantR = tenantClient.selectByName(driverRegister.getTenant());
            if (!tenantR.isOk()) {
                throw new ServiceException("Invalid {}, {}", driverRegister.getTenant(), tenantR.getMessage());
            }

            // register driver
            Driver driver = driverRegister.getDriver().setTenantId(tenantR.getData().getId());
            log.info("Register driver {}", driver);
            try {
                Driver byServiceName = driverService.selectByServiceName(driver.getServiceName());
                log.debug("Driver already registered, updating {} ", driver);
                driver.setId(byServiceName.getId());
                driver = driverService.update(driver);
            } catch (NotFoundException notFoundException1) {
                log.debug("Driver does not registered, adding {} ", driver);
                try {
                    Driver byHostPort = driverService.selectByHostPort(driver.getType(), driver.getHost(), driver.getPort(), driver.getTenantId());
                    throw new ServiceException("The port(" + driver.getPort() + ") is already occupied by driver(" + byHostPort.getName() + "/" + byHostPort.getServiceName() + ")");
                } catch (NotFoundException notFoundException2) {
                    driver = driverService.add(driver);
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

                if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                    labelDto.setPage(new Pages());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
                if (null == driverAttribute) {
                    throw new NotFoundException("The driver attribute does not exist");
                }

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);
                LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
                queryWrapper.eq(Profile::getName, name);
                queryWrapper.eq(Profile::getType, type);
                queryWrapper.eq(Profile::getTenantId, tenantId);
                Profile profile = profileMapper.selectOne(queryWrapper);
                if (null == profile) {
                    throw new NotFoundException("The profile does not exist");
                }
                try {
                    profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                } catch (NotFoundException ignored) {
                }

                profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                    String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(id, status);
                });

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Page<Driver> page = driverService.list(driverDto);
                page.getRecords().forEach(driver -> {
                    String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(driver.getId(), status);
                });

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                    try {
                        selectByNameAndProfileId(point.getName(), point.getProfileId());
                        throw new DuplicateException("The point already exists");
                    } catch (NotFoundException ignored) {
                    }
                }
                if (pointMapper.updateById(point) > 0) {
                    Point select = pointMapper.selectById(point.getId());
                    point.setName(select.getName()).setProfileId(select.getProfileId());
                    return select;
                }


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                //register driver attribute
                Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                    driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                }

                Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                try {
                    List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                    byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                } catch (NotFoundException ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }
                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
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

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                List<Dictionary> dictionaries = new ArrayList<>(16);
                LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                queryWrapper.eq(Driver::getTenantId, tenantId);

                // add device
                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);


                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
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
            }        // check tenant
            R<Tenant> tenantR = tenantClient.selectByName(driverRegister.getTenant());
            if (!tenantR.isOk()) {
                throw new ServiceException("Invalid {}, {}", driverRegister.getTenant(), tenantR.getMessage());
            }

            // register driver
            Driver driver = driverRegister.getDriver().setTenantId(tenantR.getData().getId());
            log.info("Register driver {}", driver);
            try {
                Driver byServiceName = driverService.selectByServiceName(driver.getServiceName());
                log.debug("Driver already registered, updating {} ", driver);
                driver.setId(byServiceName.getId());
                driver = driverService.update(driver);
            } catch (NotFoundException notFoundException1) {
                log.debug("Driver does not registered, adding {} ", driver);
                try {
                    Driver byHostPort = driverService.selectByHostPort(driver.getType(), driver.getHost(), driver.getPort(), driver.getTenantId());
                    throw new ServiceException("The port(" + driver.getPort() + ") is already occupied by driver(" + byHostPort.getName() + "/" + byHostPort.getServiceName() + ")");
                } catch (NotFoundException notFoundException2) {
                    driver = driverService.add(driver);
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

                if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                    labelDto.setPage(new Pages());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
                if (null == driverAttribute) {
                    throw new NotFoundException("The driver attribute does not exist");
                }

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);
                LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
                queryWrapper.eq(Profile::getName, name);
                queryWrapper.eq(Profile::getType, type);
                queryWrapper.eq(Profile::getTenantId, tenantId);
                Profile profile = profileMapper.selectOne(queryWrapper);
                if (null == profile) {
                    throw new NotFoundException("The profile does not exist");
                }
                try {
                    profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                } catch (NotFoundException ignored) {
                }

                profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                    String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(id, status);
                });

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Page<Driver> page = driverService.list(driverDto);
                page.getRecords().forEach(driver -> {
                    String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(driver.getId(), status);
                });

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                    try {
                        selectByNameAndProfileId(point.getName(), point.getProfileId());
                        throw new DuplicateException("The point already exists");
                    } catch (NotFoundException ignored) {
                    }
                }
                if (pointMapper.updateById(point) > 0) {
                    Point select = pointMapper.selectById(point.getId());
                    point.setName(select.getName()).setProfileId(select.getProfileId());
                    return select;
                }


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                //register driver attribute
                Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                    driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                }

                Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                try {
                    List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                    byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                } catch (NotFoundException ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }
                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
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

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                List<Dictionary> dictionaries = new ArrayList<>(16);
                LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                queryWrapper.eq(Driver::getTenantId, tenantId);

                // add device
                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);


                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
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
            }        // check tenant
            R<Tenant> tenantR = tenantClient.selectByName(driverRegister.getTenant());
            if (!tenantR.isOk()) {
                throw new ServiceException("Invalid {}, {}", driverRegister.getTenant(), tenantR.getMessage());
            }

            // register driver
            Driver driver = driverRegister.getDriver().setTenantId(tenantR.getData().getId());
            log.info("Register driver {}", driver);
            try {
                Driver byServiceName = driverService.selectByServiceName(driver.getServiceName());
                log.debug("Driver already registered, updating {} ", driver);
                driver.setId(byServiceName.getId());
                driver = driverService.update(driver);
            } catch (NotFoundException notFoundException1) {
                log.debug("Driver does not registered, adding {} ", driver);
                try {
                    Driver byHostPort = driverService.selectByHostPort(driver.getType(), driver.getHost(), driver.getPort(), driver.getTenantId());
                    throw new ServiceException("The port(" + driver.getPort() + ") is already occupied by driver(" + byHostPort.getName() + "/" + byHostPort.getServiceName() + ")");
                } catch (NotFoundException notFoundException2) {
                    driver = driverService.add(driver);
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

                if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                    labelDto.setPage(new Pages());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
                if (null == driverAttribute) {
                    throw new NotFoundException("The driver attribute does not exist");
                }

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);
                LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
                queryWrapper.eq(Profile::getName, name);
                queryWrapper.eq(Profile::getType, type);
                queryWrapper.eq(Profile::getTenantId, tenantId);
                Profile profile = profileMapper.selectOne(queryWrapper);
                if (null == profile) {
                    throw new NotFoundException("The profile does not exist");
                }
                try {
                    profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                } catch (NotFoundException ignored) {
                }

                profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                    String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(id, status);
                });

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Page<Driver> page = driverService.list(driverDto);
                page.getRecords().forEach(driver -> {
                    String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(driver.getId(), status);
                });

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                    try {
                        selectByNameAndProfileId(point.getName(), point.getProfileId());
                        throw new DuplicateException("The point already exists");
                    } catch (NotFoundException ignored) {
                    }
                }
                if (pointMapper.updateById(point) > 0) {
                    Point select = pointMapper.selectById(point.getId());
                    point.setName(select.getName()).setProfileId(select.getProfileId());
                    return select;
                }


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                //register driver attribute
                Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                    driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                }

                Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                try {
                    List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                    byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                } catch (NotFoundException ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }
                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
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

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                List<Dictionary> dictionaries = new ArrayList<>(16);
                LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                queryWrapper.eq(Driver::getTenantId, tenantId);

                // add device
                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);


                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
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
            }        // check tenant
            R<Tenant> tenantR = tenantClient.selectByName(driverRegister.getTenant());
            if (!tenantR.isOk()) {
                throw new ServiceException("Invalid {}, {}", driverRegister.getTenant(), tenantR.getMessage());
            }

            // register driver
            Driver driver = driverRegister.getDriver().setTenantId(tenantR.getData().getId());
            log.info("Register driver {}", driver);
            try {
                Driver byServiceName = driverService.selectByServiceName(driver.getServiceName());
                log.debug("Driver already registered, updating {} ", driver);
                driver.setId(byServiceName.getId());
                driver = driverService.update(driver);
            } catch (NotFoundException notFoundException1) {
                log.debug("Driver does not registered, adding {} ", driver);
                try {
                    Driver byHostPort = driverService.selectByHostPort(driver.getType(), driver.getHost(), driver.getPort(), driver.getTenantId());
                    throw new ServiceException("The port(" + driver.getPort() + ") is already occupied by driver(" + byHostPort.getName() + "/" + byHostPort.getServiceName() + ")");
                } catch (NotFoundException notFoundException2) {
                    driver = driverService.add(driver);
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

                if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                    labelDto.setPage(new Pages());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
                if (null == driverAttribute) {
                    throw new NotFoundException("The driver attribute does not exist");
                }

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);
                LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
                queryWrapper.eq(Profile::getName, name);
                queryWrapper.eq(Profile::getType, type);
                queryWrapper.eq(Profile::getTenantId, tenantId);
                Profile profile = profileMapper.selectOne(queryWrapper);
                if (null == profile) {
                    throw new NotFoundException("The profile does not exist");
                }
                try {
                    profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                } catch (NotFoundException ignored) {
                }

                profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                    String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(id, status);
                });

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Page<Driver> page = driverService.list(driverDto);
                page.getRecords().forEach(driver -> {
                    String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                    String status = redisUtil.getKey(key, String.class);
                    status = null != status ? status : Common.Driver.Status.OFFLINE;
                    statusMap.put(driver.getId(), status);
                });

                LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                queryWrapper.eq(DriverAttribute::getName, name);
                queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                    try {
                        selectByNameAndProfileId(point.getName(), point.getProfileId());
                        throw new DuplicateException("The point already exists");
                    } catch (NotFoundException ignored) {
                    }
                }
                if (pointMapper.updateById(point) > 0) {
                    Point select = pointMapper.selectById(point.getId());
                    point.setName(select.getName()).setProfileId(select.getProfileId());
                    return select;
                }


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                //register driver attribute
                Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                    driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                }

                Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                try {
                    List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                    byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                } catch (NotFoundException ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }
                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
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

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                List<Dictionary> dictionaries = new ArrayList<>(16);
                LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                queryWrapper.eq(Driver::getTenantId, tenantId);

                // add device
                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);


                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }



                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);


                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                //register driver attribute
                Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                    driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                }

                Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                try {
                    List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                    byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                } catch (NotFoundException ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add point
                Point point = new Point();
                point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                try {
                    point = pointService.add(point);

                    // notify driver add point
                    notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                } catch (DuplicateException duplicateException) {
                    point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                } catch (Exception ignored) {
                }
                try {
                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);
                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                    driverMetadata.setPointInfoMap(devicePointInfoMap);

                    return driverMetadata;
                } catch (NotFoundException ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
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

                Map<Long, Device> deviceMap = getDeviceMap(devices);
                driverMetadata.setDeviceMap(deviceMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                List<Dictionary> dictionaries = new ArrayList<>(16);
                LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                queryWrapper.eq(Driver::getTenantId, tenantId);

                // add device
                Device device = new Device();
                device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                try {
                    device = deviceService.add(device);

                    // notify driver add device
                    notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                } catch (DuplicateException duplicateException) {
                    device = deviceService.selectByName(deviceName, tenantId);
                } catch (Exception ignored) {
                }

                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                List<Dictionary> dictionaries = driverDictionary(tenantId);
                dictionaries.forEach(driverDictionary -> {
                    List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                    LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                    queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                    List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                    pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                    driverDictionary.setDisabled(true);
                    driverDictionary.setValue(RandomUtil.randomLong());
                    driverDictionary.setChildren(driverAttributeDictionaryList);
                });


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }

                // add profile bind
                if (null != device.getId() && null != profile.getId()) {
                    try {
                        ProfileBind profileBind = new ProfileBind();
                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                        profileBindService.add(profileBind);
                    } catch (Exception ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }

                    return new PointDetail(device.getId(), point.getId());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);


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

                    if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                        labelDto.setPage(new Pages());
                    }

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                    queryWrapper.eq(DriverAttribute::getName, name);
                    queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                    DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
                    if (null == driverAttribute) {
                        throw new NotFoundException("The driver attribute does not exist");
                    }

                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);
                    LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
                    queryWrapper.eq(Profile::getName, name);
                    queryWrapper.eq(Profile::getType, type);
                    queryWrapper.eq(Profile::getTenantId, tenantId);
                    Profile profile = profileMapper.selectOne(queryWrapper);
                    if (null == profile) {
                        throw new NotFoundException("The profile does not exist");
                    }
                    try {
                        profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                    } catch (NotFoundException ignored) {
                    }

                    profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                        String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                        String status = redisUtil.getKey(key, String.class);
                        status = null != status ? status : Common.Driver.Status.OFFLINE;
                        statusMap.put(id, status);
                    });

                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);

                    Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                    driverMetadata.setPointAttributeMap(pointAttributeMap);

                    List<Device> devices = deviceService.selectByDriverId(driver.getId());
                    Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                    Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                    driverMetadata.setDriverInfoMap(driverInfoMap);

                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Page<Driver> page = driverService.list(driverDto);
                    page.getRecords().forEach(driver -> {
                        String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                        String status = redisUtil.getKey(key, String.class);
                        status = null != status ? status : Common.Driver.Status.OFFLINE;
                        statusMap.put(driver.getId(), status);
                    });

                    LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                    queryWrapper.eq(DriverAttribute::getName, name);
                    queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                    DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

                    Device device = new Device();
                    device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                    try {
                        device = deviceService.add(device);

                        // notify driver add device
                        notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                    } catch (DuplicateException duplicateException) {
                        device = deviceService.selectByName(deviceName, tenantId);
                    } catch (Exception ignored) {
                    }

                    // add profile bind
                    if (null != device.getId() && null != profile.getId()) {
                        try {
                            ProfileBind profileBind = new ProfileBind();
                            profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                            profileBindService.add(profileBind);
                        } catch (Exception ignored) {
                        }

                        // add point
                        Point point = new Point();
                        point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                        try {
                            point = pointService.add(point);

                            // notify driver add point
                            notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                        } catch (DuplicateException duplicateException) {
                            point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                        } catch (Exception ignored) {
                        }

                        return new PointDetail(device.getId(), point.getId());
                    }

                    if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                        try {
                            selectByNameAndProfileId(point.getName(), point.getProfileId());
                            throw new DuplicateException("The point already exists");
                        } catch (NotFoundException ignored) {
                        }
                    }
                    if (pointMapper.updateById(point) > 0) {
                        Point select = pointMapper.selectById(point.getId());
                        point.setName(select.getName()).setProfileId(select.getProfileId());
                        return select;
                    }


                    Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                    driverMetadata.setPointAttributeMap(pointAttributeMap);

                    List<Device> devices = deviceService.selectByDriverId(driver.getId());
                    Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                    Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                    driverMetadata.setDriverInfoMap(driverInfoMap);


                    Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                    driverMetadata.setPointAttributeMap(pointAttributeMap);

                    List<Device> devices = deviceService.selectByDriverId(driver.getId());
                    Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                    Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                    driverMetadata.setDriverInfoMap(driverInfoMap);


                    List<Dictionary> dictionaries = driverDictionary(tenantId);
                    dictionaries.forEach(driverDictionary -> {
                        List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                        LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                        queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                        List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                        pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                        driverDictionary.setDisabled(true);
                        driverDictionary.setValue(RandomUtil.randomLong());
                        driverDictionary.setChildren(driverAttributeDictionaryList);
                    });


                    //register driver attribute
                    Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                    if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                        driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                    }

                    Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                    try {
                        List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                        byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                    } catch (NotFoundException ignored) {
                    }

                    // add profile bind
                    if (null != device.getId() && null != profile.getId()) {
                        try {
                            ProfileBind profileBind = new ProfileBind();
                            profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                            profileBindService.add(profileBind);
                        } catch (Exception ignored) {
                        }

                        // add point
                        Point point = new Point();
                        point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                        try {
                            point = pointService.add(point);

                            // notify driver add point
                            notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                        } catch (DuplicateException duplicateException) {
                            point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                        } catch (Exception ignored) {
                        }

                        return new PointDetail(device.getId(), point.getId());
                    }

                    try {
                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);
                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);

                        Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                        driverMetadata.setProfilePointMap(profilePointMap);

                        Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                        driverMetadata.setPointInfoMap(devicePointInfoMap);

                        return driverMetadata;
                    } catch (NotFoundException ignored) {
                    }

                    // add point
                    Point point = new Point();
                    point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                    try {
                        point = pointService.add(point);

                        // notify driver add point
                        notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                    } catch (DuplicateException duplicateException) {
                        point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                    } catch (Exception ignored) {
                    }
                    try {
                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);
                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);

                        Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                        driverMetadata.setProfilePointMap(profilePointMap);

                        Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                        driverMetadata.setPointInfoMap(devicePointInfoMap);

                        return driverMetadata;
                    } catch (NotFoundException ignored) {
                    }

                    // add private profile for device
                    Profile profile = new Profile();
                    profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                    try {
                        profile = profileService.add(profile);
                    } catch (DuplicateException duplicateException) {
                        profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                    } catch (Exception ignored) {
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

                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);

                    List<Dictionary> dictionaries = new ArrayList<>(16);
                    LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                    queryWrapper.eq(Driver::getTenantId, tenantId);

                    // add device
                    Device device = new Device();
                    device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                    try {
                        device = deviceService.add(device);

                        // notify driver add device
                        notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                    } catch (DuplicateException duplicateException) {
                        device = deviceService.selectByName(deviceName, tenantId);
                    } catch (Exception ignored) {
                    }

                    // add private profile for device
                    Profile profile = new Profile();
                    profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                    try {
                        profile = profileService.add(profile);
                    } catch (DuplicateException duplicateException) {
                        profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                    } catch (Exception ignored) {
                    }

                    List<Dictionary> dictionaries = driverDictionary(tenantId);
                    dictionaries.forEach(driverDictionary -> {
                        List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                        LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                        queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                        List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                        pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                        driverDictionary.setDisabled(true);
                        driverDictionary.setValue(RandomUtil.randomLong());
                        driverDictionary.setChildren(driverAttributeDictionaryList);
                    });


                    // add private profile for device
                    Profile profile = new Profile();
                    profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                    try {
                        profile = profileService.add(profile);
                    } catch (DuplicateException duplicateException) {
                        profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                    } catch (Exception ignored) {
                    }


                    // add private profile for device
                    Profile profile = new Profile();
                    profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                    try {
                        profile = profileService.add(profile);
                    } catch (DuplicateException duplicateException) {
                        profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                    } catch (Exception ignored) {
                    }

                    // add profile bind
                    if (null != device.getId() && null != profile.getId()) {
                        try {
                            ProfileBind profileBind = new ProfileBind();
                            profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                            profileBindService.add(profileBind);
                        } catch (Exception ignored) {
                        }

                        // add point
                        Point point = new Point();
                        point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                        try {
                            point = pointService.add(point);

                            // notify driver add point
                            notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                        } catch (DuplicateException duplicateException) {
                            point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                        } catch (Exception ignored) {
                        }

                        return new PointDetail(device.getId(), point.getId());
                    }

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);


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

                        if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                            labelDto.setPage(new Pages());
                        }

                        Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                        driverMetadata.setProfilePointMap(profilePointMap);

                        LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                        queryWrapper.eq(DriverAttribute::getName, name);
                        queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                        DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);
                        if (null == driverAttribute) {
                            throw new NotFoundException("The driver attribute does not exist");
                        }

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);
                        LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
                        queryWrapper.eq(Profile::getName, name);
                        queryWrapper.eq(Profile::getType, type);
                        queryWrapper.eq(Profile::getTenantId, tenantId);
                        Profile profile = profileMapper.selectOne(queryWrapper);
                        if (null == profile) {
                            throw new NotFoundException("The profile does not exist");
                        }
                        try {
                            profile.setPointIds(pointService.selectByProfileId(profile.getId()).stream().map(Point::getId).collect(Collectors.toSet()));
                        } catch (NotFoundException ignored) {
                        }

                        profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                            String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                            String status = redisUtil.getKey(key, String.class);
                            status = null != status ? status : Common.Driver.Status.OFFLINE;
                            statusMap.put(id, status);
                        });

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);

                        Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                        driverMetadata.setProfilePointMap(profilePointMap);

                        Page<Driver> page = driverService.list(driverDto);
                        page.getRecords().forEach(driver -> {
                            String key = Common.Cache.DRIVER_STATUS_KEY_PREFIX + driver.getServiceName();
                            String status = redisUtil.getKey(key, String.class);
                            status = null != status ? status : Common.Driver.Status.OFFLINE;
                            statusMap.put(driver.getId(), status);
                        });

                        LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                        queryWrapper.eq(DriverAttribute::getName, name);
                        queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                        DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }

                        // add profile bind
                        if (null != device.getId() && null != profile.getId()) {
                            try {
                                ProfileBind profileBind = new ProfileBind();
                                profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                                profileBindService.add(profileBind);
                            } catch (Exception ignored) {
                            }

                            // add point
                            Point point = new Point();
                            point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                            try {
                                point = pointService.add(point);

                                // notify driver add point
                                notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                            } catch (DuplicateException duplicateException) {
                                point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                            } catch (Exception ignored) {
                            }

                            return new PointDetail(device.getId(), point.getId());
                        }

                        if (!old.getProfileId().equals(point.getProfileId()) || !old.getName().equals(point.getName())) {
                            try {
                                selectByNameAndProfileId(point.getName(), point.getProfileId());
                                throw new DuplicateException("The point already exists");
                            } catch (NotFoundException ignored) {
                            }
                        }
                        if (pointMapper.updateById(point) > 0) {
                            Point select = pointMapper.selectById(point.getId());
                            point.setName(select.getName()).setProfileId(select.getProfileId());
                            return select;
                        }


                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        List<Dictionary> dictionaries = driverDictionary(tenantId);
                        dictionaries.forEach(driverDictionary -> {
                            List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                            LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                            queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                            List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                            pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                            driverDictionary.setDisabled(true);
                            driverDictionary.setValue(RandomUtil.randomLong());
                            driverDictionary.setChildren(driverAttributeDictionaryList);
                        });


                        //register driver attribute
                        Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                        if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                            driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        }

                        Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                        try {
                            List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                            byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        } catch (NotFoundException ignored) {
                        }

                        // add profile bind
                        if (null != device.getId() && null != profile.getId()) {
                            try {
                                ProfileBind profileBind = new ProfileBind();
                                profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                                profileBindService.add(profileBind);
                            } catch (Exception ignored) {
                            }

                            // add point
                            Point point = new Point();
                            point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                            try {
                                point = pointService.add(point);

                                // notify driver add point
                                notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                            } catch (DuplicateException duplicateException) {
                                point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                            } catch (Exception ignored) {
                            }

                            return new PointDetail(device.getId(), point.getId());
                        }

                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add point
                        Point point = new Point();
                        point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                        try {
                            point = pointService.add(point);

                            // notify driver add point
                            notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                        } catch (DuplicateException duplicateException) {
                            point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                        } catch (Exception ignored) {
                        }
                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
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

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);

                        Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                        driverMetadata.setProfilePointMap(profilePointMap);

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        List<Dictionary> dictionaries = new ArrayList<>(16);
                        LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                        queryWrapper.eq(Driver::getTenantId, tenantId);

                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }

                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        List<Dictionary> dictionaries = driverDictionary(tenantId);
                        dictionaries.forEach(driverDictionary -> {
                            List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                            LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                            queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                            List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                            pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                            driverDictionary.setDisabled(true);
                            driverDictionary.setValue(RandomUtil.randomLong());
                            driverDictionary.setChildren(driverAttributeDictionaryList);
                        });


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        // add profile bind
                        if (null != device.getId() && null != profile.getId()) {
                            try {
                                ProfileBind profileBind = new ProfileBind();
                                profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                                profileBindService.add(profileBind);
                            } catch (Exception ignored) {
                            }
    }

                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }



                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);



                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }


                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        List<Dictionary> dictionaries = driverDictionary(tenantId);
                        dictionaries.forEach(driverDictionary -> {
                            List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                            LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                            queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                            List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                            pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                            driverDictionary.setDisabled(true);
                            driverDictionary.setValue(RandomUtil.randomLong());
                            driverDictionary.setChildren(driverAttributeDictionaryList);
                        });


                        //register driver attribute
                        Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                        if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                            driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        }

                        Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                        try {
                            List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                            byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        } catch (NotFoundException ignored) {
                        }

                        // add profile bind
                        if (null != device.getId() && null != profile.getId()) {
                            try {
                                ProfileBind profileBind = new ProfileBind();
                                profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                                profileBindService.add(profileBind);
                            } catch (Exception ignored) {
                            }

                            // add point
                            Point point = new Point();
                            point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                            try {
                                point = pointService.add(point);

                                // notify driver add point
                                notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                            } catch (DuplicateException duplicateException) {
                                point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                            } catch (Exception ignored) {
                            }

                            return new PointDetail(device.getId(), point.getId());
                        }

                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add point
                        Point point = new Point();
                        point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                        try {
                            point = pointService.add(point);

                            // notify driver add point
                            notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                        } catch (DuplicateException duplicateException) {
                            point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                        } catch (Exception ignored) {
                        }
                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
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


                        LambdaQueryWrapper<PointInfo> queryWrapper = Wrappers.<PointInfo>query().lambda();
                        if (null != pointInfoDto) {
                            if (null != pointInfoDto.getPointAttributeId()) {
                                queryWrapper.eq(PointInfo::getPointAttributeId, pointInfoDto.getPointAttributeId());
                            }
                            if (null != pointInfoDto.getDeviceId()) {
                                queryWrapper.eq(PointInfo::getDeviceId, pointInfoDto.getDeviceId());
                            }
                            if (null != pointInfoDto.getPointId()) {
                                queryWrapper.eq(PointInfo::getPointId, pointInfoDto.getPointId());
                            }
                        }

                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }



                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);



                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }


                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        List<Dictionary> dictionaries = driverDictionary(tenantId);
                        dictionaries.forEach(driverDictionary -> {
                            List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                            LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                            queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                            List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                            pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                            driverDictionary.setDisabled(true);
                            driverDictionary.setValue(RandomUtil.randomLong());
                            driverDictionary.setChildren(driverAttributeDictionaryList);
                        });


                        //register driver attribute
                        Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                        if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                            driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        }

                        Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                        try {
                            List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                            byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        } catch (NotFoundException ignored) {
                        }

                        // add profile bind
                        if (null != device.getId() && null != profile.getId()) {
                            try {
                                ProfileBind profileBind = new ProfileBind();
                                profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                                profileBindService.add(profileBind);
                            } catch (Exception ignored) {
                            }

                            // add point
                            Point point = new Point();
                            point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                            try {
                                point = pointService.add(point);

                                // notify driver add point
                                notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                            } catch (DuplicateException duplicateException) {
                                point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                            } catch (Exception ignored) {
                            }

                            return new PointDetail(device.getId(), point.getId());
                        }

                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add point
                        Point point = new Point();
                        point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                        try {
                            point = pointService.add(point);

                            // notify driver add point
                            notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                        } catch (DuplicateException duplicateException) {
                            point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                        } catch (Exception ignored) {
                        }
                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
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


                        LambdaQueryWrapper<PointInfo> queryWrapper = Wrappers.<PointInfo>query().lambda();
                        if (null != pointInfoDto) {
                            if (null != pointInfoDto.getPointAttributeId()) {
                                queryWrapper.eq(PointInfo::getPointAttributeId, pointInfoDto.getPointAttributeId());
                            }
                            if (null != pointInfoDto.getDeviceId()) {
                                queryWrapper.eq(PointInfo::getDeviceId, pointInfoDto.getDeviceId());
                            }
                            if (null != pointInfoDto.getPointId()) {
                                queryWrapper.eq(PointInfo::getPointId, pointInfoDto.getPointId());
                            }
                        }

                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }



                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);



                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }


                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        List<Dictionary> dictionaries = driverDictionary(tenantId);
                        dictionaries.forEach(driverDictionary -> {
                            List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                            LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                            queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                            List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                            pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                            driverDictionary.setDisabled(true);
                            driverDictionary.setValue(RandomUtil.randomLong());
                            driverDictionary.setChildren(driverAttributeDictionaryList);
                        });


                        //register driver attribute
                        Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                        if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                            driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        }

                        Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                        try {
                            List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                            byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        } catch (NotFoundException ignored) {
                        }

                        // add profile bind
                        if (null != device.getId() && null != profile.getId()) {
                            try {
                                ProfileBind profileBind = new ProfileBind();
                                profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                                profileBindService.add(profileBind);
                            } catch (Exception ignored) {
                            }

                            // add point
                            Point point = new Point();
                            point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                            try {
                                point = pointService.add(point);

                                // notify driver add point
                                notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                            } catch (DuplicateException duplicateException) {
                                point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                            } catch (Exception ignored) {
                            }

                            return new PointDetail(device.getId(), point.getId());
                        }

                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add point
                        Point point = new Point();
                        point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                        try {
                            point = pointService.add(point);

                            // notify driver add point
                            notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                        } catch (DuplicateException duplicateException) {
                            point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                        } catch (Exception ignored) {
                        }
                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
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


                        LambdaQueryWrapper<PointInfo> queryWrapper = Wrappers.<PointInfo>query().lambda();
                        if (null != pointInfoDto) {
                            if (null != pointInfoDto.getPointAttributeId()) {
                                queryWrapper.eq(PointInfo::getPointAttributeId, pointInfoDto.getPointAttributeId());
                            }
                            if (null != pointInfoDto.getDeviceId()) {
                                queryWrapper.eq(PointInfo::getDeviceId, pointInfoDto.getDeviceId());
                            }
                            if (null != pointInfoDto.getPointId()) {
                                queryWrapper.eq(PointInfo::getPointId, pointInfoDto.getPointId());
                            }
                        }

                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }



                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);



                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }


                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        List<Dictionary> dictionaries = driverDictionary(tenantId);
                        dictionaries.forEach(driverDictionary -> {
                            List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                            LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                            queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                            List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                            pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                            driverDictionary.setDisabled(true);
                            driverDictionary.setValue(RandomUtil.randomLong());
                            driverDictionary.setChildren(driverAttributeDictionaryList);
                        });


                        //register driver attribute
                        Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                        if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                            driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        }

                        Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                        try {
                            List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                            byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        } catch (NotFoundException ignored) {
                        }

                        // add profile bind
                        if (null != device.getId() && null != profile.getId()) {
                            try {
                                ProfileBind profileBind = new ProfileBind();
                                profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                                profileBindService.add(profileBind);
                            } catch (Exception ignored) {
                            }

                            // add point
                            Point point = new Point();
                            point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                            try {
                                point = pointService.add(point);

                                // notify driver add point
                                notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                            } catch (DuplicateException duplicateException) {
                                point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                            } catch (Exception ignored) {
                            }

                            return new PointDetail(device.getId(), point.getId());
                        }

                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add point
                        Point point = new Point();
                        point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                        try {
                            point = pointService.add(point);

                            // notify driver add point
                            notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                        } catch (DuplicateException duplicateException) {
                            point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                        } catch (Exception ignored) {
                        }
                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
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


                        LambdaQueryWrapper<PointInfo> queryWrapper = Wrappers.<PointInfo>query().lambda();
                        if (null != pointInfoDto) {
                            if (null != pointInfoDto.getPointAttributeId()) {
                                queryWrapper.eq(PointInfo::getPointAttributeId, pointInfoDto.getPointAttributeId());
                            }
                            if (null != pointInfoDto.getDeviceId()) {
                                queryWrapper.eq(PointInfo::getDeviceId, pointInfoDto.getDeviceId());
                            }
                            if (null != pointInfoDto.getPointId()) {
                                queryWrapper.eq(PointInfo::getPointId, pointInfoDto.getPointId());
                            }
                        }

                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }



                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);



                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }


                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        List<Dictionary> dictionaries = driverDictionary(tenantId);
                        dictionaries.forEach(driverDictionary -> {
                            List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                            LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                            queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                            List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                            pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                            driverDictionary.setDisabled(true);
                            driverDictionary.setValue(RandomUtil.randomLong());
                            driverDictionary.setChildren(driverAttributeDictionaryList);
                        });


                        //register driver attribute
                        Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                        if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                            driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        }

                        Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                        try {
                            List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                            byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        } catch (NotFoundException ignored) {
                        }

                        // add profile bind
                        if (null != device.getId() && null != profile.getId()) {
                            try {
                                ProfileBind profileBind = new ProfileBind();
                                profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                                profileBindService.add(profileBind);
                            } catch (Exception ignored) {
                            }

                            // add point
                            Point point = new Point();
                            point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                            try {
                                point = pointService.add(point);

                                // notify driver add point
                                notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                            } catch (DuplicateException duplicateException) {
                                point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                            } catch (Exception ignored) {
                            }

                            return new PointDetail(device.getId(), point.getId());
                        }

                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add point
                        Point point = new Point();
                        point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                        try {
                            point = pointService.add(point);

                            // notify driver add point
                            notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                        } catch (DuplicateException duplicateException) {
                            point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                        } catch (Exception ignored) {
                        }
                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
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


                        // add point
                        Point point = new Point();
                        point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                        try {
                            point = pointService.add(point);

                            // notify driver add point
                            notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                        } catch (DuplicateException duplicateException) {
                            point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                        } catch (Exception ignored) {
                        }
                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
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


                        LambdaQueryWrapper<PointInfo> queryWrapper = Wrappers.<PointInfo>query().lambda();
                        if (null != pointInfoDto) {
                            if (null != pointInfoDto.getPointAttributeId()) {
                                queryWrapper.eq(PointInfo::getPointAttributeId, pointInfoDto.getPointAttributeId());
                            }
                            if (null != pointInfoDto.getDeviceId()) {
                                queryWrapper.eq(PointInfo::getDeviceId, pointInfoDto.getDeviceId());
                            }
                            if (null != pointInfoDto.getPointId()) {
                                queryWrapper.eq(PointInfo::getPointId, pointInfoDto.getPointId());
                            }
                        }

                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }



                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);



                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }


                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        List<Dictionary> dictionaries = driverDictionary(tenantId);
                        dictionaries.forEach(driverDictionary -> {
                            List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                            LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                            queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                            List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                            pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                            driverDictionary.setDisabled(true);
                            driverDictionary.setValue(RandomUtil.randomLong());
                            driverDictionary.setChildren(driverAttributeDictionaryList);
                        });


                        //register driver attribute
                        Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                        if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                            driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        }

                        Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                        try {
                            List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                            byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        } catch (NotFoundException ignored) {
                        }

                        // add profile bind
                        if (null != device.getId() && null != profile.getId()) {
                            try {
                                ProfileBind profileBind = new ProfileBind();
                                profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                                profileBindService.add(profileBind);
                            } catch (Exception ignored) {
                            }

                            // add point
                            Point point = new Point();
                            point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                            try {
                                point = pointService.add(point);

                                // notify driver add point
                                notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                            } catch (DuplicateException duplicateException) {
                                point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                            } catch (Exception ignored) {
                            }

                            return new PointDetail(device.getId(), point.getId());
                        }

                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add point
                        Point point = new Point();
                        point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                        try {
                            point = pointService.add(point);

                            // notify driver add point
                            notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                        } catch (DuplicateException duplicateException) {
                            point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                        } catch (Exception ignored) {
                        }
                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
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


                        LambdaQueryWrapper<PointInfo> queryWrapper = Wrappers.<PointInfo>query().lambda();
                        if (null != pointInfoDto) {
                            if (null != pointInfoDto.getPointAttributeId()) {
                                queryWrapper.eq(PointInfo::getPointAttributeId, pointInfoDto.getPointAttributeId());
                            }
                            if (null != pointInfoDto.getDeviceId()) {
                                queryWrapper.eq(PointInfo::getDeviceId, pointInfoDto.getDeviceId());
                            }
                            if (null != pointInfoDto.getPointId()) {
                                queryWrapper.eq(PointInfo::getPointId, pointInfoDto.getPointId());
                            }
                        }

                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }



                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);



                        // add device
                        Device device = new Device();
                        device.setName(deviceName).setDriverId(driverId).setTenantId(tenantId).setDescription("auto create by driver");
                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }


                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                        queryWrapper.eq(Label::getName, name);
                        queryWrapper.eq(Label::getTenantId, tenantId);
                        Label label = labelMapper.selectOne(queryWrapper);
                        if (null == label) {
                            throw new NotFoundException("The label does not exist");
                        }


                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }

                        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                        driverMetadata.setDriverAttributeMap(driverAttributeMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);

                        Map<Long, Device> deviceMap = getDeviceMap(devices);
                        driverMetadata.setDeviceMap(deviceMap);

                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                        driverMetadata.setPointAttributeMap(pointAttributeMap);

                        List<Device> devices = deviceService.selectByDriverId(driver.getId());
                        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                        driverMetadata.setDriverInfoMap(driverInfoMap);


                        List<Dictionary> dictionaries = driverDictionary(tenantId);
                        dictionaries.forEach(driverDictionary -> {
                            List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
                            LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
                            queryWrapper.eq(PointAttribute::getDriverId, driverDictionary.getValue());
                            List<PointAttribute> pointAttributeList = pointAttributeMapper.selectList(queryWrapper);
                            pointAttributeList.forEach(pointAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(pointAttribute.getDisplayName()).setValue(pointAttribute.getId())));

                            driverDictionary.setDisabled(true);
                            driverDictionary.setValue(RandomUtil.randomLong());
                            driverDictionary.setChildren(driverAttributeDictionaryList);
                        });


                        //register driver attribute
                        Map<String, DriverAttribute> newDriverAttributeMap = new HashMap<>(8);
                        if (null != driverRegister.getDriverAttributes() && driverRegister.getDriverAttributes().size() > 0) {
                            driverRegister.getDriverAttributes().forEach(driverAttribute -> newDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        }

                        Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                        try {
                            List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                            byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                        } catch (NotFoundException ignored) {
                        }

                        // add profile bind
                        if (null != device.getId() && null != profile.getId()) {
                            try {
                                ProfileBind profileBind = new ProfileBind();
                                profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                                profileBindService.add(profileBind);
                            } catch (Exception ignored) {
                            }

                            // add point
                            Point point = new Point();
                            point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                            try {
                                point = pointService.add(point);

                                // notify driver add point
                                notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                            } catch (DuplicateException duplicateException) {
                                point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                            } catch (Exception ignored) {
                            }

                            return new PointDetail(device.getId(), point.getId());
                        }

                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add point
                        Point point = new Point();
                        point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                        try {
                            point = pointService.add(point);

                            // notify driver add point
                            notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                        } catch (DuplicateException duplicateException) {
                            point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                        } catch (Exception ignored) {
                        }
                        try {
                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);
                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, Map<Long, Map<String, AttributeInfo>>> devicePointInfoMap = getPointInfoMap(devices, profilePointMap, pointAttributeMap);
                            driverMetadata.setPointInfoMap(devicePointInfoMap);

                            return driverMetadata;
                        } catch (NotFoundException ignored) {
                        }

                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
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

                            if (!Optional.ofNullable(labelDto.getPage()).isPresent()) {
                                labelDto.setPage(new Pages());
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


                            LambdaQueryWrapper<PointInfo> queryWrapper = Wrappers.<PointInfo>query().lambda();
                            if (null != pointInfoDto) {
                                if (null != pointInfoDto.getPointAttributeId()) {
                                    queryWrapper.eq(PointInfo::getPointAttributeId, pointInfoDto.getPointAttributeId());
                                }
                                if (null != pointInfoDto.getDeviceId()) {
                                    queryWrapper.eq(PointInfo::getDeviceId, pointInfoDto.getDeviceId());
                                }
                                if (null != pointInfoDto.getPointId()) {
                                    queryWrapper.eq(PointInfo::getPointId, pointInfoDto.getPointId());
                                }
                            }

                            // add point
                            Point point = new Point();
                            point.setName(pointName).setProfileId(profile.getId()).setTenantId(tenantId).setDefault();
                            try {
                                point = pointService.add(point);

                                // notify driver add point
                                notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point);
                            } catch (DuplicateException duplicateException) {
                                point = pointService.selectByNameAndProfileId(pointName, profile.getId());
                            } catch (Exception ignored) {
                            }

                            return new PointDetail(device.getId(), point.getId());
                        }

                        Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                        driverMetadata.setProfilePointMap(profilePointMap);

                        LambdaQueryWrapper<PointInfo> queryWrapper = Wrappers.<PointInfo>query().lambda();
                        if (null != pointInfoDto) {
                            if (null != pointInfoDto.getPointAttributeId()) {
                                queryWrapper.eq(PointInfo::getPointAttributeId, pointInfoDto.getPointAttributeId());
                            }
                            if (null != pointInfoDto.getDeviceId()) {
                                queryWrapper.eq(PointInfo::getDeviceId, pointInfoDto.getDeviceId());
                            }
                            if (null != pointInfoDto.getPointId()) {
                                queryWrapper.eq(PointInfo::getPointId, pointInfoDto.getPointId());
                            }
                        }

    @Override
    @Cacheable(value = Common.Cache.PROFILE + Common.Cache.LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<Profile> list(ProfileDto profileDto) {
        if (!Optional.ofNullable(profileDto.getPage()).isPresent()) {
            profileDto.setPage(new Pages());
        }

        return page;
    }

    @Override
    public LambdaQueryWrapper<Profile> fuzzyQuery(ProfileDto profileDto) {

        return queryWrapper;
    }

}
