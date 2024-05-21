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

import com.dc3.common.bean.Dictionary;

import java.util.List;

/**
 * Dictionary Interface
 *
 * @author njrsun20240123
 */
public interface DictionaryService {
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
     * 获取租户驱动字典
     *
     * @return Dictionary Array
     */
    List<Dictionary> driverDictionary(Long tenantId);

    /**
     * 获取驱动配置属性字典
     *
     * @return Dictionary Array
     */
    List<Dictionary> driverAttributeDictionary(Long tenantId);

    /**
     * 获取位号配置属性字典
     *
     * @return Dictionary Array
     */
    List<Dictionary> pointAttributeDictionary(Long tenantId);

    /**
     * 获取租户模板字典
     *
     * @return Dictionary Array
     */
    List<Dictionary> profileDictionary(Long tenantId);

    /**
     * 获取租户驱动下设备字典
     *
     * @return Dictionary Array
     */
    List<Dictionary> deviceDictionary(Long tenantId);

    /**
     * 获取租户模板、设备位号字典
     * profile/device
     *
     * @param parent
     * @return Dictionary Array
     */
    List<Dictionary> pointDictionary(String parent, Long tenantId);

}
