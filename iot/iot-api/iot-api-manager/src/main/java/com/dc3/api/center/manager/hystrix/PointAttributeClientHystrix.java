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

package com.njrsun.iot.api.center.manager.hystrix;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njrsun.iot.api.center.manager.feign.PointAttributeClient;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.dto.PointAttributeDto;
import com.njrsun.iot.common.model.PointAttribute;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * PointAttributeClientHystrix
 *
 * @author njrsun20240123
 */
@Slf4j
@Component
public class PointAttributeClientHystrix implements FallbackFactory<PointAttributeClient> {

    @Override
    public PointAttributeClient create(Throwable throwable) {
        String message = throwable.getMessage() == null ? "No available server for client: DC3-MANAGER" : throwable.getMessage();
        log.error("Hystrix:{}", message);

        return new PointAttributeClient() {
            @Override
            public R<DriverAttribute> add(DriverAttribute driverAttribute) {
                return R.fail(message);
            }

            @Override
            public R<Boolean> delete(Long id) {
                return R.fail(message);
            }

            @Override
            public R<DriverAttribute> update(DriverAttribute driverAttribute) {
                return R.fail(message);
            }

            @Override
            public R<DriverAttribute> selectById(Long id) {
                return R.fail(message);
            }

            @Override
            public R<List<DriverAttribute>> selectByDriverId(Long id) {
                return R.fail(message);
            }

            @Override
            public R<PointAttribute> add(PointAttribute pointAttribute) {
                return R.fail(message);
            }

            @Override
            public R<Boolean> delete(Long id) {
                return R.fail(message);
            }

            @Override
            public R<PointAttribute> update(PointAttribute pointAttribute) {
                return R.fail(message);
            }

            @Override
            public R<PointAttribute> selectById(Long id) {
                return R.fail(message);
            }

            @Override
            public R<List<PointAttribute>> selectByDriverId(@NotNull Long id) {
                return R.fail(message);
            }

            @Override
            public R<Page<PointAttribute>> list(PointAttributeDto pointAttributeDto) {
                return R.fail(message);
            }

        };
    }
}