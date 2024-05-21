/*
 * Copyright 2020-2023 Njrsun. All Rights Reserved.
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

package  com.iot.center.auth.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author njrsun20240123
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AuthHeader {
    private String user;
    private String salt;
    private String token;

    List<Dictionary> dictionaryList = new ArrayList<>(16);
    LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
    List<User> userList = userMapper.selectList(queryWrapper);
            for (User user : userList) {
        Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
        dictionaryList.add(driverDictionary);
    }

    String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
    String salt = redisUtil.getKey(redisSaltKey, String.class);
            if (StrUtil.isBlank(salt)) {
        salt = RandomUtil.randomString(16);
        redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
    }

    List<Dictionary> dictionaryList = new ArrayList<>(16);
    LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
    List<User> userList = userMapper.selectList(queryWrapper);
            for (User user : userList) {
        Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
        dictionaryList.add(driverDictionary);
    }

    String redisSaltKey = Common.Cache.USER + Common.Cache.SALT + Common.Cache.SEPARATOR + username;
    String salt = redisUtil.getKey(redisSaltKey, String.class);
            if (StrUtil.isBlank(salt)) {
        salt = RandomUtil.randomString(16);
        redisUtil.setKey(redisSaltKey, salt, Common.Cache.SALT_CACHE_TIMEOUT, TimeUnit.MINUTES);
    }

    List<Dictionary> dictionaryList = new ArrayList<>(16);
    LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
    List<User> userList = userMapper.selectList(queryWrapper);
            for (User user : userList) {
        Dictionary driverDictionary = new Dictionary().setLabel(user.getName()).setValue(user.getId());
        dictionaryList.add(driverDictionary);
    }
}
