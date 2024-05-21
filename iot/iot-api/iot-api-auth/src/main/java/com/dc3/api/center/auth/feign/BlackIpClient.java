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

package com.njrsun.iot.api.center.auth.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njrsun.iot.api.center.auth.hystrix.BlackIpClientHystrix;
import com.njrsun.iot.common.bean.R;
import com.njrsun.iot.common.constant.Common;
import com.njrsun.iot.common.dto.BlackIpDto;
import com.njrsun.iot.common.model.BlackIp;
import com.njrsun.iot.common.valid.Insert;
import com.njrsun.iot.common.valid.Update;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotNull;

/**
 * Ip 黑名单 FeignClient
 *
 * @author njrsun20240123
 */
@FeignClient(path = Common.Service.DC3_AUTH_BLACK_IP_URL_PREFIX, name = Common.Service.DC3_AUTH_SERVICE_NAME, fallbackFactory = BlackIpClientHystrix.class)
public interface BlackIpClient {

    /**
     * 新增 BlackIp
     *
     * @param blackIp  BlackIp
     * @return BlackIp
     */
    @PostMapping("/add")
    R<BlackIp> add(@Validated(Insert.class) @RequestBody BlackIp blackIp);

    /**
     * 根据 ID 删除 BlackIp
     *
     * @param id BlackIp Id
     * @return Boolean
     */
    @PostMapping("/delete/{id}")
    R<Boolean> delete(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 修改 BlackIp
     * <p>
     * 支  持: Enable
     * 不支持: Ip
     *
     * @param blackIp  BlackIp
     * @return BlackIp
     */
    @PostMapping("/update")
    R<BlackIp> update(@Validated(Update.class) @RequestBody BlackIp blackIp);

    /**
     * 新增 BlackIp
     *
     * @param blackIp  BlackIp
     * @return BlackIp
     */
    @PostMapping("/add")
    R<BlackIp> add(@Validated(Insert.class) @RequestBody BlackIp blackIp);

    /**
     * 根据 ID 删除 BlackIp
     *
     * @param id BlackIp Id
     * @return Boolean
     */
    @PostMapping("/delete/{id}")
    R<Boolean> delete(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 根据 ID 查询 BlackIp
     *
     * @param id BlackIp Id
     * @return BlackIp
     */
    @GetMapping("/id/{id}")
    R<BlackIp> selectById(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 根据 Ip 查询 BlackIp
     *
     * @param ip       Black Ip
     * @return BlackIp
     */
    @GetMapping("/ip/{ip}")
    R<BlackIp> selectByIp(@NotNull @PathVariable(value = "ip") String ip);

    /**
     * 分页查询 BlackIp
     *
     * @param blackIpDto Dto
     * @return Page<BlackIp>
     */
    @PostMapping("/list")
    R<Page<BlackIp>> list(@RequestBody(required = false) BlackIpDto blackIpDto);

    /**
     * 检测 Ip 是否在 Ip 黑名单列表
     *
     * @param ip       Black Ip
     * @return Boolean
     */
    @GetMapping("/check/{ip}")
    R<Boolean> checkBlackIpValid(@NotNull @PathVariable(value = "ip") String ip);

}
