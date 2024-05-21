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
 */

package com.njrsun.iot.api.transfer.rtmp.hystrix;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njrsun.iot.api.transfer.rtmp.feign.RtmpClient;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.dto.RtmpDto;
import com.njrsun.iot.common.model.Rtmp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * RtmpClientHystrix
 *
 * @author njrsun20240123
 */
@Slf4j
@Component
public class RtmpClientHystrix implements FallbackFactory<RtmpClient> {

    @Override
    public RtmpClient create(Throwable throwable) {
        String message = throwable.getMessage() == null ? "No available server for client: DC3-RTMP" : throwable.getMessage();
        log.error("Hystrix:{}", message);

        return new RtmpClient() {
            @Override
            public R<Rtmp> add(Rtmp rtmp, Long tenantId) {
                return R.fail(message);
            }

            @Override
            public R<Boolean> delete(Long id) {
                return R.fail(message);
            }

            @Override
            public R<Rtmp> update(Rtmp rtmp, Long tenantId) {
                return R.fail(message);
            }

            @Override
            public R<Rtmp> selectById(Long id) {
                return R.fail(message);
            }

            @Override
            public R<Page<Rtmp>> list(RtmpDto rtmpDto, Long tenantId) {
                return R.fail(message);
            }

            @Override
            public R<Boolean> start(Long id) {
                return R.fail(message);
            }

            @Override
            public R<Boolean> stop(Long id) {
                return R.fail(message);
            }
        };
    }
}