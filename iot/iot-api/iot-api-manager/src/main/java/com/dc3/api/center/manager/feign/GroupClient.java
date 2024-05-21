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
import com.njrsun.iot.api.center.manager.hystrix.GroupClientHystrix;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.constant.Common;
import com.njrsun.iot.common.dto.GroupDto;
import com.njrsun.iot.common.model.Group;
import com.njrsun.iot.common.valid.Insert;
import com.njrsun.iot.common.valid.Update;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * 分组 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_MANAGER_GROUP_URL_PREFIX, name = Common.Service.DC3_MANAGER_SERVICE_NAME, fallbackFactory = GroupClientHystrix.class)
public interface GroupClient {

    /**
     * 新增 Group
     *
     * @param group Group
     * @return Group
     */
    @PostMapping("/add")
    R<Group> add(@Validated(Insert.class) @RequestBody Group group, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);
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
     * 根据 ID 删除 Group
     *
     * @param id Group Id
     * @return Boolean
     */
    @PostMapping("/delete/{id}")
    R<Boolean> delete(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 修改 Group
     *
     * @param group Group
     * @return Group
     */
    @PostMapping("/update")
    R<Group> update(@Validated(Update.class) @RequestBody Group group, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 根据 ID 查询 Group
     *
     * @param id Group Id
     * @return Group
     */
    @GetMapping("/id/{id}")
    R<Group> selectById(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 分页查询 Group
     *
     * @param groupDto Group Dto
     * @return Page<Group>
     */
    @PostMapping("/list")
    R<Page<Group>> list(@RequestBody(required = false) GroupDto groupDto, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

}
