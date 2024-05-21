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

package com.njrsun.iot.api.center.data.hystrix;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njrsun.iot.api.center.data.feign.PointValueClient;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.dto.PointValueDto;
import com.njrsun.iot.common.bean.point.PointValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PointValueClientHystrix
 *
 * @author njrsun20240123
 */
@Slf4j
@Component
public class PointValueClientHystrix implements FallbackFactory<PointValueClient> {

    @Override
    public PointValueClient create(Throwable throwable) {
        String message = throwable.getMessage() == null ? "No available server for client: DC3-DATA" : throwable.getMessage();
        log.error("Hystrix:{}", message);

        return new PointValueClient() {

            @Override
            public R<List<PointValue>> latest(Long deviceId, Boolean history) {
                return R.fail(message);
            }

            @Override
            public R<PointValue> latest(Long deviceId, Long pointId, Boolean history) {
                return R.fail(message);
            }

            @Override
            public R<Page<PointValue>> list(PointValueDto pointValueDto) {
                return R.fail(message);
            }

        };
    }
}