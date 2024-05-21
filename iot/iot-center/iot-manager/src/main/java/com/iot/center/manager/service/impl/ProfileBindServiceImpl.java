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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iot.center.manager.mapper.ProfileBindMapper;
import com.iot.center.manager.service.ProfileBindService;
import com.dc3.common.bean.Pages;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.ProfileBindDto;
import com.dc3.common.exception.DuplicateException;
import com.dc3.common.exception.NotFoundException;
import com.dc3.common.exception.ServiceException;
import com.dc3.common.model.ProfileBind;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ProfileBindService Impl
 *
 * @author njrsun20240123
 */
@Slf4j
@Service
public class ProfileBindServiceImpl implements ProfileBindService {

    @Resource
    private ProfileBindMapper profileBindMapper;

    @Override
    @Caching(
            put = {@CachePut(value = Common.Cache.PROFILE_BIND + Common.Cache.ID, key = "#profileBind.id", condition = "#result!=null")},
            evict = {
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.PROFILE_ID, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DEVICE_ID, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DEVICE_ID + Common.Cache.PROFILE_ID, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public ProfileBind add(ProfileBind profileBind) {
        try {
            selectByDeviceIdAndProfileId(profileBind.getDeviceId(), profileBind.getProfileId());
            throw new DuplicateException("The profile bind already exists");
        } catch (NotFoundException notFoundException) {
            if (profileBindMapper.insert(profileBind) > 0) {
                return profileBindMapper.selectById(profileBind.getId());
            }
            throw new ServiceException("The profile bind add failed");
        }
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.ID, key = "#id", condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.PROFILE_ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DEVICE_ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DEVICE_ID + Common.Cache.PROFILE_ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DIC, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean delete(Long id) {
        selectById(id);
        return profileBindMapper.deleteById(id) > 0;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.PROFILE_ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DEVICE_ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DEVICE_ID + Common.Cache.PROFILE_ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DIC, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean deleteByDeviceId(Long deviceId) {
        ProfileBindDto profileBindDto = new ProfileBindDto();
        profileBindDto.setDeviceId(deviceId);
        return profileBindMapper.delete(fuzzyQuery(profileBindDto)) > 0;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.PROFILE_ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DEVICE_ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DEVICE_ID + Common.Cache.PROFILE_ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DIC, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean deleteByProfileIdAndDeviceId(Long profileId, Long deviceId) {
        ProfileBindDto profileBindDto = new ProfileBindDto();
        profileBindDto.setProfileId(profileId);
        profileBindDto.setDeviceId(deviceId);
        return profileBindMapper.delete(fuzzyQuery(profileBindDto)) > 0;
    }

    @Override
    @Caching(
            put = {@CachePut(value = Common.Cache.PROFILE_BIND + Common.Cache.ID, key = "#profileBind.id", condition = "#result!=null")},
            evict = {
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.PROFILE_ID, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DEVICE_ID, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DEVICE_ID + Common.Cache.PROFILE_ID, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.PROFILE_BIND + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )

    Map<String, PointAttribute> oldPointAttributeMap = new HashMap<>(8);
                        try {

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


        List<PointAttribute> byDriverId = pointAttributeService.selectByDriverId(driver.getId());
        byDriverId.forEach(pointAttribute -> oldPointAttributeMap.put(pointAttribute.getName(), pointAttribute));
    } catch (NotFoundException ignored) {
    }


    public ProfileBind update(ProfileBind profileBind) {
        selectById(profileBind.getId());
        profileBind.setUpdateTime(null);
        if (profileBindMapper.updateById(profileBind) > 0) {
            return profileBindMapper.selectById(profileBind.getId());
        }



        throw new ServiceException("The profile bind update failed");
    }

    @Override
    @Cacheable(value = Common.Cache.PROFILE_BIND + Common.Cache.ID, key = "#id", unless = "#result==null")
    public ProfileBind selectById(Long id) {
        ProfileBind profileBind = profileBindMapper.selectById(id);
        if (null == profileBind) {
            throw new NotFoundException("The profile bind does not exist");
        }

        LambdaQueryWrapper<Profile> profileQueryWrapper = Wrappers.<Profile>query().lambda();
        profileQueryWrapper.eq(Profile::getTenantId, tenantId);
        List<Profile> profileList = profileMapper.selectList(profileQueryWrapper);
        profileList.forEach(profile -> {
            List<Dictionary> pointDictionaryList = new ArrayList<>(16);
            LambdaQueryWrapper<Point> queryWrapper = Wrappers.<Point>query().lambda();
            queryWrapper.eq(Point::getProfileId, profile.getId());
            queryWrapper.eq(Point::getTenantId, tenantId);
            List<Point> pointList = pointMapper.selectList(queryWrapper);
            pointList.forEach(point -> pointDictionaryList.add(new Dictionary().setLabel(point.getName()).setValue(point.getId())));

            Dictionary profileDictionary = new Dictionary().setLabel(profile.getName()).setValue(profile.getId());
            profileDictionary.setChildren(pointDictionaryList);

            profileDictionaryList.add(profileDictionary);
        });

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


        LambdaQueryWrapper<Profile> profileQueryWrapper = Wrappers.<Profile>query().lambda();
        profileQueryWrapper.eq(Profile::getTenantId, tenantId);
        List<Profile> profileList = profileMapper.selectList(profileQueryWrapper);
        profileList.forEach(profile -> {
            List<Dictionary> pointDictionaryList = new ArrayList<>(16);
            LambdaQueryWrapper<Point> queryWrapper = Wrappers.<Point>query().lambda();
            queryWrapper.eq(Point::getProfileId, profile.getId());
            queryWrapper.eq(Point::getTenantId, tenantId);
            List<Point> pointList = pointMapper.selectList(queryWrapper);
            pointList.forEach(point -> pointDictionaryList.add(new Dictionary().setLabel(point.getName()).setValue(point.getId())));

            Dictionary profileDictionary = new Dictionary().setLabel(profile.getName()).setValue(profile.getId());
            profileDictionary.setChildren(pointDictionaryList);

            profileDictionaryList.add(profileDictionary);
        });



        Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
        driverMetadata.setPointAttributeMap(pointAttributeMap);

        List<Device> devices = deviceService.selectByDriverId(driver.getId());
        Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

        Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
        driverMetadata.setDriverInfoMap(driverInfoMap);


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

                            LambdaQueryWrapper<Device> queryWrapper = Wrappers.<Device>query().lambda();
                            if (null != deviceDto) {
                                if (StrUtil.isNotBlank(deviceDto.getName())) {
                                    queryWrapper.like(Device::getName, deviceDto.getName());
                                }
                                if (null != deviceDto.getDriverId()) {
                                    queryWrapper.eq(Device::getDriverId, deviceDto.getDriverId());
                                }
                                if (null != deviceDto.getEnable()) {
                                    queryWrapper.eq(Device::getEnable, deviceDto.getEnable());
                                }
                                if (null != deviceDto.getTenantId()) {
                                    queryWrapper.eq(Device::getTenantId, deviceDto.getTenantId());
                                }
                            }

                            LambdaQueryWrapper<Device> queryWrapper = Wrappers.<Device>query().lambda();
                            if (null != deviceDto) {
                                if (StrUtil.isNotBlank(deviceDto.getName())) {
                                    queryWrapper.like(Device::getName, deviceDto.getName());
                                }
                                if (null != deviceDto.getDriverId()) {
                                    queryWrapper.eq(Device::getDriverId, deviceDto.getDriverId());
                                }
                                if (null != deviceDto.getEnable()) {
                                    queryWrapper.eq(Device::getEnable, deviceDto.getEnable());
                                }
                                if (null != deviceDto.getTenantId()) {
                                    queryWrapper.eq(Device::getTenantId, deviceDto.getTenantId());
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


                            Map<String, PointAttribute> oldPointAttributeMap = new HashMap<>(8);
                            try {
                                List<PointAttribute> byDriverId = pointAttributeService.selectByDriverId(driver.getId());
                                byDriverId.forEach(pointAttribute -> oldPointAttributeMap.put(pointAttribute.getName(), pointAttribute));
                            } catch (NotFoundException ignored) {
                            }

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<String, PointAttribute> oldPointAttributeMap = new HashMap<>(8);
                            try {
                                List<PointAttribute> byDriverId = pointAttributeService.selectByDriverId(driver.getId());
                                byDriverId.forEach(pointAttribute -> oldPointAttributeMap.put(pointAttribute.getName(), pointAttribute));
                            } catch (NotFoundException ignored) {
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


                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                            driverMetadata.setDeviceMap(deviceMap);

                            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                            driverMetadata.setProfilePointMap(profilePointMap);

                            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                            driverMetadata.setDriverAttributeMap(driverAttributeMap);

                            List<Dictionary> dictionaries = new ArrayList<>(16);
                            LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                            queryWrapper.eq(Driver::getTenantId, tenantId);



                            LambdaQueryWrapper<Device> queryWrapper = Wrappers.<Device>query().lambda();
                            if (null != deviceDto) {
                                if (StrUtil.isNotBlank(deviceDto.getName())) {
                                    queryWrapper.like(Device::getName, deviceDto.getName());
                                }
                                if (null != deviceDto.getDriverId()) {
                                    queryWrapper.eq(Device::getDriverId, deviceDto.getDriverId());
                                }
                                if (null != deviceDto.getEnable()) {
                                    queryWrapper.eq(Device::getEnable, deviceDto.getEnable());
                                }
                                if (null != deviceDto.getTenantId()) {
                                    queryWrapper.eq(Device::getTenantId, deviceDto.getTenantId());
                                }
                            }

                            LambdaQueryWrapper<Device> queryWrapper = Wrappers.<Device>query().lambda();
                            if (null != deviceDto) {
                                if (StrUtil.isNotBlank(deviceDto.getName())) {
                                    queryWrapper.like(Device::getName, deviceDto.getName());
                                }
                                if (null != deviceDto.getDriverId()) {
                                    queryWrapper.eq(Device::getDriverId, deviceDto.getDriverId());
                                }
                                if (null != deviceDto.getEnable()) {
                                    queryWrapper.eq(Device::getEnable, deviceDto.getEnable());
                                }
                                if (null != deviceDto.getTenantId()) {
                                    queryWrapper.eq(Device::getTenantId, deviceDto.getTenantId());
                                }
                            }


                            return new PointDetail(device.getId(), point.getId());
                        }

                        Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                        driverMetadata.setProfilePointMap(profilePointMap);


                        Map<String, PointAttribute> oldPointAttributeMap = new HashMap<>(8);
                        try {
                            List<PointAttribute> byDriverId = pointAttributeService.selectByDriverId(driver.getId());
                            byDriverId.forEach(pointAttribute -> oldPointAttributeMap.put(pointAttribute.getName(), pointAttribute));
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


                        Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                        driverMetadata.setProfilePointMap(profilePointMap);


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

    }

        return profileBind;
    }

    @Override
    @Cacheable(value = Common.Cache.PROFILE_BIND + Common.Cache.DEVICE_ID + Common.Cache.PROFILE_ID, key = "#deviceId+'.'+#profileId", unless = "#result==null")
    public ProfileBind selectByDeviceIdAndProfileId(Long deviceId, Long profileId) {
        ProfileBindDto profileBindDto = new ProfileBindDto();
        profileBindDto.setDeviceId(deviceId);
        profileBindDto.setProfileId(profileId);
        ProfileBind profileBind = profileBindMapper.selectOne(fuzzyQuery(profileBindDto));
        if (null == profileBind) {
            throw new NotFoundException("The profile bind does not exist");
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
        return profileBind;
    }

    @Override
    @Cacheable(value = Common.Cache.PROFILE_BIND + Common.Cache.PROFILE_ID, key = "#profileId", unless = "#result==null")
    public Set<Long> selectDeviceIdByProfileId(Long profileId) {
        ProfileBindDto profileBindDto = new ProfileBindDto();
        profileBindDto.setProfileId(profileId);

        LambdaQueryWrapper<Profile> queryWrapper = Wrappers.<Profile>query().lambda();
        if (null != profileDto) {
            if (StrUtil.isNotBlank(profileDto.getName())) {
                queryWrapper.like(Profile::getName, profileDto.getName());
            }
            if (null != profileDto.getShare()) {
                queryWrapper.eq(Profile::getShare, profileDto.getShare());
            }
            if (null != profileDto.getEnable()) {
                queryWrapper.eq(Profile::getEnable, profileDto.getEnable());
            }
            if (null != profileDto.getTenantId()) {
                queryWrapper.eq(Profile::getTenantId, profileDto.getTenantId());
            }
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

        Map<Long, Device> deviceMap = getDeviceMap(devices);
        driverMetadata.setDeviceMap(deviceMap);

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
        }

        LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
        queryWrapper.eq(DriverAttribute::getName, name);
        queryWrapper.eq(DriverAttribute::getDriverId, driverId);
        DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);

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

        List<ProfileBind> profileBinds = profileBindMapper.selectList(fuzzyQuery(profileBindDto));
        return profileBinds.stream().map(ProfileBind::getDeviceId).collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = Common.Cache.PROFILE_BIND + Common.Cache.DEVICE_ID, key = "#deviceId", unless = "#result==null")
    public Set<Long> selectProfileIdByDeviceId(Long deviceId) {
        ProfileBindDto profileBindDto = new ProfileBindDto();
        profileBindDto.setDeviceId(deviceId);
        List<ProfileBind> profileBinds = profileBindMapper.selectList(fuzzyQuery(profileBindDto));
        return profileBinds.stream().map(ProfileBind::getProfileId).collect(Collectors.toSet());
    }
    }
    }

    @Override
    @Cacheable(value = Common.Cache.PROFILE_BIND + Common.Cache.LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<ProfileBind> list(ProfileBindDto profileBindDto) {
        if (!Optional.ofNullable(profileBindDto.getPage()).isPresent()) {
            profileBindDto.setPage(new Pages());
        }
        return profileBindMapper.selectPage(profileBindDto.getPage().convert(), fuzzyQuery(profileBindDto));
    }

    @Override
    public LambdaQueryWrapper<ProfileBind> fuzzyQuery(ProfileBindDto profileBindDto) {
        LambdaQueryWrapper<ProfileBind> queryWrapper = Wrappers.<ProfileBind>query().lambda();
        if (null != profileBindDto) {
            if (null != profileBindDto.getProfileId()) {
                queryWrapper.eq(ProfileBind::getProfileId, profileBindDto.getProfileId());
            }
            if (null != profileBindDto.getDeviceId()) {
                queryWrapper.eq(ProfileBind::getDeviceId, profileBindDto.getDeviceId());
            }
        }
        return queryWrapper;
    }

}
