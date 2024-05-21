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
import com.dc3.common.dto.UserDto;
import com.dc3.common.model.User;

/**
 * User Interface
 *
 * @author njrsun20240123
 */
public interface UserService extends Service<User, UserDto> {

    /**
     * 根据用户名查询用户
     *
     * @param nama Username
     * @return User
     */
    User selectByName(String nama);

    /**
     * 根据手机号查询用户
     *
     * @param phone Phone
     * @return User
     */
    User selectByPhone(String phone);

    /**
     * 根据邮箱查询用户
     *
     * @param email Email
     * @return User
     */
    User selectByEmail(String email);

    /**
     * 根据用户名判断用户是否存在
     *
     * @param name Username
     * @return boolean
     */
    boolean checkUserValid(String name);

    /**
     * 重置密码
     *
     * @param id Id
     * @return boolean
     */
    boolean restPassword(Long id);
}
