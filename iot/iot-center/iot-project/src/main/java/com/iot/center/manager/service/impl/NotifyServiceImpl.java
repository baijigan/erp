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

import com.iot.center.manager.service.DriverService;
import com.iot.center.manager.service.NotifyService;
import com.dc3.common.bean.driver.DriverConfiguration;
import com.dc3.common.constant.Common;
import com.dc3.common.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * NotifyService Impl
 *
 * @author njrsun20240123
 */
@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {

    @Resource
    private DriverService driverService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void notifyDriverProfile(String command, Profile profile) {
        try {
            List<Driver> drivers = driverService.selectByProfileId(profile.getId());
            drivers.forEach(driver -> {
                DriverConfiguration operation = new DriverConfiguration().setType(Common.Driver.Type.PROFILE).setCommand(command).setContent(profile);
                notifyDriver(driver, operation);
            });
        } catch (Exception e) {
            log.warn("Notify Driver Profile : {}", e.getMessage());
        }
    }

    @Override
    public void notifyDriverPoint(String command, Point point) {
        try {


            Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
            driverMetadata.setProfilePointMap(profilePointMap);
            Pages pages = null == deviceEventDto.getPage() ? new Pages() : deviceEventDto.getPage();
            if (pages.getStartTime() > 0 && pages.getEndTime() > 0 && pages.getStartTime() <= pages.getEndTime()) {
                criteria.and("originTime").gte(pages.getStartTime()).lte(pages.getEndTime());
            }


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

            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
            driverMetadata.setDriverAttributeMap(driverAttributeMap);

            Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
            driverMetadata.setPointAttributeMap(pointAttributeMap);

            List<Device> devices = deviceService.selectByDriverId(driver.getId());
            Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

            Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
            driverMetadata.setDriverInfoMap(driverInfoMap);


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


            Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
            driverMetadata.setDriverAttributeMap(driverAttributeMap);

            Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
            driverMetadata.setPointAttributeMap(pointAttributeMap);

            List<Device> devices = deviceService.selectByDriverId(driver.getId());
            Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

            Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
            driverMetadata.setDriverInfoMap(driverInfoMap);

            List<Dictionary> dictionaries = new ArrayList<>(16);
            LambdaQueryWrapper<Driver> queryWrapper = Wrappers.<Driver>query().lambda();
            queryWrapper.eq(Driver::getTenantId, tenantId);

            LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
            queryWrapper.eq(Label::getName, name);
            queryWrapper.eq(Label::getTenantId, tenantId);
            Label label = labelMapper.selectOne(queryWrapper);
            if (null == label) {
                throw new NotFoundException("The label does not exist");
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



                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

                Map<Long, PointAttribute> pointAttributeMap = getPointAttributeMap(driver.getId());
                driverMetadata.setPointAttributeMap(pointAttributeMap);

                List<Device> devices = deviceService.selectByDriverId(driver.getId());
                Set<Long> deviceIds = devices.stream().map(Description::getId).collect(Collectors.toSet());

                Map<Long, Map<String, AttributeInfo>> driverInfoMap = getDriverInfoMap(deviceIds, driverAttributeMap);
                driverMetadata.setDriverInfoMap(driverInfoMap);

                Map<Long, Map<Long, Point>> profilePointMap = getProfilePointMap(deviceIds);
                driverMetadata.setProfilePointMap(profilePointMap);


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

                Map<Long, DriverAttribute> driverAttributeMap = getDriverAttributeMap(driver.getId());
                driverMetadata.setDriverAttributeMap(driverAttributeMap);

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
        } catch (Exception e) {
            log.warn("Notify Driver Point : {}", e.getMessage());
        }
    }

    @Override
    public void notifyDriverDevice(String command, Device device) {
        try {
            Driver driver = driverService.selectById(device.getDriverId());
            DriverConfiguration operation = new DriverConfiguration().setType(Common.Driver.Type.DEVICE).setCommand(command).setContent(device);
            notifyDriver(driver, operation);
        } catch (Exception e) {
            log.warn("Notify Driver Device : {}", e.getMessage());
        }
    }

    @Override
    public void notifyDriverDriverInfo(String command, DriverInfo driverInfo) {
        try {
            Driver driver = driverService.selectByDeviceId(driverInfo.getDeviceId());
            DriverConfiguration operation = new DriverConfiguration().setType(Common.Driver.Type.DRIVER_INFO).setCommand(command).setContent(driverInfo);
            notifyDriver(driver, operation);
        } catch (Exception e) {
            log.warn("Notify Driver DriverInfo : {}", e.getMessage());
        }
    }

    @Override
    public void notifyDriverPointInfo(String command, PointInfo pointInfo) {
        try {
            Driver driver = driverService.selectByDeviceId(pointInfo.getDeviceId());
            DriverConfiguration operation = new DriverConfiguration().setType(Common.Driver.Type.POINT_INFO).setCommand(command).setContent(pointInfo);
            notifyDriver(driver, operation);
        } catch (Exception e) {
            log.warn("Notify Driver PointInfo : {}", e.getMessage());
        }
    }

    /**
     * notify driver
     *
     * @param driver              Driver
     * @param driverConfiguration DriverConfiguration
     */
    private void notifyDriver(Driver driver, DriverConfiguration driverConfiguration) {
        log.debug("Notify Driver {} : {}", driver.getServiceName(), driverConfiguration);
        rabbitTemplate.convertAndSend(Common.Rabbit.TOPIC_EXCHANGE_METADATA, Common.Rabbit.ROUTING_DRIVER_METADATA_PREFIX + driver.getServiceName(), driverConfiguration);
    }

}
