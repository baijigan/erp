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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF*
 */

package com.njrsun.iot.api.center.auth.feign;

import com.njrsun.iot.api.center.auth.hystrix.TokenClientHystrix;
import com.njrsun.iot.common.bean.Login;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.constant.Common;
import com.njrsun.iot.common.valid.Auth;
import com.njrsun.iot.common.valid.Check;
import com.njrsun.iot.common.valid.Update;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 令牌 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_AUTH_TOKEN_URL_PREFIX, name = Common.Service.DC3_AUTH_SERVICE_NAME, fallbackFactory = TokenClientHystrix.class)
public interface TokenClient {

    /**
     * 生成用户随机 Salt
     *
     * @param login Login
     * @return R<String>
     */
    @PostMapping("/salt")
    R<String> generateSalt(@Validated(Update.class) @RequestBody Login login);

    /**
     * 生成用户 Token 令牌
     *
     * @param login Login
     * @return R<String>
     */
    @PostMapping("/generate")
    R<String> generateToken(@Validated(Auth.class) @RequestBody Login login);


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
     * 检测用户 Token 令牌是否有效
     *
     * @param login Login
     * @return R<Boolean>
     */
    @PostMapping("/check")
    R<Long> checkTokenValid(@Validated(Check.class) @RequestBody Login login);

    /**
     * 注销用户的Token令牌
     *
     * @param login Login
     * @return R<Boolean>
     */
    @PostMapping("/cancel")
    R<Boolean> cancelToken(@Validated(Update.class) @RequestBody Login login);
}
