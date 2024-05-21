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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.*
 */

package com.njrsun.iot.api.center.auth.hystrix;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njrsun.iot.api.center.auth.feign.BlackIpClient;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.dto.BlackIpDto;
import com.njrsun.iot.common.model.BlackIp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * BlackIpClientHystrix
 *
 * @author njrsun20240123
 */
@Slf4j
@Component
public class BlackIpClientHystrix implements FallbackFactory<BlackIpClient> {

    @Override
    public BlackIpClient create(Throwable throwable) {
        String message = throwable.getMessage() == null ? "No available server for client: DC3-AUTH" : throwable.getMessage();
        log.error("Hystrix:{}", message);

        return new BlackIpClient() {

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
            public R<BlackIp> selectById(Long id) {
                return R.fail(message);
            }

            @Override
            public R<BlackIp> selectByIp(String ip) {
                return R.fail(message);
            }

            @Override
            public R<Page<BlackIp>> list(BlackIpDto blackIpDto) {
                return R.fail(message);
            }

            @Override
            public R<Boolean> checkBlackIpValid(String ip) {
                return R.fail(message);
            }
        };
    }
}