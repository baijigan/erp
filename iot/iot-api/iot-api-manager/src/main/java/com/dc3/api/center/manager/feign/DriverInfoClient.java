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
import com.njrsun.iot.api.center.manager.hystrix.DriverInfoClientHystrix;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.constant.Common;
import com.njrsun.iot.common.dto.DriverInfoDto;
import com.njrsun.iot.common.model.DriverInfo;
import com.njrsun.iot.common.valid.Insert;
import com.njrsun.iot.common.valid.Update;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 驱动配置信息 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_MANAGER_DRIVER_INFO_URL_PREFIX, name = Common.Service.DC3_MANAGER_SERVICE_NAME, fallbackFactory = DriverInfoClientHystrix.class)
public interface DriverInfoClient {

    /**
     * 新增 DriverInfo
     *
     * @param driverInfo DriverInfo
     * @return DriverInfo
     */
    @PostMapping("/add")
    R<DriverInfo> add(@Validated(Insert.class) @RequestBody DriverInfo driverInfo);

    /**
     * 根据 ID 删除 DriverInfo
     *
     * @param id DriverInfo Id
     * @return Boolean
     */
    @PostMapping("/delete/{id}")
    R<Boolean> delete(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 修改 DriverInfo
     *
     * @param driverInfo DriverInfo
     * @return DriverInfo
     */
    @PostMapping("/update")
    R<DriverInfo> update(@Validated(Update.class) @RequestBody DriverInfo driverInfo);

    /**
     * 根据 ID 查询 DriverInfo
     *
     * @param id DriverInfo Id
     * @return DriverInfo
     */
    @GetMapping("/id/{id}")
    R<DriverInfo> selectById(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 根据 属性ID 和 设备ID 查询 DriverInfo
     *
     * @param attributeId Attribute Id
     * @param deviceId    Device Id
     * @return DriverInfo
     */
    @GetMapping("/attribute_id/{attributeId}/device_id/{deviceId}")
    R<DriverInfo> selectByAttributeIdAndDeviceId(@NotNull @PathVariable(value = "attributeId") Long attributeId, @NotNull @PathVariable(value = "deviceId") Long deviceId);

    /**
     * 根据 设备ID 查询 DriverInfo
     *
     * @param deviceId Device Id
     * @return DriverInfo Array
     */
    @GetMapping("/device_id/{deviceId}")
    R<List<DriverInfo>> selectByDeviceId(@NotNull @PathVariable(value = "deviceId") Long deviceId);

    /**
     * 分页查询 DriverInfo
     *
     * @param driverInfoDto DriverInfo Dto
     * @return Page<DriverInfo>
     */
    @PostMapping("/list")
    R<Page<DriverInfo>> list(@RequestBody(required = false) DriverInfoDto driverInfoDto);

}
