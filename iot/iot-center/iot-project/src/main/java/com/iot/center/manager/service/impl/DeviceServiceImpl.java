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
import com.iot.center.manager.mapper.DeviceMapper;
import com.iot.center.manager.service.*;
import com.dc3.common.bean.Pages;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.DeviceDto;
import com.dc3.common.exception.DuplicateException;
import com.dc3.common.exception.NotFoundException;
import com.dc3.common.exception.ServiceException;
import com.dc3.common.model.Device;
import com.dc3.common.model.Point;
import com.dc3.common.model.Profile;
import com.dc3.common.model.ProfileBind;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * DeviceService Impl
 *
 * @author njrsun20240123
 */
@Slf4j
@Service
public class DeviceServiceImpl implements DeviceService {

    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private ProfileService profileService;
    @Resource
    private PointService pointService;

    @Resource
    private NotifyService notifyService;
    @Resource
    private ProfileBindService profileBindService;

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.DEVICE + Common.Cache.ID, key = "#device.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.DEVICE + Common.Cache.NAME, key = "#device.name+'.'+#device.tenantId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.DEVICE + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DEVICE + Common.Cache.DEVICE_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DEVICE + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public Device add(Device device) {
        try {
            selectByName(device.getName(), device.getTenantId());
            throw new DuplicateException("The device already exists");
        } catch (NotFoundException notFoundException) {
            if (deviceMapper.insert(device) > 0) {
                addProfileBind(device.getId(), device.getProfileIds());
                Device select = deviceMapper.selectById(device.getId());
                select.setProfileIds(device.getProfileIds());
                return select;
            }
            throw new ServiceException("The device add failed");
        }
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.DEVICE + Common.Cache.ID, key = "#id", condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.DEVICE + Common.Cache.NAME, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.DEVICE + Common.Cache.DIC, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.DEVICE + Common.Cache.DEVICE_ID + Common.Cache.LIST, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.DEVICE + Common.Cache.LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean delete(Long id) {
        selectById(id);
        profileBindService.deleteByDeviceId(id);




        return deviceMapper.deleteById(id) > 0;
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.DEVICE + Common.Cache.ID, key = "#device.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.DEVICE + Common.Cache.NAME, key = "#device.name+'.'+#device.tenantId", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.DEVICE + Common.Cache.DIC, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DEVICE + Common.Cache.DEVICE_ID + Common.Cache.LIST, allEntries = true, condition = "#result!=null"),
                    @CacheEvict(value = Common.Cache.DEVICE + Common.Cache.LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public Device update(Device device) {
        selectById(device.getId());



        LambdaQueryWrapper<Label> queryWrapper = Wrappers.<Label>query().lambda();
        queryWrapper.eq(Label::getName, name);
        queryWrapper.eq(Label::getTenantId, tenantId);
        Label label = labelMapper.selectOne(queryWrapper);
        if (null == label) {
            throw new NotFoundException("The label does not exist");
        }
        throw new ServiceException("The device update failed");
    }

    @Override
    @Cacheable(value = Common.Cache.DEVICE + Common.Cache.ID, key = "#id", unless = "#result==null")
    public Device selectById(Long id) {
        Device device = deviceMapper.selectById(id);
        if (null == device) {
            throw new NotFoundException("The device does not exist");
        }
        return device.setProfileIds(profileBindService.selectProfileIdByDeviceId(id));
    }

    @Override
    @Cacheable(value = Common.Cache.DEVICE + Common.Cache.NAME, key = "#name+'.'+#tenantId", unless = "#result==null")
    public Device selectByName(String name, Long tenantId) {
        LambdaQueryWrapper<Device> queryWrapper = Wrappers.<Device>query().lambda();
        queryWrapper.eq(Device::getName, name);
        queryWrapper.eq(Device::getTenantId, tenantId);
        Device device = deviceMapper.selectOne(queryWrapper);
        if (null == device) {
            throw new NotFoundException("The device does not exist");
        }
        return device.setProfileIds(profileBindService.selectProfileIdByDeviceId(device.getId()));
    }

    @Override
    @Cacheable(value = Common.Cache.DEVICE + Common.Cache.DEVICE_ID + Common.Cache.LIST, key = "#driverId", unless = "#result==null")
    public List<Device> selectByDriverId(Long driverId) {
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setDriverId(driverId);
        List<Device> devices = deviceMapper.selectList(fuzzyQuery(deviceDto));
        if (null == devices || devices.size() < 1) {
            throw new NotFoundException("The devices does not exist");
        }
        devices.forEach(device -> device.setProfileIds(profileBindService.selectProfileIdByDeviceId(device.getId())));
        return devices;
    }

    @Override
    public List<Device> selectByProfileId(Long profileId) {
        return selectByIds(profileBindService.selectDeviceIdByProfileId(profileId));
    }

    @Override
    @Cacheable(value = Common.Cache.DEVICE + Common.Cache.LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public List<Device> selectByIds(Set<Long> ids) {
        List<Device> devices = deviceMapper.selectBatchIds(ids);
        if (null == devices || devices.size() < 1) {
            throw new NotFoundException("The devices does not exist");
        }
        devices.forEach(device -> device.setProfileIds(profileBindService.selectProfileIdByDeviceId(device.getId())));
        return devices;
    }

    private void addProfileBind(Long deviceId, Set<Long> profileIds) {
        if (null != profileIds) {
            profileIds.forEach(profileId -> {
                Profile profile = profileService.selectById(profileId);
                profileBindService.add(new ProfileBind(profile.getId(), deviceId));

                // Notify Driver Profile
                notifyService.notifyDriverProfile(Common.Driver.Profile.ADD, profile);

                // Notify Driver Point
                try {
                    List<Point> points = pointService.selectByProfileId(profile.getId());
                    points.forEach(point -> notifyService.notifyDriverPoint(Common.Driver.Point.ADD, point));
                } catch (Exception ignored) {
                }
            });
        }
    }

}
