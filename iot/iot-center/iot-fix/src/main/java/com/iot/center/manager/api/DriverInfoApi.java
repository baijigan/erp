/*
 * Copyright 2020-2024 Njrsun. All Rights Reserved.
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

package  com.iot.center.manager.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dc3.api.center.manager.feign.DriverInfoClient;
import com.iot.center.manager.service.DriverInfoService;
import com.iot.center.manager.service.NotifyService;
import com.dc3.common.bean.R;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.DriverInfoDto;
import com.dc3.common.model.DriverInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 位号配置信息 Client 接口实现
 *
 * @author njrsun20240123
 */
@Slf4j
@RestController
@RequestMapping(Common.Service.DC3_MANAGER_DRIVER_INFO_URL_PREFIX)
public class DriverInfoApi implements DriverInfoClient {

    @Resource
    private DriverInfoService driverInfoService;
    @Resource
    private NotifyService notifyService;

    @Override
    public R<DriverInfo> add(DriverInfo driverInfo) {
        try {
            DriverInfo add = driverInfoService.add(driverInfo);
            if (null != add) {
                notifyService.notifyDriverDriverInfo(Common.Driver.DriverInfo.ADD, add);
                return R.ok(add);
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.fail();
    }

    @Override
    public R<Boolean> delete(Long id) {
        try {
            DriverInfo driverInfo = driverInfoService.selectById(id);
            if (null != driverInfo && driverInfoService.delete(id)) {
                notifyService.notifyDriverDriverInfo(Common.Driver.DriverInfo.DELETE, driverInfo);
                return R.ok();
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.fail();
    }

    @Override
    public R<DriverInfo> update(DriverInfo driverInfo) {
        try {
            DriverInfo update = driverInfoService.update(driverInfo);
            if (null != update) {
                notifyService.notifyDriverDriverInfo(Common.Driver.DriverInfo.UPDATE, update);
                return R.ok(update);
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.fail();
    }

    @Override
    public R<DriverInfo> selectById(Long id) {
        try {
            DriverInfo select = driverInfoService.selectById(id);
            if (null != select) {
                return R.ok(select);
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.fail();
    }

    @Override
    public R<DriverInfo> selectByAttributeIdAndDeviceId(Long attributeId, Long deviceId) {
        try {
            DriverInfo select = driverInfoService.selectByAttributeIdAndDeviceId(attributeId, deviceId);
            if (null != select) {
                return R.ok(select);
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.fail();
    }

    @Override
    public R<List<DriverInfo>> selectByDeviceId(Long deviceId) {
        try {
            List<DriverInfo> select = driverInfoService.selectByDeviceId(deviceId);
            if (null != select) {
                return R.ok(select);
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.fail();
    }

    @Override
    public R<Page<DriverInfo>> list(DriverInfoDto driverInfoDto) {
        try {
            Page<DriverInfo> page = driverInfoService.list(driverInfoDto);
            if (null != page) {
                return R.ok(page);
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.fail();
    }

}
