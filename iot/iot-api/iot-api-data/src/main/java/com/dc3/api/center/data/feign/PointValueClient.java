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

package com.njrsun.iot.api.center.data.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njrsun.iot.api.center.data.hystrix.PointValueClientHystrix;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.constant.Common;
import com.njrsun.iot.common.dto.PointValueDto;
import com.njrsun.iot.common.bean.point.PointValue;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 数据 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_DATA_POINT_VALUE_URL_PREFIX, name = Common.Service.DC3_DATA_SERVICE_NAME, fallbackFactory = PointValueClientHystrix.class)
public interface PointValueClient {

    /**
     * 查询最新 PointValue 集合
     *
     * @param deviceId Device Id
     * @return PointValue Array
     */
    @GetMapping("/latest/device_id/{deviceId}")
    R<List<PointValue>> latest(@NotNull @PathVariable(value = "deviceId") Long deviceId, @RequestParam(required = false, defaultValue = "false") Boolean history);

    /**
     * 查询最新 PointValue
     *
     * @param deviceId Device Id
     * @param pointId  Point Id
     * @return PointValue
     */
    @GetMapping("/latest/device_id/{deviceId}/point_id/{pointId}")
    R<PointValue> latest(@NotNull @PathVariable(value = "deviceId") Long deviceId, @NotNull @PathVariable(value = "pointId") Long pointId, @RequestParam(required = false, defaultValue = "false") Boolean history);

    /**
     * 分页查询 PointValue
     *
     * @param pointValueDto PointValueDto
     * @return Page<PointValue>
     */
    @PostMapping("/list")
    R<Page<PointValue>> list(@RequestBody(required = false) PointValueDto pointValueDto);
}
