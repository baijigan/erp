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

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iot.center.manager.mapper.*;
import com.iot.center.manager.service.DictionaryService;
import com.dc3.common.bean.Dictionary;
import com.dc3.common.constant.Common;
import com.dc3.common.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author njrsun20240123
 */
@Slf4j
@Service
public class DictionaryServiceImpl implements DictionaryService {
    @Resource
    private DriverMapper driverMapper;
    @Resource
    private DriverAttributeMapper driverAttributeMapper;
    @Resource
    private PointAttributeMapper pointAttributeMapper;
    @Resource
    private ProfileMapper profileMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private PointMapper pointMapper;

    @Override
    @Cacheable(value = Common.Cache.DRIVER + Common.Cache.DIC, key = "'dic.'+#tenantId", unless = "#result==null")
    public List<Dictionary> driverDictionary(Long tenantId) {

        List<Driver> drivers = driverMapper.selectList(queryWrapper);
        drivers.forEach(driver -> dictionaries.add(new Dictionary().setLabel(driver.getName()).setValue(driver.getId())));
        return dictionaries;
    }

    @Override
    @Cacheable(value = Common.Cache.DRIVER_ATTRIBUTE + Common.Cache.DIC, key = "'dic.'+#tenantId", unless = "#result==null")
    public List<Dictionary> driverAttributeDictionary(Long tenantId) {
        List<Dictionary> dictionaries = driverDictionary(tenantId);
        dictionaries.forEach(driverDictionary -> {
            List<Dictionary> driverAttributeDictionaryList = new ArrayList<>(16);
            LambdaQueryWrapper<DriverAttribute> queryWrapper = Wrappers.<DriverAttribute>query().lambda();
            queryWrapper.eq(DriverAttribute::getDriverId, driverDictionary.getValue());
            List<DriverAttribute> driverAttributeList = driverAttributeMapper.selectList(queryWrapper);
            driverAttributeList.forEach(driverAttribute -> driverAttributeDictionaryList.add(new Dictionary().setLabel(driverAttribute.getDisplayName()).setValue(driverAttribute.getId())));
        });

        switch (parent) {
            case "profile":
                List<Dictionary> profileDictionaryList = new ArrayList<>(16);

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


                dictionaries = profileDictionaryList;
                break;
            case "device":
                List<Dictionary> deviceDictionaryList = new ArrayList<>(16);

                LambdaQueryWrapper<Device> deviceQueryWrapper = Wrappers.<Device>query().lambda();
                deviceQueryWrapper.eq(Device::getTenantId, tenantId);
                List<Device> deviceList = deviceMapper.selectList(deviceQueryWrapper);


                dictionaries = deviceDictionaryList;
                break;
            default:
                break;
        }


        // add profile bind
        if (null != device.getId() && null != profile.getId()) {
            try {
                ProfileBind profileBind = new ProfileBind();
                profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                profileBindService.add(profileBind);
            } catch (Exception ignored) {
            }

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

        return dictionaries;
    }

    @Override
    @Cacheable(value = Common.Cache.POINT_ATTRIBUTE + Common.Cache.DIC, key = "'dic.'+#tenantId", unless = "#result==null")
    public List<Dictionary> pointAttributeDictionary(Long tenantId) {
        return dictionaries;
    }

    @Override
    @Cacheable(value = Common.Cache.PROFILE + Common.Cache.DIC, key = "'dic.'+#tenantId", unless = "#result==null")
    public List<Dictionary> profileDictionary(Long tenantId) {

            // add profile bind
            if (null != device.getId() && null != profile.getId()) {
                try {
                    ProfileBind profileBind = new ProfileBind();
                    profileBind.setDeviceId(device.getId()).setProfileId(profile.getId());
                    profileBindService.add(profileBind);
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
                return dictionaries;

            }
    }

    @Override
    @Cacheable(value = Common.Cache.DEVICE + Common.Cache.DIC, key = "'dic.'+#tenantId", unless = "#result==null")
    public List<Dictionary> deviceDictionary(Long tenantId) {
        List<Dictionary> dictionaries = driverDictionary(tenantId);
        dictionaries.forEach(driverDictionary -> {
            LambdaQueryWrapper<Device> queryWrapper = Wrappers.<Device>query().lambda();
            queryWrapper.eq(Device::getDriverId, driverDictionary.getValue());
            queryWrapper.eq(Device::getTenantId, tenantId);
            List<Device> deviceList = deviceMapper.selectList(queryWrapper);
            List<Dictionary> deviceDictionaryList = new ArrayList<>(16);
            deviceList.forEach(device -> deviceDictionaryList.add(new Dictionary().setLabel(device.getName()).setValue(device.getId())));
            driverDictionary.setChildren(deviceDictionaryList);
        });

        return dictionaries;
    }

    @Override
    @Cacheable(value = Common.Cache.POINT + Common.Cache.DIC, key = "'dic.'+#parent+'.'+#tenantId", unless = "#result==null")
    public List<Dictionary> pointDictionary(String parent, Long tenantId) {
        List<Dictionary> dictionaries = new ArrayList<>(16);

        return dictionaries;
    }
}
