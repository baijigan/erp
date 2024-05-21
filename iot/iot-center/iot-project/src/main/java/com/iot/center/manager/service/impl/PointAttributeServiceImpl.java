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
import com.iot.center.manager.mapper.PointAttributeMapper;
import com.iot.center.manager.service.PointAttributeService;
import com.dc3.common.bean.Pages;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.PointAttributeDto;
import com.dc3.common.exception.DuplicateException;
import com.dc3.common.exception.NotFoundException;
import com.dc3.common.exception.ServiceException;
import com.dc3.common.model.PointAttribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * PointAttributeService Impl
 *
 * @author njrsun20240123
 */
@Slf4j
@Service
public class PointAttributeServiceImpl implements PointAttributeService {
    @Resource
    private PointAttributeMapper pointAttributeMapper;

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.ID, key = "#pointAttribute.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.NAME + Common.Cache.DRIVER_ID, key = "#pointAttribute.name+'.'+#pointAttribute.driverId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.DRIVER_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public PointAttribute add(PointAttribute pointAttribute) {
        try {
            selectByNameAndDriverId(pointAttribute.getName(), pointAttribute.getDriverId());
            throw new DuplicateException("The point attribute already exists");
        } catch (NotFoundException notFoundException) {
            if (pointAttributeMapper.insert(pointAttribute) > 0) {
                return pointAttributeMapper.selectById(pointAttribute.getId());
            }
            throw new ServiceException("The point attribute add failed");
        }
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.ID, key = "#id", condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.NAME + Common.Cache.DRIVER_ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.DIC, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.DRIVER_ID + Common.Cache.LIST, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean delete(Long id) {
        selectById(id);
        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
        driverMetadata.setDriverAttributeMap(driverAttributeMap);


        // add profile bind
        if (null != device.getId() && null != profile.getId()) {
            try {
                ProfileBind profileBind = new ProfileBind();
                profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                profileBindService.add(profileBind);
            } catch (Exception ignored) {
            }

            DriverInfoDto driverInfoDto = new DriverInfoDto();
            driverInfoDto.setDriverAttributeId(driverAttributeId);
            driverInfoDto.setDeviceId(deviceId);
            DriverInfo driverInfo = driverInfoMapper.selectOne(fuzzyQuery(driverInfoDto));
            if (null == driverInfo) {
                throw new NotFoundException("The driver info does not exist");
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


                // add private profile for device
                Profile profile = new Profile();
                profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                try {
                    profile = profileService.add(profile);
                } catch (DuplicateException duplicateException) {
                    profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                } catch (Exception ignored) {
                }


                // register point attribute
                Map<String, PointAttribute> newPointAttributeMap = new HashMap<>(8);
                if (null != driverRegister.getPointAttributes() && driverRegister.getPointAttributes().size() > 0) {
                    driverRegister.getPointAttributes().forEach(pointAttribute -> newPointAttributeMap.put(pointAttribute.getName(), pointAttribute));
                }

                Map<String, PointAttribute> oldPointAttributeMap = new HashMap<>(8);
                try {
                    List<PointAttribute> byDriverId = pointAttributeService.selectByDriverId(driver.getId());
                    byDriverId.forEach(pointAttribute -> oldPointAttributeMap.put(pointAttribute.getName(), pointAttribute));
                } catch (NotFoundException ignored) {
                }

                if (pointInfoMapper.updateById(pointInfo) > 0) {
                    PointInfo select = pointInfoMapper.selectById(pointInfo.getId());
                    pointInfo.setPointAttributeId(select.getPointAttributeId()).setDeviceId(select.getDeviceId()).setPointId(select.getPointId());
                    return select;
                }

                Set<Long> newProfileIds = null != device.getProfileIds() ? device.getProfileIds() : new HashSet<>();
                Set<Long> oldProfileIds = profileBindService.selectProfileIdByDeviceId(device.getId());

                Set<Long> add = new HashSet<>(newProfileIds);
                add.removeAll(oldProfileIds);

                Set<Long> delete = new HashSet<>(oldProfileIds);
                delete.removeAll(newProfileIds);

                addProfileBind(device.getId(), add);
                delete.forEach(profileId -> profileBindService.deleteByProfileIdAndDeviceId(profileId, device.getId()));

                device.setUpdateTime(null);
                if (deviceMapper.updateById(device) > 0) {
                    Device select = deviceMapper.selectById(device.getId());
                    select.setProfileIds(newProfileIds);
                    device.setName(select.getName());
                    return select;
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

                    DriverInfoDto driverInfoDto = new DriverInfoDto();
                    driverInfoDto.setDriverAttributeId(driverAttributeId);
                    driverInfoDto.setDeviceId(deviceId);
                    DriverInfo driverInfo = driverInfoMapper.selectOne(fuzzyQuery(driverInfoDto));
                    if (null == driverInfo) {
                        throw new NotFoundException("The driver info does not exist");
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



                    return new PointDetail(device.getId(), point.getId());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                if (labelBindMapper.updateById(labelBind) > 0) {
                    return labelBindMapper.selectById(labelBind.getId());
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

                        // add private profile for device
                        Profile profile = new Profile();
                        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
                        try {
                            profile = profileService.add(profile);
                        } catch (DuplicateException duplicateException) {
                            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
                        } catch (Exception ignored) {
                        }


                        // register point attribute
                        Map<String, PointAttribute> newPointAttributeMap = new HashMap<>(8);
                        if (null != driverRegister.getPointAttributes() && driverRegister.getPointAttributes().size() > 0) {
                            driverRegister.getPointAttributes().forEach(pointAttribute -> newPointAttributeMap.put(pointAttribute.getName(), pointAttribute));
                        }

                        Map<String, PointAttribute> oldPointAttributeMap = new HashMap<>(8);
                        try {
                            List<PointAttribute> byDriverId = pointAttributeService.selectByDriverId(driver.getId());
                            byDriverId.forEach(pointAttribute -> oldPointAttributeMap.put(pointAttribute.getName(), pointAttribute));
                        } catch (NotFoundException ignored) {
                        }

                        try {
                            device = deviceService.add(device);

                            // notify driver add device
                            notifyService.notifyDriverDevice(Common.Driver.Device.ADD, device);
                        } catch (DuplicateException duplicateException) {
                            device = deviceService.selectByName(deviceName, tenantId);
                        } catch (Exception ignored) {
                        }


                        profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
                            String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
                            String status = redisUtil.getKey(key, String.class);
                            status = null != status ? status : Common.Driver.Status.OFFLINE;
                            statusMap.put(id, status);
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

                        LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                        queryWrapper.eq(DriverAttribute::getName, name);
                        queryWrapper.eq(DriverAttribute::getDriverId, driverId);
                        DriverAttribute driverAttribute = driverAttributeMapper.selectOne(queryWrapper);



                        if (null != labelBindDto) {
                if (null != labelBindDto.getLabelId()) {
                    queryWrapper.eq(LabelBind::getLabelId, labelBindDto.getLabelId());
                }
                if (null != labelBindDto.getEntityId()) {
                    queryWrapper.eq(LabelBind::getEntityId, labelBindDto.getEntityId());
                }
                if (StrUtil.isNotBlank(labelBindDto.getType())) {
                    queryWrapper.eq(LabelBind::getType, labelBindDto.getType());
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



            return new PointDetail(device.getId(), point.getId());
        }

        Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
        driverMetadata.setProfilePointMap(profilePointMap);

        if (labelBindMapper.updateById(labelBind) > 0) {
            return labelBindMapper.selectById(labelBind.getId());
        }


    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.ID, key = "#pointAttribute.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.NAME + Common.Cache.DRIVER_ID, key = "#pointAttribute.name+'.'+#pointAttribute.driverId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.DRIVER_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public PointAttribute update(PointAttribute pointAttribute) {
        selectById(pointAttribute.getId());


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


                        driverAttribute.setUpdateTime(null);
                        if (driverAttributeMapper.updateById(driverAttribute) > 0) {
                            DriverAttribute select = driverAttributeMapper.selectById(driverAttribute.getId());
                            driverAttribute.setName(select.getName()).setDriverId(select.getDriverId());
                            return select;
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

                                            Map<String, PointAttribute> oldPointAttributeMap = new HashMap<>(8);
                                            try {
                                                List<PointAttribute> byDriverId = pointAttributeService.selectByDriverId(driver.getId());
                                                byDriverId.forEach(pointAttribute -> oldPointAttributeMap.put(pointAttribute.getName(), pointAttribute));
                                            } catch (NotFoundException ignored) {
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

                                            LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
                                            queryWrapper.eq(Label::getName, name);
                                            queryWrapper.eq(Label::getTenantId, tenantId);
                                            Label label = labelMapper.selectOne(queryWrapper);
                                            if (null == label) {
                                                throw new NotFoundException("The label does not exist");
                                            }

                                            Map<Long, Device> deviceMap = getDeviceMap(devices);
                                            driverMetadata.setDeviceMap(deviceMap);


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


                                                Map<Long, Device> deviceMap = getDeviceMap(devices);
                                                driverMetadata.setDeviceMap(deviceMap);

                                                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                                                driverMetadata.setProfilePointMap(profilePointMap);

                                                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                                                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                                                List<Dictionary> dictionaries = new ArrayList<>(16);
                                                LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                                                queryWrapper.eq(Driver::getTenantId, tenantId);


                                                
                                                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                                                driverMetadata.setProfilePointMap(profilePointMap);


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


                                                // add profile bind
                                                if (null != device.getId() && null != profile.getId()) {
                                                    try {
                                                        ProfileBind profileBind = new ProfileBind();
                                                        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                                                        profileBindService.add(profileBind);
                                                    } catch (Exception ignored) {
                                                    }


        pointAttribute.setUpdateTime(null);
        if (pointAttributeMapper.updateById(pointAttribute) > 0) {
            PointAttribute select = pointAttributeMapper.selectById(pointAttribute.getId());
            pointAttribute.setName(select.getName()).setDriverId(select.getDriverId());
            return select;
        }
        throw new ServiceException("The point attribute update failed");
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.ID, key = "#id", unless = "#result==null")
    public PointAttribute selectById(Long id) {
        PointAttribute pointAttribute = pointAttributeMapper.selectById(id);
        if (null == pointAttribute) {
            throw new NotFoundException("The point attribute does not exist");
        }
        return pointAttribute;
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.NAME + Common.Cache.DRIVER_ID, key = "#name+'.'+#driverId", unless = "#result==null")
    public PointAttribute selectByNameAndDriverId(String name, Long driverId) {
        LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
        queryWrapper.eq(PointAttribute::getName, name);
        queryWrapper.eq(PointAttribute::getDriverId, driverId);
        PointAttribute pointAttribute = pointAttributeMapper.selectOne(queryWrapper);
        if (null == pointAttribute) {
            throw new NotFoundException("The point attribute does not exist");
        }
        return pointAttribute;
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.DRIVER_ID + Common.Cache.LIST, key = "#driverId", unless = "#result==null")
    public List<PointAttribute> selectByDriverId(Long driverId) {
        PointAttributeDto pointAttributeDto = new PointAttributeDto();
        pointAttributeDto.setDriverId(driverId);
        List<PointAttribute> pointAttributes = pointAttributeMapper.selectList(fuzzyQuery(pointAttributeDto));
        if (null == pointAttributes || pointAttributes.size() < 1) {
            throw new NotFoundException("The point attributes does not exist");
        }
        return pointAttributes;
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<PointAttribute> list(PointAttributeDto pointAttributeDto) {
        if (!Optional.ofNullable(pointAttributeDto.getPage()).isPresent()) {
            pointAttributeDto.setPage(new Pages());
        }
        return pointAttributeMapper.selectPage(pointAttributeDto.getPage().convert(), fuzzyQuery(pointAttributeDto));
    }

    @Override
    public LambdaQueryWrapper<PointAttribute> fuzzyQuery(PointAttributeDto pointAttributeDto) {
        LambdaQueryWrapper<PointAttribute> queryWrapper = Wrappers.<PointAttribute>query().lambda();
        if (null != pointAttributeDto) {
            if (StrUtil.isNotBlank(pointAttributeDto.getName())) {
                queryWrapper.like(PointAttribute::getName, pointAttributeDto.getName());
            }
            if (StrUtil.isNotBlank(pointAttributeDto.getDisplayName())) {
                queryWrapper.like(PointAttribute::getDisplayName, pointAttributeDto.getDisplayName());
            }
            if (StrUtil.isNotBlank(pointAttributeDto.getType())) {
                queryWrapper.eq(PointAttribute::getType, pointAttributeDto.getType());
            }
            Optional.ofNullable(pointAttributeDto.getDriverId()).ifPresent(driverId -> {
                queryWrapper.eq(PointAttribute::getDriverId, driverId);
            });
        }
        return queryWrapper;
    }

}
