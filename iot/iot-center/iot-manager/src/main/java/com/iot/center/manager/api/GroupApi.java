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
import com.dc3.api.center.manager.feign.GroupClient;
import com.iot.center.manager.service.GroupService;
import com.dc3.common.bean.R;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.GroupDto;
import com.dc3.common.model.Group;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 设备 Client 接口实现
 *
 * @author njrsun20240123
 */
@Slf4j
@RestController
@RequestMapping(Common.Service.DC3_MANAGER_GROUP_URL_PREFIX)
public class GroupApi implements GroupClient {

    @Resource
    private GroupService groupService;

    @Override
    public R<Group> add(Group group, Long tenantId) {
        try {
            Group add = groupService.add(group.setTenantId(tenantId));
            if (null != add) {
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
            return groupService.delete(id) ? R.ok() : R.fail();
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @Override
    public R<Group> update(Group group, Long tenantId) {
        try {
            Group update = groupService.update(group.setTenantId(tenantId));
            if (null != update) {
                return R.ok(update);
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.fail();
    }

    @Override
    public R<Group> selectById(Long id) {
        try {
            Group select = groupService.selectById(id);
            if (null != select) {
                return R.ok(select);
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.fail();
    }

    @Override
    public R<Page<Group>> list(GroupDto groupDto, Long tenantId) {
        try {
            groupDto.setTenantId(tenantId);
            Page<Group> page = groupService.list(groupDto);
            if (null != page) {
                return R.ok(page);
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.fail();
    }

}
