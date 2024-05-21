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
import com.njrsun.iot.api.center.manager.hystrix.DeviceClientHystrix;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.constant.Common;
import com.njrsun.iot.common.dto.DeviceDto;
import com.njrsun.iot.common.model.Device;
import com.njrsun.iot.common.valid.Insert;
import com.njrsun.iot.common.valid.Update;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 设备 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_MANAGER_DEVICE_URL_PREFIX, name = Common.Service.DC3_MANAGER_SERVICE_NAME, fallbackFactory = DeviceClientHystrix.class)
public interface DeviceClient {

    /**
     * 新增 Device
     *
     * @param device Device
     * @return R<Device>
     */
    @PostMapping("/add")
    R<Device> add(@Validated(Insert.class) @RequestBody Device device, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 根据 ID 删除 Device
     *
     * @param id Device Id
     * @return R<Boolean>
     */
    @PostMapping("/delete/{id}")
    R<Boolean> delete(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 修改 Device
     *
     * @param device Device
     * @return R<Device>
     */
    @PostMapping("/update")
    R<Device> update(@Validated(Update.class) @RequestBody Device device, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 根据 ID 查询 Device
     *
     * @param id Device Id
     * @return R<Device>
     */
    @GetMapping("/id/{id}")
    R<Device> selectById(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 根据 驱动ID 查询 Device
     *
     * @param driverId Driver Id
     * @return R<Device>
     */
    @GetMapping("/driver_id/{driverId}")
    R<List<Device>> selectByDriverId(@NotNull @PathVariable(value = "driverId") Long driverId);

    /**
     * 根据 模板ID 查询 Device
     *
     * @param profileId Profile Id
     * @return R<Device>
     */
    @GetMapping("/profile_id/{profileId}")
    R<List<Device>> selectByProfileId(@NotNull @PathVariable(value = "profileId") Long profileId);

    /**
     * 分页查询 Device
     *
     * @param deviceDto Device Dto
     * @return R<Page < Device>>
     */
    @PostMapping("/list")
    R<Page<Device>> list(@RequestBody(required = false) DeviceDto deviceDto, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

}
