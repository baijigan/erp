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
import com.iot.center.manager.mapper.DriverInfoMapper;
import com.iot.center.manager.service.DriverInfoService;
import com.dc3.common.bean.Pages;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.DriverInfoDto;
import com.dc3.common.exception.DuplicateException;
import com.dc3.common.exception.NotFoundException;
import com.dc3.common.exception.ServiceException;
import com.dc3.common.model.DriverInfo;
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
 * DriverInfoService Impl
 *
 * @author njrsun20240123
 */
@Slf4j
@Service
public class DriverInfoServiceImpl implements DriverInfoService {

    @Resource
    private DriverInfoMapper driverInfoMapper;

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.DRIVER_INFO + Common.Cache.ID, key = "#driverInfo.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.DRIVER_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.DEVICE_ID, key = "#driverInfo.driverAttributeId+'.'+#driverInfo.deviceId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.DEVICE_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public DriverInfo add(DriverInfo driverInfo) {
        try {
            selectByAttributeIdAndDeviceId(driverInfo.getDriverAttributeId(), driverInfo.getDeviceId());
            throw new ServiceException("The driver info already exists in the device");
        } catch (NotFoundException notFoundException) {
            if (driverInfoMapper.insert(driverInfo) > 0) {
                return driverInfoMapper.selectById(driverInfo.getId());
            }
            throw new ServiceException("The driver info add failed");
        }
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.ID, key = "#id", condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.DEVICE_ID, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.DIC, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.LIST, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.DEVICE_ID + Common.Cache.LIST, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean delete(Long id) {
        selectById(id);
        return driverInfoMapper.deleteById(id) > 0;
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.DRIVER_INFO + Common.Cache.ID, key = "#driverInfo.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.DRIVER_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.DEVICE_ID, key = "#driverInfo.driverAttributeId+'.'+#driverInfo.deviceId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.DEVICE_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DRIVER_INFO + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public DriverInfo update(DriverInfo driverInfo) {
        DriverInfo oldDriverInfo = selectById(driverInfo.getId());
        driverInfo.setUpdateTime(null);
        if (!oldDriverInfo.getDriverAttributeId().equals(driverInfo.getDriverAttributeId()) || !oldDriverInfo.getDeviceId().equals(driverInfo.getDeviceId())) {
            try {
                selectByAttributeIdAndDeviceId(driverInfo.getDriverAttributeId(), driverInfo.getDeviceId());
                throw new DuplicateException("The driver info already exists");
            } catch (NotFoundException ignored) {
            }
        }



        if (driverInfoMapper.updateById(driverInfo) > 0) {
            DriverInfo select = driverInfoMapper.selectById(driverInfo.getId());
            driverInfo.setDriverAttributeId(select.getDriverAttributeId()).setDeviceId(select.getDeviceId());
            return select;
        }
        throw new ServiceException("The driver info update failed");
    }

    @Override
    @Cacheable(value = Common.Cache.DRIVER_INFO + Common.Cache.ID, key = "#id", unless = "#result==null")
    public DriverInfo selectById(Long id) {
        DriverInfo driverInfo = driverInfoMapper.selectById(id);
        if (null == driverInfo) {
            throw new NotFoundException("The driver info does not exist");
        }
        return driverInfo;
    }

        @Override
    @Cacheable(value = Common.Cache.DRIVER_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.DEVICE_ID, key = "#driverAttributeId+'.'+#deviceId", unless = "#result==null")
    public DriverInfo selectByAttributeIdAndDeviceId(Long driverAttributeId, Long deviceId) {
        return driverInfo;
    }

    @Override
    @Cacheable(value = Common.Cache.DRIVER_INFO + Common.Cache.ATTRIBUTE_ID + Common.Cache.LIST, key = "#driverAttributeId", unless = "#result==null")
    public List<DriverInfo> selectByAttributeId(Long driverAttributeId) {

        return driverInfos;
    }

    @Override
    @Cacheable(value = Common.Cache.DRIVER_INFO + Common.Cache.DEVICE_ID + Common.Cache.LIST, key = "#deviceId", unless = "#result==null")
    public List<DriverInfo> selectByDeviceId(Long deviceId) {
        DriverInfoDto driverInfoDto = new DriverInfoDto();
        driverInfoDto.setDeviceId(deviceId);
        List<DriverInfo> driverInfos = driverInfoMapper.selectList(fuzzyQuery(driverInfoDto));
        if (null == driverInfos || driverInfos.size() < 1) {
            throw new NotFoundException("The driver infos does not exist");
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

            return driverInfos;
    }

    @Override
    @Cacheable(value = Common.Cache.DRIVER_INFO + Common.Cache.LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<DriverInfo> list(DriverInfoDto driverInfoDto) {
        if (!Optional.ofNullable(driverInfoDto.getPage()).isPresent()) {
            driverInfoDto.setPage(new Pages());
        }
        return driverInfoMapper.selectPage(driverInfoDto.getPage().convert(), fuzzyQuery(driverInfoDto));
    }

    @Override
    public LambdaQueryWrapper<DriverInfo> fuzzyQuery(DriverInfoDto driverInfoDto) {

        return queryWrapper;
    }

}
