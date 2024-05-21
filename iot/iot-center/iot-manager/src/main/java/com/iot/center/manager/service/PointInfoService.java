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
import com.dc3.common.dto.PointInfoDto;
import com.dc3.common.model.PointInfo;

import java.util.List;

/**
 * PointInfo Interface
 *
 * @author njrsun20240123
 */
public interface PointInfoService extends Service<PointInfo, PointInfoDto> {

    /**
     * 根据位号配置信息 ID & 设备 ID & 位号 ID 查询
     *
     * @param pointAttributeId Point Attribute Id
     * @param deviceId         Device Id
     * @param pointId          Point Id
     * @return PointInfo
     */
    PointInfo selectByAttributeIdAndDeviceIdAndPointId(Long pointAttributeId, Long deviceId, Long pointId);

    /**
     * 根据位号配置信息 ID 查询
     *
     * @param pointAttributeId Point Attribute Id
     * @return PointInfo Array
     */
    List<PointInfo> selectByAttributeId(Long pointAttributeId);

    /**
     * 根据 设备 ID 查询
     *
     * @param deviceId Device Id
     * @return PointInfo Array
     */
    List<PointInfo> selectByDeviceId(Long deviceId);

    /**
     * 根据 设备 ID & 位号 ID 查询
     *
     * @param deviceId Device Id
     * @param pointId  Point Id
     * @return PointInfo Array
     */
    List<PointInfo> selectByDeviceIdAndPointId(Long deviceId, Long pointId);
}
