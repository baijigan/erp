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

import com.dc3.api.center.manager.feign.StatusClient;
import com.iot.center.manager.service.StatusService;
import com.dc3.common.bean.R;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.DeviceDto;
import com.dc3.common.dto.DriverDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 设备 Client 接口实现
 *
 * @author njrsun20240123
 */
@Slf4j
@RestController
@RequestMapping(Common.Service.DC3_MANAGER_STATUS_URL_PREFIX)
public class StatusApi implements StatusClient {

    @Resource
    private StatusService statusService;

    @Override
    public R<Map<Long, String>> driverStatus(DriverDto driverDto, Long tenantId) {
        try {
            driverDto.setTenantId(tenantId);
            Map<Long, String> statuses = statusService.driver(driverDto);
            return R.ok(statuses);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @Override
    public R<Map<Long, String>> deviceStatus(DeviceDto deviceDto, Long tenantId) {
        try {
            deviceDto.setTenantId(tenantId);
            Map<Long, String> statuses = statusService.device(deviceDto);
            return R.ok(statuses);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @Override
    public R<Map<Long, String>> deviceStatusByDriverId(Long driverId) {
        try {
            DeviceDto deviceDto = new DeviceDto();
            deviceDto.setDriverId(driverId);
            Map<Long, String> statuses = statusService.device(deviceDto);
            return R.ok(statuses);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @Override
    public R<Map<Long, String>> deviceStatusByProfileId(Long profileId) {
        try {
            Map<Long, String> statuses = statusService.deviceByProfileId(profileId);
            return R.ok(statuses);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

}
