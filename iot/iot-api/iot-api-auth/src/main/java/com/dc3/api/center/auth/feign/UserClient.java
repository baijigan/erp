/*
 * Copyright 2018-2023 nanjing rising sun software. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.njrsun.com
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.njrsun.iot.api.center.auth.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njrsun.iot.api.center.auth.hystrix.UserClientHystrix;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.constant.Common;
import com.njrsun.iot.common.dto.UserDto;
import com.njrsun.iot.common.model.User;
import com.njrsun.iot.common.valid.Insert;
import com.njrsun.iot.common.valid.Update;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotNull;

/**
 * 用户 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_AUTH_USER_URL_PREFIX, name = Common.Service.DC3_AUTH_SERVICE_NAME, fallbackFactory = UserClientHystrix.class)
public interface UserClient {

    /**
     * 新增 User
     *
     * @param user User
     * @return User
     */
    @PostMapping("/add")
    R<User> add(@Validated(Insert.class) @RequestBody User user);

    /**
     * 根据 ID 删除 User
     *
     * @param id User Id
     * @return Boolean
     */
    @PostMapping("/delete/{id}")
    R<Boolean> delete(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 查询租户 Dictionary
     *
     * @return List<Dictionary>
     */
    @GetMapping("/tenant")
    R<List<Dictionary>> tenantDictionary();

    /**
     * 查询用户 Dictionary
     *
     * @return List<Dictionary>
     */
    @GetMapping("/user")
    R<List<Dictionary>> userDictionary(@RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);


    /**
     * 修改 User
     * <p>
     * 支  持: Enable,Password
     * 不支持: Name
     *
     * @param user User
     * @return User
     */
    @PostMapping("/update")
    R<User> update(@Validated(Update.class) @RequestBody User user);

    /**
     * 根据 ID 重置 User 密码
     *
     * @param id User Id
     * @return Boolean
     */
    @PostMapping("/reset/{id}")
    R<Boolean> restPassword(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 根据 ID 查询 User
     *
     * @param id User Id
     * @return User
     */
    @GetMapping("/id/{id}")
    R<User> selectById(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 根据 Name 查询 User
     *
     * @param name User Name
     * @return User
     */
    @GetMapping("/name/{name}")
    R<User> selectByName(@NotNull @PathVariable(value = "name") String name);

    /**
     * 分页查询 User
     *
     * @param userDto Dto
     * @return Page<User>
     */
    @PostMapping("/list")
    R<Page<User>> list(@RequestBody(required = false) UserDto userDto);

    /**
     * 检测用户是否存在
     *
     * @param name User Name
     * @return Boolean
     */
    @GetMapping("/check/{name}")
    R<Boolean> checkUserValid(@NotNull @PathVariable(value = "name") String name);

}
