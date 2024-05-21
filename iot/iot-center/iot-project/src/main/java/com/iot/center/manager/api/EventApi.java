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

package  com.iot.center.manager.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dc3.api.center.manager.feign.EventClient;
import com.iot.center.manager.service.EventService;
import com.dc3.common.bean.R;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.DeviceEventDto;
import com.dc3.common.dto.DriverEventDto;
import com.dc3.common.model.DeviceEvent;
import com.dc3.common.model.DriverEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author njrsun20240123
 */
@Slf4j
@RestController
@RequestMapping(Common.Service.DC3_MANAGER_EVENT_URL_PREFIX)
public class EventApi implements EventClient {

    @Resource
    private EventService eventService;

    @Override
    public R<Page<DriverEvent>> driverEvent(DriverEventDto driverEventDto) {
        try {
            Page<DriverEvent> page = eventService.driverEvent(driverEventDto);
            if (null != page) {
                return R.ok(page);
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.fail();
    }

    @Override
    public R<Page<DeviceEvent>> deviceEvent(DeviceEventDto deviceEventDto) {
        try {
            Page<DeviceEvent> page = eventService.deviceEvent(deviceEventDto);
            if (null != page) {
                return R.ok(page);
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.fail();
    }

}