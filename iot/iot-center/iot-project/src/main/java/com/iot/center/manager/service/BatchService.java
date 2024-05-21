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

package  com.iot.center.manager.service;

import com.dc3.common.bean.batch.BatchDriver;
import com.dc3.common.bean.driver.DriverMetadata;

import java.util.List;

/**
 * BatchService Interface
 *
 * @author njrsun20240123
 */
public interface BatchService {

    /**
     * 批量导入
     * <ul>
     *     <li>驱动</li>
     *     <li>模版</li>
     *     <li>驱动配置</li>
     *     <li>位号</li>
     *     <li>设备</li>
     *     <li>位号配置</li>
     * </ul>
     *
     * @param batchDrivers List<BatchDriver>
     */
    void batchImport(List<BatchDriver> batchDrivers);

    /**
     * 批量导出
     * <ul>
     *     <li>驱动</li>
     *     <li>模版</li>
     *     <li>驱动配置</li>
     *     <li>位号</li>
     *     <li>设备</li>
     *     <li>位号配置</li>
     * </ul>
     *
     * @param serviceName Driver Service Name
     * @return BatchDriver
     */
    BatchDriver batchExport(String serviceName);

    /**
     * 获取驱动元数据
     *
     * @param serviceName Driver Service Name
     * @return DriverMetadata
     */
    DriverMetadata batchDriverMetadata(String serviceName);

}
