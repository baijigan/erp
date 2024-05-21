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
import com.njrsun.iot.api.center.manager.hystrix.PointAttributeClientHystrix;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.constant.Common;
import com.njrsun.iot.common.dto.PointAttributeDto;
import com.njrsun.iot.common.model.PointAttribute;
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
 * 位号配置属性 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_MANAGER_POINT_ATTRIBUTE_URL_PREFIX, name = Common.Service.DC3_MANAGER_SERVICE_NAME, fallbackFactory = PointAttributeClientHystrix.class)
public interface PointAttributeClient {

    /**
     * 新增 PointAttribute
     *
     * @param pointAttribute PointAttribute
     * @return PointAttribute
     */
    @PostMapping("/add")
    R<PointAttribute> add(@Validated(Insert.class) @RequestBody PointAttribute pointAttribute);
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
     * 根据 ID 删除 PointAttribute
     *
     * @param id PointAttribute Id
     * @return Boolean
     */
    @PostMapping("/delete/{id}")
    R<Boolean> delete(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 修改 PointAttribute
     *
     * @param pointAttribute PointAttribute
     * @return PointAttribute
     */
    @PostMapping("/update")
    R<PointAttribute> update(@Validated(Update.class) @RequestBody PointAttribute pointAttribute);

    /**
     * 根据 ID 查询 PointAttribute
     *
     * @param id PointAttribute Id
     * @return PointAttribute
     */
    @GetMapping("/id/{id}")
    R<PointAttribute> selectById(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 根据 驱动ID 查询 PointAttribute
     *
     * @param id PointAttribute Id
     * @return PointAttribute Array
     */
    @GetMapping("/driver_id/{id}")
    R<List<PointAttribute>> selectByDriverId(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 分页查询 PointAttribute
     *
     * @param pointAttributeDto PointAttribute Dto
     * @return Page<PointAttribute>
     */
    @PostMapping("/list")
    R<Page<PointAttribute>> list(@RequestBody(required = false) PointAttributeDto pointAttributeDto);

}
