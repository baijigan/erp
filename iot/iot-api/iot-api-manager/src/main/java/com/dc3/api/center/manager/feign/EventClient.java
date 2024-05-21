/*
 * Copyright 2018-2023 nanjing rising sun software. All Rights Reserved.
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

package com.njrsun.iot.api.center.manager.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njrsun.iot.api.center.manager.hystrix.EventClientHystrix;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.constant.Common;
import com.njrsun.iot.common.dto.DeviceEventDto;
import com.njrsun.iot.common.dto.DriverEventDto;
import com.njrsun.iot.common.model.DeviceEvent;
import com.njrsun.iot.common.model.DriverEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 数据 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_MANAGER_EVENT_URL_PREFIX, name = Common.Service.DC3_MANAGER_SERVICE_NAME, fallbackFactory = EventClientHystrix.class)
public interface EventClient {

    /**
     * 分页查询 DriverEvent
     *
     * @param driverEventDto DriverEventDto
     * @return Page<DriverEvent>
     */
    @PostMapping("/driver")
    R<Page<DriverEvent>> driverEvent(@RequestBody(required = false) DriverEventDto driverEventDto);

    /**
     * 分页查询 DeviceEvent
     *
     * @param deviceEventDto DeviceEventDto
     * @return Page<DeviceEvent>
     */
    @PostMapping("/device")
    R<Page<DeviceEvent>> deviceEvent(@RequestBody(required = false) DeviceEventDto deviceEventDto);
}
