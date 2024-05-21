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

package com.njrsun.iot.api.center.manager.feign;

import com.njrsun.iot.api.center.manager.hystrix.DictionaryClientHystrix;
import com.njrsun.iot.common.bean.Dictionary;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.constant.Common;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 字典 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_MANAGER_DICTIONARY_URL_PREFIX, name = Common.Service.DC3_MANAGER_SERVICE_NAME, fallbackFactory = DictionaryClientHystrix.class)
public interface DictionaryClient {

    /**
     * 查询驱动 Dictionary
     *
     * @return List<Dictionary>
     */
    @GetMapping("/driver")
    R<List<Dictionary>> driverDictionary(@RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 查询驱动属性 Dictionary
     *
     * @return List<Dictionary>
     */
    @GetMapping("/driver_attribute")
    R<List<Dictionary>> driverAttributeDictionary(@RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 查询位号属性 Dictionary
     *
     * @return List<Dictionary>
     */
    @GetMapping("/point_attribute")
    R<List<Dictionary>> pointAttributeDictionary(@RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 查询模板 Dictionary
     *
     * @return List<Dictionary>
     */
    @GetMapping("/profile")
    R<List<Dictionary>> profileDictionary(@RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 查询设备 Dictionary
     *
     * @return List<Dictionary>
     */
    @GetMapping("/device")
    R<List<Dictionary>> deviceDictionary(@RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

    /**
     * 查询位号 Dictionary
     *
     * @param parent profile/device
     * @return List<Dictionary>
     */
    @GetMapping("/point/{parent}")
    R<List<Dictionary>> pointDictionary(@NotNull @PathVariable("parent") String parent, @RequestHeader(value = Common.Service.DC3_AUTH_TENANT_ID, defaultValue = "-1") Long tenantId);

}
