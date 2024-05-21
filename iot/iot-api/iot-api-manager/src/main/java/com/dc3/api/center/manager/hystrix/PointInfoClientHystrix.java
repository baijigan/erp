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
import com.njrsun.iot.api.center.manager.feign.PointInfoClient;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.dto.PointInfoDto;
import com.njrsun.iot.common.model.PointInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PointInfoClientHystrix
 *
 * @author njrsun20240123
 */
@Slf4j
@Component
public class PointInfoClientHystrix implements FallbackFactory<PointInfoClient> {

    @Override
    public PointInfoClient create(Throwable throwable) {
        String message = throwable.getMessage() == null ? "No available server for client: DC3-MANAGER" : throwable.getMessage();
        log.error("Hystrix:{}", message);

        return new PointInfoClient() {

            @Override
            public R<PointInfo> add(PointInfo pointInfo) {
                return R.fail(message);
            }

            @Override
            public R<Boolean> delete(Long id) {
                return R.fail(message);
            }

            @Override
            public R<PointInfo> update(PointInfo pointInfo) {
                return R.fail(message);
            }
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
            public R<PointInfo> selectById(Long id) {
                return R.fail(message);
            }

            @Override
            public R<PointInfo> selectByAttributeIdAndDeviceIdAndPointId(Long attributeId, Long deviceId, Long pointId) {
                return R.fail(message);
            }

            @Override
            public R<List<PointInfo>> selectByDeviceIdAndPointId(Long deviceId, Long pointId) {
                return R.fail(message);
            }

            @Override
            public R<List<PointInfo>> selectByDeviceId(Long deviceId) {
                return R.fail(message);
            }

            @Override
            public R<Page<PointInfo>> list(PointInfoDto pointInfoDto) {
                return R.fail(message);
            }

        };
    }
}