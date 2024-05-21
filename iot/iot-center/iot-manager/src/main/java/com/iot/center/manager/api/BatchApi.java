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

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.dc3.api.center.manager.feign.BatchClient;
import com.iot.center.manager.service.BatchService;
import com.dc3.common.bean.R;
import com.dc3.common.bean.batch.BatchDriver;
import com.dc3.common.constant.Common;
import com.dc3.common.exception.ServiceException;
import com.dc3.common.utils.Dc3Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 批量导入 Client 接口实现
 *
 * @author njrsun20240123
 */
@Slf4j
@RestController
@RequestMapping(Common.Service.DC3_MANAGER_BATCH_URL_PREFIX)
public class BatchApi implements BatchClient {

    @Resource
    private BatchService batchService;


    @Override
    public R<Boolean> batchImport(MultipartFile multipartFile) {
        try {
            if (multipartFile.isEmpty()) {
                throw new ServiceException("Import file is empty");
            }
            // Convert json file to BatchDriver Array
            List<BatchDriver> batchDrivers = JSON.parseArray(
                    Dc3Util.inputStreamToString(multipartFile.getInputStream()),
                    BatchDriver.class
            );
            if (null == batchDrivers) {
                throw new ServiceException("Import file is blank");
            }
            batchService.batchImport(batchDrivers);
            return R.ok();
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @Override
    public R<Boolean> batchImport(List<BatchDriver> batchDrivers) {
        try {
            if (null == batchDrivers) {
                throw new ServiceException("Import file is blank");
            }
            batchService.batchImport(batchDrivers);
            return R.ok();
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @Override
    public R<BatchDriver> batchExport(String serviceName) {
        try {
            if (StrUtil.hasBlank(serviceName)) {
                throw new ServiceException("Export driver service name is blank");
            }
            BatchDriver batchDriver = batchService.batchExport(serviceName);
            if (null != batchDriver) {
                return R.ok(batchDriver);
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.fail();
    }

}
