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

package  com.iot.center.auth.api;

import com.dc3.api.center.auth.feign.TokenClient;
import com.iot.center.auth.bean.TokenValid;
import com.iot.center.auth.service.TokenService;
import com.dc3.common.bean.Login;
import com.dc3.common.bean.R;
import com.dc3.common.constant.Common;
import com.dc3.common.exception.UnAuthorizedException;
import com.dc3.common.utils.Dc3Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 令牌 Feign Client 接口实现
 *
 * @author njrsun20240123
 */
@Slf4j
@RestController
@RequestMapping(Common.Service.DC3_AUTH_TOKEN_URL_PREFIX)
public class TokenApi implements TokenClient {

    @Resource
    private TokenService tokenService;

    @Override
    public R<String> generateSalt(Login login) {
        String salt = tokenService.generateSalt(login.getName());
        return null != salt ? R.ok(salt, "The salt will expire in 5 minutes") : R.fail();
    }

    @Override
    public R<String> generateToken(Login login) {
        String token = tokenService.generateToken(login.getTenant(), login.getName(), login.getSalt(), login.getPassword());
        return null != token ? R.ok(token, "The token will expire in 12 hours.") : R.fail();
    }

    @Override
    public R<Long> checkTokenValid(Login login) {
        TokenValid tokenValid = tokenService.checkTokenValid(login.getName(), login.getSalt(), login.getToken());
        if (tokenValid.isValid()) {
            return R.ok(tokenValid.getExpireTime().getTime(), "The token will expire in " + Dc3Util.formatData(tokenValid.getExpireTime()));
        }
        throw new UnAuthorizedException("Token invalid");
    }

    @Override
    public R<Boolean> cancelToken(Login login) {
        return tokenService.cancelToken(login.getName()) ? R.ok() : R.fail();
    }

    /*static void main(String[] args) {
        System.out.println(Dc3Util.md5("10e339be1130a90dc1b9ff0332abced6" + "dsb785i4ikx0h4wt"));
    }*/
}
