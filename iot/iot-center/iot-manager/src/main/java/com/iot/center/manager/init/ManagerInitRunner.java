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

package  com.iot.center.manager.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * 初始化
 *
 * @author njrsun20240123
 */
@Component
@EnableFeignClients(basePackages = {
        "com.dc3.api.center.auth.*",
})
@ComponentScan(basePackages = {
        "com.dc3.api.center.auth",
})
public class ManagerInitRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
    }
}
