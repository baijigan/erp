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

import com.dc3.common.base.Service;
import com.dc3.common.dto.PointAttributeDto;
import com.dc3.common.model.PointAttribute;

import java.util.List;

/**
 * PointAttribute Interface
 *
 * @author njrsun20240123
 */
public interface PointAttributeService extends Service<PointAttribute, PointAttributeDto> {
    /**
     * 根据位号配置属性 NAME 和 驱动 ID 查询
     *
     * @param name
     * @param driverId
     * @return PointAttribute
     */
    PointAttribute selectByNameAndDriverId(String name, Long driverId);

    /**
     * 根据 模版Id集 查询模版
     *
     * @param ids Profile Id Set
     * @return Profile Array
     */
    List<Profile> selectByIds(Set<Long> ids);

    /**
     * 根据 设备Id 查询模版
     *
     * @param deviceId Device Id
     * @return Profile Array
     */
    List<Profile> selectByDeviceId(Long deviceId);

    /**
     * 根据驱动 ID 查询
     *
     * @param driverId
     * @return PointAttribute Array
     */
    List<PointAttribute> selectByDriverId(Long driverId);
}
