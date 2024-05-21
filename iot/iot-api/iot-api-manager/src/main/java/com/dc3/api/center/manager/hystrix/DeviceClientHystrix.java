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
import com.njrsun.iot.api.center.manager.feign.DeviceClient;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.dto.DeviceDto;
import com.njrsun.iot.common.model.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DeviceClientHystrix
 *
 * @author njrsun20240123
 */
@Slf4j
@Component
public class DeviceClientHystrix implements FallbackFactory<DeviceClient> {

    @Override
    public DeviceClient create(Throwable throwable) {
        String message = throwable.getMessage() == null ? "No available server for client: DC3-MANAGER" : throwable.getMessage();
        log.error("Hystrix:{}", message);

        return new DeviceClient() {

            @Override
            public R<Device> add(Device device, Long tenantId) {
                return R.fail(message);
            }

            @Override
            public R<Boolean> delete(Long id) {
                return R.fail(message);
            }

            @Override
            public R<Device> update(Device device, Long tenantId) {
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
            public R<Device> selectById(Long id) {
                return R.fail(message);
            }

            @Override
            public R<List<Device>> selectByDriverId(Long driverId) {
                return R.fail(message);
            }

            @Override
            public R<List<Device>> selectByProfileId(Long profileId) {
                return R.fail(message);
            }

            @Override
            public R<Page<Device>> list(DeviceDto deviceDto, Long tenantId) {
                return R.fail(message);
            }

        };
    }
}