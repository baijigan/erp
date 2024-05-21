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
import com.njrsun.iot.api.center.manager.hystrix.PointInfoClientHystrix;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.constant.Common;
import com.njrsun.iot.common.dto.PointInfoDto;
import com.njrsun.iot.common.model.PointInfo;
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
 * 位号配置信息 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_MANAGER_POINT_INFO_URL_PREFIX, name = Common.Service.DC3_MANAGER_SERVICE_NAME, fallbackFactory = PointInfoClientHystrix.class)
public interface PointInfoClient {

    /**
     * 新增 PointInfo
     *
     * @param pointInfo PointInfo
     * @return PointInfo
     */
    @PostMapping("/add")
    R<PointInfo> add(@Validated(Insert.class) @RequestBody PointInfo pointInfo);
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
     * 根据 ID 删除 PointInfo
     *
     * @param id PointInfo Id
     * @return Boolean
     */
    @PostMapping("/delete/{id}")
    R<Boolean> delete(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 修改 PointInfo
     *
     * @param pointInfo PointInfo
     * @return PointInfo
     */
    @PostMapping("/update")
    R<PointInfo> update(@Validated(Update.class) @RequestBody PointInfo pointInfo);

    /**
     * 根据 ID 查询 PointInfo
     *
     * @param id PointInfo Id
     * @return PointInfo
     */
    @GetMapping("/id/{id}")
    R<PointInfo> selectById(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 根据 属性ID、设备ID 和 位号ID 查询 PointInfo
     *
     * @param attributeId Attribute Id
     * @param deviceId    Device Id
     * @param pointId     Point Id
     * @return PointInfo
     */
    @GetMapping("/attribute_id/{attributeId}/device_id/{deviceId}/point_id/{pointId}")
    R<PointInfo> selectByAttributeIdAndDeviceIdAndPointId(@NotNull @PathVariable(value = "attributeId") Long attributeId, @NotNull @PathVariable(value = "deviceId") Long deviceId, @NotNull @PathVariable(value = "pointId") Long pointId);

    /**
     * 根据 设备ID 和 位号ID 查询 PointInfo
     *
     * @param deviceId Device Id
     * @param pointId  Point Id
     * @return PointInfo
     */
    @GetMapping("/device_id/{deviceId}/point_id/{pointId}")
    R<List<PointInfo>> selectByDeviceIdAndPointId(@NotNull @PathVariable(value = "deviceId") Long deviceId, @NotNull @PathVariable(value = "pointId") Long pointId);

    /**
     * 根据 设备ID 查询 PointInfo
     *
     * @param deviceId Device Id
     * @return PointInfo
     */
    @GetMapping("/device_id/{deviceId}")
    R<List<PointInfo>> selectByDeviceId(@NotNull @PathVariable(value = "deviceId") Long deviceId);

    /**
     * 分页查询 PointInfo
     *
     * @param pointInfoDto PointInfo Dto
     * @return Page<PointInfo>
     */
    @PostMapping("/list")
    R<Page<PointInfo>> list(@RequestBody(required = false) PointInfoDto pointInfoDto);

}
