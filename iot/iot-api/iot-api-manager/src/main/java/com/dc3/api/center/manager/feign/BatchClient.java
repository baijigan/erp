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

import com.njrsun.iot.api.center.manager.hystrix.BatchClientHystrix;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.bean.batch.BatchDriver;
import com.njrsun.iot.common.constant.Common;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量导入 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_MANAGER_BATCH_URL_PREFIX, name = Common.Service.DC3_MANAGER_SERVICE_NAME, fallbackFactory = BatchClientHystrix.class)
public interface BatchClient {

    /**
     * 批量导入，包含：驱动->模版->驱动配置->位号->设备->位号配置
     *
     * @param multipartFile MultipartFile
     * @return Boolean
     */
    @PostMapping("/import")
    R<Boolean> batchImport(@RequestParam(value = "file") MultipartFile multipartFile);

    /**
     * 批量导入，包含：驱动->模版->驱动配置->位号->设备->位号配置
     *
     * @param batchDrivers List<BatchDriver>
     * @return Boolean
     */
    @PostMapping("/import/batch_driver")
    R<Boolean> batchImport(@RequestBody List<BatchDriver> batchDrivers);

    /**
     * 批量导出，包含：驱动->模版->驱动配置->位号->设备->位号配置
     *
     * @param serviceName 驱动服务名称
     * @return BatchDriver
     */
    @GetMapping("/export/{serviceName}")
    R<BatchDriver> batchExport(@NotNull @PathVariable(value = "serviceName") String serviceName);

}
