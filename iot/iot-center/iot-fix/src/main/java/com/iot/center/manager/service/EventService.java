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

package  com.iot.center.manager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dc3.common.dto.DeviceEventDto;
import com.dc3.common.dto.DriverEventDto;
import com.dc3.common.model.DeviceEvent;
import com.dc3.common.model.DriverEvent;

import java.util.List;

/**
 * @author njrsun20240123
 */
public interface EventService {
    /**
     * 根据 模版Id集 查询模版
     *
     * @param ids Profile Id Set
     * @return Profile Array
     */
    List<Profile> selectByIds(Set<Long> ids);

    /**
     * 根据 设备Id 查询模版
     *
     * @param deviceId Device Id
     * @return Profile Array
     */
    List<Profile> selectByDeviceId(Long deviceId);
    /**
     * 新增 DriverEvent
     *
     * @param driverEvent DriverEvent
     */
    void addDriverEvent(DriverEvent driverEvent);

    /**
     * 批量新增 DriverEvent
     *
     * @param driverEvents DriverEvent Array
     */
    void addDriverEvents(List<DriverEvent> driverEvents);

    /**
     * 新增 DeviceEvent
     *
     * @param deviceEvent DeviceEvent
     */
    void addDeviceEvent(DeviceEvent deviceEvent);

    /**
     * 批量新增 DeviceEvent
     *
     * @param deviceEvents DeviceEvent Array
     */
    void addDeviceEvents(List<DeviceEvent> deviceEvents);

    /**
     * 获取 DriverEvent 带分页、排序
     *
     * @param driverEventDto DriverEventDto
     * @return Page<DriverEvent>
     */
    Page<DriverEvent> driverEvent(DriverEventDto driverEventDto);

    /**
     * 获取 DeviceEvent 带分页、排序
     *
     * @param deviceEventDto DeviceEventDto
     * @return Page<DeviceEvent>
     */
    Page<DeviceEvent> deviceEvent(DeviceEventDto deviceEventDto);

}
