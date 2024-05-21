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

import com.iot.center.auth.bean.TokenValid;

/**
 * Token Interface
 *
 * @author njrsun20240123
 */
public interface TokenService {
    /**
     * 生成用户的随机 salt
     *
     * @param username Username
     * @return String
     */
    String generateSalt(String username);

    /**
     * 生成用户的Token令牌
     *
     * @param tenant   Tenant
     * @param name     User Name
     * @param salt     User Salt
     * @param password User Password
     * @return String
     */
    String generateToken(String tenant, String name, String salt, String password);

    /**
     * 校验用户的Token令牌是否有效
     *
     * @param username Username
     * @param token    Token
     * @return TokenValid
     */
    TokenValid checkTokenValid(String username, String salt, String token);

    /**
     * 注销用户的Token令牌
     *
     * @param username Username
     */
    boolean cancelToken(String username);
}
