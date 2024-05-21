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
import com.njrsun.iot.api.center.manager.hystrix.DriverClientHystrix;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.constant.Common;
import com.njrsun.iot.common.dto.DriverDto;
import com.njrsun.iot.common.model.Driver;
import com.njrsun.iot.common.valid.Insert;
import com.njrsun.iot.common.valid.Update;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * 驱动 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_MANAGER_DRIVER_URL_PREFIX, name = Common.Service.DC3_MANAGER_SERVICE_NAME, fallbackFactory = DriverClientHystrix.class)
public interface DriverClient {

    /**
     * 新增 Driver
     *
     * @param driver Driver
     * @return Driver
     */
    @PostMapping("/add")
    R<Driver> add(@Validated(Insert.class) @RequestBody Driver driver, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 根据 ID 删除 Driver
     *
     * @param id Driver Id
     * @return Boolean
     */
    @PostMapping("/delete/{id}")
    R<Boolean> delete(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 修改 Driver
     *
     * @param driver Driver
     * @return Driver
     */
    @PostMapping("/update")
    R<Driver> update(@Validated(Update.class) @RequestBody Driver driver, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 根据 ID 查询 Driver
     *
     * @param id Driver Id
     * @return Driver
     */
    @GetMapping("/id/{id}")
    R<Driver> selectById(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 根据 SERVICENAME 查询 Driver
     *
     * @param serviceName Driver Service Name
     * @return Driver
     */
    @GetMapping("/service/{serviceName}")
    R<Driver> selectByServiceName(@NotNull @PathVariable(value = "serviceName") String serviceName);

    /**
     * 根据 TYPE & HOST & PORT 查询 Driver
     *
     * @param type Driver type
     * @param host Driver Host
     * @param port Driver Port
     * @return Driver
     */
    @GetMapping("/type/{type}/host/{host}/port/{port}")
    R<Driver> selectByHostPort(@NotNull @PathVariable(value = "type") String type, @NotNull @PathVariable(value = "host") String host, @NotNull @PathVariable(value = "port") Integer port, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 分页查询 Driver
     *
     * @param driverDto Driver Dto
     * @return Page<Driver>
     */
    @PostMapping("/list")
    R<Page<Driver>> list(@RequestBody(required = false) DriverDto driverDto, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

}
