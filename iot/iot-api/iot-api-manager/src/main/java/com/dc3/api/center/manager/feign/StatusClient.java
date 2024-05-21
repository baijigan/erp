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

import com.njrsun.iot.api.center.manager.hystrix.StatusClientHystrix;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.constant.Common;
import com.njrsun.iot.common.dto.DeviceDto;
import com.njrsun.iot.common.dto.DriverDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 设备 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_MANAGER_STATUS_URL_PREFIX, name = Common.Service.DC3_MANAGER_SERVICE_NAME, fallbackFactory = StatusClientHystrix.class)
public interface StatusClient {

    /**
     * 查询 Driver 服务状态
     * ONLINE, OFFLINE
     *
     * @param driverDto Driver Dto
     * @return Map<Long, String>
     */
    @PostMapping("/driver")
    R<Map<Long, String>> driverStatus(@RequestBody(required = false) DriverDto driverDto, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 查询 Device 服务状态
     * ONLINE, OFFLINE, MAINTAIN, FAULT
     *
     * @param deviceDto Device Dto
     * @return Map<Long, String>
     */
    @PostMapping("/device")
    R<Map<Long, String>> deviceStatus(@RequestBody(required = false) DeviceDto deviceDto, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 根据 驱动ID 查询 Device 服务状态
     * ONLINE, OFFLINE, MAINTAIN, FAULT
     *
     * @param driverId Driver Id
     * @return Map<Long, String>
     */
    @GetMapping("/device/driver_id/{driverId}")
    R<Map<Long, String>> deviceStatusByDriverId(@NotNull @PathVariable(value = "driverId") Long driverId);
    /**
     * 查询 Driver 服务状态
     * ONLINE, OFFLINE
     *
     * @param driverDto Driver Dto
     * @return Map<Long, String>
     */
    @PostMapping("/driver")
    R<Map<Long, String>> driverStatus(@RequestBody(required = false) DriverDto driverDto, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 查询 Device 服务状态
     * ONLINE, OFFLINE, MAINTAIN, FAULT
     *
     * @param deviceDto Device Dto
     * @return Map<Long, String>
     */
    @PostMapping("/device")
    R<Map<Long, String>> deviceStatus(@RequestBody(required = false) DeviceDto deviceDto, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 根据 模板ID 查询 Device 服务状态
     * ONLINE, OFFLINE, MAINTAIN, FAULT
     *
     * @param profileId Profile Id
     * @return Map<Long, String>
     */
    @GetMapping("/device/profile_id/{profileId}")
    R<Map<Long, String>> deviceStatusByProfileId(@NotNull @PathVariable(value = "profileId") Long profileId);

}
