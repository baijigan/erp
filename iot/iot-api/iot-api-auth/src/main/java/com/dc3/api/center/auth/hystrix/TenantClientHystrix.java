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
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.njrsun.com
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.njrsun.iot.api.center.auth.hystrix;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njrsun.iot.api.center.auth.feign.TenantClient;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.dto.TenantDto;
import com.njrsun.iot.common.model.Tenant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * TenantClientHystrix
 *
 * @author njrsun20240123
 */
@Slf4j
@Component
public class TenantClientHystrix implements FallbackFactory<TenantClient> {

    @Override
    public TenantClient create(Throwable throwable) {
        String message = throwable.getMessage() == null ? "No available server for client: DC3-AUTH" : throwable.getMessage();
        log.error("Hystrix:{}", message);

        return new TenantClient() {

            @Override
            public R<Tenant> add(Tenant user) {
                return R.fail(message);
            }

            @Override
            public R<Boolean> delete(Long id) {
                return R.fail(message);
            }

            @Override
            public R<Tenant> update(Tenant user) {
                return R.fail(message);
            }

            @Override
            public R<BlackIp> add(BlackIp blackIp) {
                return R.fail(message);
            }

            @Override
            public R<Boolean> delete(Long id) {
                return R.fail(message);
            }

            @Override
            public R<BlackIp> update(BlackIp blackIp) {
                return R.fail(message);
            }

            @Override
            public R<Tenant> selectById(Long id) {
                return R.fail(message);
            }

            @Override
            public R<Tenant> selectByName(String name) {
                return R.fail(message);
            }

            @Override
            public R<Page<Tenant>> list(TenantDto userDto) {
                return R.fail(message);
            }
        };
    }
}