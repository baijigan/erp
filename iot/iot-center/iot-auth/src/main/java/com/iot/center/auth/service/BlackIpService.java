/*
 * Copyright 2020-2023 Njrsun. All Rights Reserved.
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

package  com.iot.center.auth.service;

import com.dc3.common.base.Service;
import com.dc3.common.dto.BlackIpDto;
import com.dc3.common.model.BlackIp;

/**
 * User Interface
 *
 * @author njrsun20240123
 */
public interface BlackIpService extends Service<BlackIp, BlackIpDto> {
    /**
     * 根据 Ip 查询 BlackIp
     *
     * @param ip IP
     * @return BlackIp
     */
    BlackIp selectByIp(String ip);

    /**
     * 根据 Ip 是否在Ip黑名单列表
     *
     * @param ip IP
     * @return boolean
     */
    boolean checkBlackIpValid(String ip);
}
