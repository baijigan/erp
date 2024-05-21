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
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.njrsun.com
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * *
 * limitations under the License.
 */

package com.njrsun.iot.api.center.auth.hystrix;

import com.njrsun.iot.api.center.auth.feign.DictionaryClient;
import com.njrsun.iot.common.bean.Dictionary;
import com.njrsun.iot.common.bean.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DictionaryClientHystrix
 *
 * @author njrsun20240123
 */
@Slf4j
@Component
public class DictionaryClientHystrix implements FallbackFactory<DictionaryClient> {

    @Override
    public DictionaryClient create(Throwable throwable) {
        String message = throwable.getMessage() == null ? "No available server for client: DC3-AUTH" : throwable.getMessage();
        log.error("Hystrix:{}", message);

        return new DictionaryClient() {

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
            public R<List<Dictionary>> tenantDictionary() {
                return R.fail(message);
            }

            @Override
            public R<List<Dictionary>> userDictionary(Long tenantId) {
                return R.fail(message);
            }

            @Override
            public R<List<Dictionary>> blackIpDictionary(Long tenantId) {
                return R.fail(message);
            }

        };
    }
}