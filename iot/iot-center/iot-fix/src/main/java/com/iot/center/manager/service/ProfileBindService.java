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
import com.dc3.common.dto.ProfileBindDto;
import com.dc3.common.model.ProfileBind;

import java.util.Set;

/**
 * ProfileBind Interface
 *
 * @author njrsun20240123
 */
public interface ProfileBindService extends Service<ProfileBind, ProfileBindDto> {

    /**
     * 根据 设备Id 删除关联的模版映射
     *
     * @param deviceId Device Id
     */
    boolean deleteByDeviceId(Long deviceId);

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
     * 根据 模版ID和设备Id 删除关联的模版映射
     *
     * @param profileId Profile Id
     * @param deviceId  Device Id
     */
    boolean deleteByProfileIdAndDeviceId(Long profileId, Long deviceId);

    /**
     * 根据 设备ID 和 模版ID 查询关联的模版映射
     *
     * @param deviceId  Device Id
     * @param profileId Profile Id
     * @return ProfileBind
     */
    ProfileBind selectByDeviceIdAndProfileId(Long deviceId, Long profileId);

    /**
     * 根据 模版ID 查询关联的 设备ID 集合
     *
     * @param profileId Profile Id
     * @return Device Id Set
     */
    Set<Long> selectDeviceIdByProfileId(Long profileId);

    /**
     * 根据 设备ID 查询关联的 模版ID 集合
     *
     * @param deviceId Device Id
     * @return Profile Id Set
     */
    Set<Long> selectProfileIdByDeviceId(Long deviceId);

}
