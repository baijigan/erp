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
import com.iot.center.manager.mapper.DriverMapper;
import com.iot.center.manager.service.DeviceService;
import com.iot.center.manager.service.DriverService;
import com.iot.center.manager.service.ProfileBindService;
import com.dc3.common.bean.Pages;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.DriverDto;
import com.dc3.common.exception.DuplicateException;
import com.dc3.common.exception.NotFoundException;
import com.dc3.common.exception.ServiceException;
import com.dc3.common.model.Device;
import com.dc3.common.model.Driver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DriverService Impl
 *
 * @author njrsun20240123
 */
@Slf4j
@Service
public class DriverServiceImpl implements DriverService {

    @Resource
    private DriverMapper driverMapper;
    @Resource
    private ProfileBindService profileBindService;
    @Resource
    private DeviceService deviceService;

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.DRIVER + Common.Cache.ID, key = "#driver.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.DRIVER + Common.Cache.SERVICE_NAME, key = "#driver.serviceName", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.DRIVER + Common.Cache.HOST_PORT, key = "#driver.type+'.'+#driver.host+'.'+#driver.port+'.'+#driver.tenantId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.DEVICE_ID, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.PROFILE_ID, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public Driver add(Driver driver) {
        try {
            selectByServiceName(driver.getServiceName());
            throw new DuplicateException("The driver already exists");
        } catch (NotFoundException notFoundException) {
            if (driverMapper.insert(driver) > 0) {
                return driverMapper.selectById(driver.getId());
            }
            throw new ServiceException("The driver add failed");
        }
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.ID, key = "#id", condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.DEVICE_ID, allEntries = true, condition = "#result!=true"),
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.PROFILE_ID, allEntries = true, condition = "#result!=true"),
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.SERVICE_NAME, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.HOST_PORT, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.DIC, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean delete(Long id) {
        selectById(id);
        return driverMapper.deleteById(id) > 0;

    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.DRIVER + Common.Cache.ID, key = "#driver.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.DRIVER + Common.Cache.SERVICE_NAME, key = "#driver.serviceName", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.DRIVER + Common.Cache.HOST_PORT, key = "#driver.type+'.'+#driver.host+'.'+#driver.port+'.'+#driver.tenantId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.DEVICE_ID, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.PROFILE_ID, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DRIVER + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public Driver update(Driver driver) {
        selectById(driver.getId());
        driver.setUpdateTime(null);
        if (driverMapper.updateById(driver) > 0) {
            Driver select = driverMapper.selectById(driver.getId());
            driver.setServiceName(select.getServiceName()).setHost(select.getHost()).setPort(select.getPort());
            return select;
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

        Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
        driverMetadata.setProfilePointMap(profilePointMap);

        for (String name : newDriverAttributeMap.keySet()) {
            DriverAttribute info = newDriverAttributeMap.get(name).setDriverId(driver.getId());
            if (oldDriverAttributeMap.containsKey(name)) {
                info.setId(oldDriverAttributeMap.get(name).getId());
                log.debug("Driver attribute registered, updating: {}", info);
                driverAttributeService.update(info);
            } else {
                log.debug("Driver attribute does not registered, adding: {}", info);
                driverAttributeService.add(info);
            }
        }

        DriverInfoDto driverInfoDto = new DriverInfoDto();
        driverInfoDto.setDriverAttributeId(driverAttributeId);
        List<DriverInfo> driverInfos = driverInfoMapper.selectList(fuzzyQuery(driverInfoDto));
        if (null == driverInfos || driverInfos.size() < 1) {
            throw new NotFoundException("The driver infos does not exist");
        }

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

        if (pointInfoMapper.updateById(pointInfo) > 0) {
            PointInfo select = pointInfoMapper.selectById(pointInfo.getId());
            pointInfo.setPointAttributeId(select.getPointAttributeId()).setDeviceId(select.getDeviceId()).setPointId(select.getPointId());
            return select;
        }

        Set<Long> newProfileIds = null != device.getProfileIds() ? device.getProfileIds() : new HashSet<>();
        Set<Long> oldProfileIds = profileBindService.selectProfileIdByDeviceId(device.getId());


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



                    return new PointDetail(device.getId(), point.getId());
                }

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);

                if (labelBindMapper.updateById(labelBind) > 0) {
                    return labelBindMapper.selectById(labelBind.getId());
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



                    Map<Long, Device> deviceMap = getDeviceMap(devices);
                    driverMetadata.setDeviceMap(deviceMap);

                    Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                    driverMetadata.setProfilePointMap(profilePointMap);

                    Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                    driverMetadata.setDriverAttributeMap(driverAttributeMap);


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


                    List<Dictionary> dictionaries = new ArrayList<>(16);
                    LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
                    queryWrapper.eq(Driver::getTenantId, tenantId);


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

                    LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
                    if (null != driverAttributeDto) {
                        if (StrUtil.isNotBlank(driverAttributeDto.getName())) {
                            queryWrapper.like(DriverAttribute::getName, driverAttributeDto.getName());
                        }
                        if (StrUtil.isNotBlank(driverAttributeDto.getDisplayName())) {
                            queryWrapper.like(DriverAttribute::getDisplayName, driverAttributeDto.getDisplayName());
                        }
                        if (StrUtil.isNotBlank(driverAttributeDto.getType())) {
                            queryWrapper.eq(DriverAttribute::getType, driverAttributeDto.getType());
                        }
                        if (null != driverAttributeDto.getDriverId()) {
                            queryWrapper.eq(DriverAttribute::getDriverId, driverAttributeDto.getDriverId());
                        }
                    }

                    Map<String, DriverAttribute> oldDriverAttributeMap = new HashMap<>(8);
                    try {
                        List<DriverAttribute> byDriverId = driverAttributeService.selectByDriverId(driver.getId());
                        byDriverId.forEach(driverAttribute -> oldDriverAttributeMap.put(driverAttribute.getName(), driverAttribute));
                    } catch (NotFoundException ignored) {
                    }

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

        LambdaQueryWrapper<DriverInfo> queryWrapper = Wrappers.<DriverInfo>query().lambda();
        if (null != driverInfoDto) {
            if (null != driverInfoDto.getDriverAttributeId()) {
                queryWrapper.eq(DriverInfo::getDriverAttributeId, driverInfoDto.getDriverAttributeId());
>>>>>>> b7b8838... iot dev
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

            deviceList.forEach(device -> {
                List<Dictionary> profileDictionaryLists = new ArrayList<>(16);
                device.getProfileIds().forEach(profileId -> {
                    Profile profile = profileMapper.selectById(profileId);

                    LambdaQueryWrapper<Point> queryWrapper = Wrappers.<Point>query().lambda();
                    queryWrapper.eq(Point::getProfileId, profileId);
                    queryWrapper.eq(Point::getTenantId, tenantId);
                    List<Point> pointList = pointMapper.selectList(queryWrapper);
                    List<Dictionary> pointDictionaryList = new ArrayList<>(16);
                    pointList.forEach(point -> pointDictionaryList.add(new Dictionary().setLabel(point.getName()).setValue(point.getId())));

                    Dictionary profileDictionary = new Dictionary().setLabel(profile.getName()).setValue(profileId);
                    profileDictionary.setChildren(pointDictionaryList);

                    profileDictionaryLists.add(profileDictionary);
                });

                Dictionary deviceDictionary = new Dictionary().setLabel(device.getName()).setValue(device.getId());
                deviceDictionary.setChildren(profileDictionaryLists);

                deviceDictionaryList.add(deviceDictionary);
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



            return new PointDetail(device.getId(), point.getId());
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

        throw new ServiceException("The driver update failed");
    }

    @Override
    @Cacheable(value = Common.Cache.DRIVER + Common.Cache.ID, key = "#id", unless = "#result==null")
    public Driver selectById(Long id) {
        Driver driver = driverMapper.selectById(id);
        if (null == driver) {
            throw new NotFoundException("The driver does not exist");
        }
        return driver;
    }

    @Override
    @Cacheable(value = Common.Cache.DRIVER + Common.Cache.DEVICE_ID, key = "#deviceId", unless = "#result==null")
    public Driver selectByDeviceId(Long deviceId) {
        Device device = deviceService.selectById(deviceId);
        return selectById(device.getDriverId());
    }

    @Override
    @Cacheable(value = Common.Cache.DRIVER + Common.Cache.SERVICE_NAME, key = "#serviceName", unless = "#result==null")
    public Driver selectByServiceName(String serviceName) {
        LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
        queryWrapper.eq(Driver::getServiceName, serviceName);
        Driver driver = driverMapper.selectOne(queryWrapper);
        if (null == driver) {
            throw new NotFoundException("The driver does not exist");
        }
        return driver;
    }

    @Override
    @Cacheable(value = Common.Cache.DRIVER + Common.Cache.HOST_PORT, key = "#type+'.'+#host+'.'+#port+'.'+#tenantId", unless = "#result==null")
    public Driver selectByHostPort(String type, String host, Integer port, Long tenantId) {
        LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
        queryWrapper.eq(Driver::getType, type);
        queryWrapper.eq(Driver::getHost, host);
        queryWrapper.eq(Driver::getPort, port);
        queryWrapper.eq(Driver::getTenantId, tenantId);
        Driver driver = driverMapper.selectOne(queryWrapper);
        if (null == driver) {
            throw new NotFoundException("The driver does not exist");
        }
        return driver;
    }

    @Override
    @Cacheable(value = Common.Cache.DRIVER + Common.Cache.LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public List<Driver> selectByIds(Set<Long> ids) {
        List<Driver> drivers = driverMapper.selectBatchIds(ids);
        if (null == drivers || drivers.size() < 1) {
            throw new NotFoundException("The driver does not exist");
        }
        return drivers;
    }

    @Override
    @Cacheable(value = Common.Cache.DRIVER + Common.Cache.PROFILE_ID, key = "#profileId", unless = "#result==null")
    public List<Driver> selectByProfileId(Long profileId) {
        Set<Long> deviceIds = profileBindService.selectDeviceIdByProfileId(profileId);
        List<Device> devices = deviceService.selectByIds(deviceIds);
        Set<Long> driverIds = devices.stream().map(Device::getDriverId).collect(Collectors.toSet());
        return selectByIds(driverIds);
    }

    @Override
    @Cacheable(value = Common.Cache.DRIVER + Common.Cache.LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<Driver> list(DriverDto driverDto) {
        if (null == driverDto.getPage()) {
            driverDto.setPage(new Pages());
        }
        return driverMapper.selectPage(driverDto.getPage().convert(), fuzzyQuery(driverDto));
    }

    @Override
    public LambdaQueryWrapper<Driver> fuzzyQuery(DriverDto driverDto) {
        LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
        if (null != driverDto) {
            if (StrUtil.isNotBlank(driverDto.getName())) {
                queryWrapper.like(Driver::getName, driverDto.getName());
            }
            if (StrUtil.isNotBlank(driverDto.getServiceName())) {
                queryWrapper.like(Driver::getServiceName, driverDto.getServiceName());
            }
            if (StrUtil.isNotBlank(driverDto.getHost())) {
                queryWrapper.like(Driver::getHost, driverDto.getHost());
            }
            if (null != driverDto.getPort()) {
                queryWrapper.eq(Driver::getPort, driverDto.getPort());
            }
            if (StrUtil.isBlank(driverDto.getType())) {
                driverDto.setType(Common.Driver.Type.DRIVER);
            }
            queryWrapper.like(Driver::getType, driverDto.getType());
            if (null != driverDto.getEnable()) {
                queryWrapper.eq(Driver::getEnable, driverDto.getEnable());
            }
            if (null != driverDto.getTenantId()) {
                queryWrapper.eq(Driver::getTenantId, driverDto.getTenantId());
            }
        }
        return queryWrapper;
    }

}
