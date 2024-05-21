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
import com.iot.center.manager.mapper.PointInfoMapper;
import com.iot.center.manager.service.PointInfoService;
import com.iot.center.manager.service.PointService;
import com.dc3.common.bean.Pages;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.PointInfoDto;
import com.dc3.common.exception.DuplicateException;
import com.dc3.common.exception.NotFoundException;
import com.dc3.common.exception.ServiceException;
import com.dc3.common.model.Point;
import com.dc3.common.model.PointInfo;
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
 * PointInfoService Impl
 *
 * @author njrsun20240123
 */
@Slf4j
@Service
public class PointInfoServiceImpl implements PointInfoService {

    @Resource
    private PointService pointService;

    @Resource
    private PointInfoMapper pointInfoMapper;


    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.POINT_INFO + Common.Cache.ID, key = "#pointInfo.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.POINT_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.DEVICE_ID + Common.Cache.POINT_ID, key = "#pointInfo.pointAttributeId+'.'+#pointInfo.deviceId+'.'+#pointInfo.pointId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.DEVICE_ID + Common.Cache.POINT_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.DEVICE_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public PointInfo add(PointInfo pointInfo) {
        try {
            selectByAttributeIdAndDeviceIdAndPointId(pointInfo.getPointAttributeId(), pointInfo.getDeviceId(), pointInfo.getPointId());
            throw new DuplicateException("The point info already exists");
        } catch (NotFoundException notFoundException) {
            if (pointInfoMapper.insert(pointInfo) > 0) {
                return pointInfoMapper.selectById(pointInfo.getId());
            }
            throw new ServiceException("The point info add failed");
        }
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.ID, key = "#id", condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.DEVICE_ID + Common.Cache.POINT_ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.DIC, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.LIST, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.DEVICE_ID + Common.Cache.POINT_ID + Common.Cache.LIST, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.DEVICE_ID + Common.Cache.LIST, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean delete(Long id) {
        selectById(id);
        return pointInfoMapper.deleteById(id) > 0;
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.POINT_INFO + Common.Cache.ID, key = "#pointInfo.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.POINT_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.DEVICE_ID + Common.Cache.POINT_ID, key = "#pointInfo.pointAttributeId+'.'+#pointInfo.deviceId+'.'+#pointInfo.pointId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.DEVICE_ID + Common.Cache.POINT_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.DEVICE_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.POINT_INFO + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )


    @Override
    @Cacheable(value = Common.Cache.LABEL + Common.Cache.ID, key = "#id", unless = "#result==null")
    public Label selectById(Long id) {
        Label label = labelMapper.selectById(id);
        if (null == label) {
            throw new NotFoundException("The label does not exist");
        }
        return label;
    }

    @Override
    @Cacheable(value = Common.Cache.LABEL + Common.Cache.NAME, key = "#name+'.'+#tenantId", unless = "#result==null")
    public Label selectByName(String name, Long tenantId) {

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


        return new PointDetail(device.getId(), point.getId());
    }


    Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
        driverMetadata.setPointAttributeMap(pointAttributeMap);

    List<Device> devices = deviceService.selectByDriverId(driver.getId());
    Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

    Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
        driverMetadata.setDriverInfoMap(driverInfoMap);


    Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
        driverMetadata.setPointAttributeMap(pointAttributeMap);

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


        }


        if (!old.getPointAttributeId().equals(pointInfo.getPointAttributeId()) || !old.getDeviceId().equals(pointInfo.getDeviceId()) || !old.getPointId().equals(pointInfo.getPointId())) {
        try {
        selectByAttributeIdAndDeviceIdAndPointId(pointInfo.getPointAttributeId(), pointInfo.getDeviceId(), pointInfo.getPointId());
        throw new DuplicateException("The point info already exists");
        } catch (NotFoundException ignored) {
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


        // add profile bind
        if (null != device.getId() && null != profile.getId()) {
        try {
        ProfileBind profileBind = new ProfileBind();
        profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
        profileBindService.add(profileBind);
        } catch (Exception ignored) {
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
        s

        Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
        driverMetadata.setDriverAttributeMap(driverAttributeMap);

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


        Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
        driverMetadata.setProfilePointMap(profilePointMap);

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

        return new PointDetail(device.getId(), point.getId());
        }

        Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
        driverMetadata.setProfilePointMap(profilePointMap);

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

        return label;
    }

    @Override
    @Cacheable(value = Common.Cache.LABEL + Common.Cache.LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<Label> list(LabelDto labelDto) {

        return labelMapper.selectPage(labelDto.getPage().convert(), fuzzyQuery(labelDto));
    }


    public PointInfo update(PointInfo pointInfo) {
        PointInfo old = selectById(pointInfo.getId());
        pointInfo.setUpdateTime(null);

<<<<<<< HEAD
=======

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


>>>>>>> b7b8838... iot dev
        throw new ServiceException("The point info update failed");
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_INFO + Common.Cache.ID, key = "#id", unless = "#result==null")
    public PointInfo selectById(Long id) {
        PointInfo pointInfo = pointInfoMapper.selectById(id);
        if (null == pointInfo) {
            throw new NotFoundException("The point info does not exist");
        }
        return pointInfo;
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.DEVICE_ID + Common.Cache.POINT_ID, key = "#pointAttributeId+'.'+#deviceId+'.'+#pointId", unless = "#result==null")
    public PointInfo selectByAttributeIdAndDeviceIdAndPointId(Long pointAttributeId, Long deviceId, Long pointId) {
        LambdaQueryWrapper<PointInfo> queryWrapper = Wrappers.<PointInfo>query().lambda();
        queryWrapper.eq(PointInfo::getPointAttributeId, pointAttributeId);
        queryWrapper.eq(PointInfo::getDeviceId, deviceId);
        queryWrapper.eq(PointInfo::getPointId, pointId);
        PointInfo pointInfo = pointInfoMapper.selectOne(queryWrapper);
        if (null == pointInfo) {
            throw new NotFoundException("The point info does not exist");
        }
        return pointInfo;
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.LIST, key = "#pointAttributeId", unless = "#result==null")
    public List<PointInfo> selectByAttributeId(Long pointAttributeId) {
        LambdaQueryWrapper<PointInfo> queryWrapper = Wrappers.<PointInfo>query().lambda();
        queryWrapper.eq(PointInfo::getPointAttributeId, pointAttributeId);
        List<PointInfo> pointInfos = pointInfoMapper.selectList(queryWrapper);
        if (null == pointInfos || pointInfos.size() < 1) {
            throw new NotFoundException("The point infos does not exist");
        }
        return pointInfos;
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_INFO + Common.Cache.DEVICE_ID + Common.Cache.LIST, key = "#deviceId", unless = "#result==null")
    public List<PointInfo> selectByDeviceId(Long deviceId) {
        LambdaQueryWrapper<PointInfo> queryWrapper = Wrappers.<PointInfo>query().lambda();
        List<Point> points = pointService.selectByDeviceId(deviceId);
        Set<Long> pointIds = points.stream().map(Point::getId).collect(Collectors.toSet());
        queryWrapper.eq(PointInfo::getDeviceId, deviceId);
        queryWrapper.in(PointInfo::getPointId, pointIds);
        List<PointInfo> pointInfos = pointInfoMapper.selectList(queryWrapper);
        if (null == pointInfos) {
            throw new NotFoundException("The point infos does not exist");
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


        for (String name : oldDriverAttributeMap.keySet()) {
            if (!newDriverAttributeMap.containsKey(name)) {
                try {
                    driverInfoService.selectByAttributeId(oldDriverAttributeMap.get(name).getId());
                    throw new ServiceException("The driver attribute(" + name + ") used by driver info and cannot be deleted");
                } catch (NotFoundException notFoundException) {
                    log.debug("Driver attribute is redundant, deleting: {}", oldDriverAttributeMap.get(name));
                    driverAttributeService.delete(oldDriverAttributeMap.get(name).getId());
                }
            }
        }

        DriverInfoDto driverInfoDto = new DriverInfoDto();
        driverInfoDto.setDriverAttributeId(driverAttributeId);
        driverInfoDto.setDeviceId(deviceId);
        DriverInfo driverInfo = driverInfoMapper.selectOne(fuzzyQuery(driverInfoDto));
        if (null == driverInfo) {
            throw new NotFoundException("The driver info does not exist");
        }


        LambdaQueryWrapper<DriverInfo> queryWrapper = Wrappers.<DriverInfo>query().lambda();
        if (null != driverInfoDto) {
            if (null != driverInfoDto.getDriverAttributeId()) {
                queryWrapper.eq(DriverInfo::getDriverAttributeId, driverInfoDto.getDriverAttributeId());
            }
            if (null != driverInfoDto.getDeviceId()) {
                queryWrapper.eq(DriverInfo::getDeviceId, driverInfoDto.getDeviceId());
            }
        }

        profileBindService.selectDeviceIdByProfileId(profileId).forEach(id -> {
            String key = Common.Cache.DEVICE_STATUS_KEY_PREFIX + id;
            String status = redisUtil.getKey(key, String.class);
            status = null != status ? status : Common.Driver.Status.OFFLINE;
            statusMap.put(id, status);
        });
        return pointInfos;
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_INFO + Common.Cache.DEVICE_ID + Common.Cache.POINT_ID + Common.Cache.LIST, key = "#deviceId+'.'+#pointId", unless = "#result==null")
    public List<PointInfo> selectByDeviceIdAndPointId(Long deviceId, Long pointId) {
        LambdaQueryWrapper<PointInfo> queryWrapper = Wrappers.<PointInfo>query().lambda();
        queryWrapper.eq(PointInfo::getDeviceId, deviceId);
        queryWrapper.eq(PointInfo::getPointId, pointId);
        List<PointInfo> pointInfos = pointInfoMapper.selectList(queryWrapper);
        if (null == pointInfos) {
            throw new NotFoundException("The point infos does not exist");
        }
        return pointInfos;
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_INFO + Common.Cache.LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<PointInfo> list(PointInfoDto pointInfoDto) {
        if (!Optional.ofNullable(pointInfoDto.getPage()).isPresent()) {
            pointInfoDto.setPage(new Pages());
        }
        return pointInfoMapper.selectPage(pointInfoDto.getPage().convert(), fuzzyQuery(pointInfoDto));
    }

    @Override
    public LambdaQueryWrapper<PointInfo> fuzzyQuery(PointInfoDto pointInfoDto) {

        return queryWrapper;
    }

}
