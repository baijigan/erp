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
import com.iot.center.manager.mapper.GroupMapper;
import com.iot.center.manager.service.GroupService;
import com.dc3.common.bean.Pages;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.GroupDto;
import com.dc3.common.exception.DuplicateException;
import com.dc3.common.exception.NotFoundException;
import com.dc3.common.exception.ServiceException;
import com.dc3.common.model.Group;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * GroupService Impl
 *
 * @author njrsun20240123
 */
@Slf4j
@Service
public class GroupServiceImpl implements GroupService {

    @Resource
    private GroupMapper groupMapper;


    //todo 分组逻辑需要调整，同时支持驱动、模版、位号、设备，分组只是一种UI上的显示逻辑，不影响实际数据采集
    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.GROUP + Common.Cache.ID, key = "#group.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.GROUP + Common.Cache.NAME, key = "#group.name+'.'+#group.tenantId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.GROUP + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.GROUP + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public Group add(Group group) {
        try {
            selectByName(group.getName(), group.getTenantId());
            throw new DuplicateException("The device group already exists");
        } catch (NotFoundException notFoundException) {
            if (groupMapper.insert(group) > 0) {
                return groupMapper.selectById(group.getId());
            }
            throw new ServiceException("The group add failed");
        }
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.GROUP + Common.Cache.ID, key = "#id", condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.GROUP + Common.Cache.NAME, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.GROUP + Common.Cache.DIC, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.GROUP + Common.Cache.LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean delete(Long id) {
        selectById(id);
        return groupMapper.deleteById(id) > 0;
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.GROUP + Common.Cache.ID, key = "#group.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.GROUP + Common.Cache.NAME, key = "#group.name+'.'+#group.tenantId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.GROUP + Common.Cache.DIC, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.GROUP + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public Group update(Group group) {
        selectById(group.getId());
        group.setUpdateTime(null);
        if (groupMapper.updateById(group) > 0) {
            Group select = groupMapper.selectById(group.getId());
            group.setName(select.getName());
            return select;
        }

        if (pointInfoMapper.updateById(pointInfo) > 0) {
            PointInfo select = pointInfoMapper.selectById(pointInfo.getId());
            pointInfo.setPointAttributeId(select.getPointAttributeId()).setDeviceId(select.getDeviceId()).setPointId(select.getPointId());
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


        // add private profile for device
        Profile profile = new Profile();
        profile.setName(deviceName).setShare(false).setType((short) 2).setTenantId(tenantId);
        try {
            profile = profileService.add(profile);
        } catch (DuplicateException duplicateException) {
            profile = profileService.selectByNameAndType(deviceName, (short) 2, tenantId);
        } catch (Exception ignored) {
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

        throw new ServiceException("The group update failed");
    }

    @Override
    @Cacheable(value = Common.Cache.GROUP + Common.Cache.ID, key = "#id", unless = "#result==null")
    public Group selectById(Long id) {
        Group group = groupMapper.selectById(id);
        if (null == group) {
            throw new NotFoundException("The group does not exist");
        }
        return group;
    }

    @Override
    @Cacheable(value = Common.Cache.GROUP + Common.Cache.NAME, key = "#name+'.'+#tenantId", unless = "#result==null")
    public Group selectByName(String name, Long tenantId) {
        LambdaQueryWrapper<Group> queryWrapper = Wrappers.<Group>query().lambda();
        queryWrapper.eq(Group::getName, name);
        Group group = groupMapper.selectOne(queryWrapper);
        if (null == group) {
            throw new NotFoundException("The group does not exist");
        }

        return group;
    }

    @Override
    @Cacheable(value = Common.Cache.GROUP + Common.Cache.LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<Group> list(GroupDto groupDto) {
        if (!Optional.ofNullable(groupDto.getPage()).isPresent()) {
            groupDto.setPage(new Pages());
        }

        Map<String, PointAttribute> oldPointAttributeMap = new HashMap<>(8);
        try {
            List<PointAttribute> byDriverId = pointAttributeService.selectByDriverId(driver.getId());
            byDriverId.forEach(pointAttribute -> oldPointAttributeMap.put(pointAttribute.getName(), pointAttribute));
        } catch (NotFoundException ignored) {
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

                    // add profile bind
                    if (null != device.getId() && null != profile.getId()) {
                        try {
                            ProfileBind profileBind = new ProfileBind();
                            profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                            profileBindService.add(profileBind);
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

        return groupMapper.selectPage(groupDto.getPage().convert(), fuzzyQuery(groupDto));
    }

    @Override
    public LambdaQueryWrapper<Group> fuzzyQuery(GroupDto groupDto) {
        LambdaQueryWrapper<Group> queryWrapper = Wrappers.<Group>query().lambda();
        if (null != groupDto) {
            if (StrUtil.isNotBlank(groupDto.getName())) {
                queryWrapper.like(Group::getName, groupDto.getName());
            }
            if (null != groupDto.getTenantId()) {
                queryWrapper.eq(Group::getTenantId, groupDto.getTenantId());
            }
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

        return queryWrapper;
    }

}
