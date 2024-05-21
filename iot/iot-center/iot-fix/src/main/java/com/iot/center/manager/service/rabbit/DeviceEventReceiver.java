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

package  com.iot.center.manager.service.rabbit;

import com.iot.center.manager.service.EventService;
import com.dc3.common.constant.Common;
import com.dc3.common.model.DeviceEvent;
import com.dc3.common.utils.RedisUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 接收驱动发送过来的设备事件
 *
 * @author njrsun20240123
 */
@Slf4j
@Component
public class DeviceEventReceiver {

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private EventService eventService;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @RabbitHandler
    @RabbitListener(queues = "#{deviceEventQueue.name}")
    public void deviceEventReceive(Channel channel, Message message, DeviceEvent deviceEvent) {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            if (null == deviceEvent || null == deviceEvent.getDeviceId()) {
                log.error("Invalid device event: {}", deviceEvent);
                return;
            }
            log.debug("Device {} event, From: {}, Event: {}", deviceEvent.getType(), message.getMessageProperties().getReceivedRoutingKey(), deviceEvent);

            switch (deviceEvent.getType()) {
                // Save device heartbeat to Redis
                case Common.Device.Event.HEARTBEAT:
                    redisUtil.setKey(
                            Common.Cache.DEVICE_STATUS_KEY_PREFIX + deviceEvent.getDeviceId(),
                            deviceEvent.getContent(),
                            deviceEvent.getTimeOut(),
                            deviceEvent.getTimeUnit()
                    );
                    break;
                case Common.Device.Event.ERROR:
                case Common.Device.Event.OVER_UPPER_LIMIT:
                case Common.Device.Event.OVER_LOWER_LIMIT:
                    //TODO 去重
                    threadPoolExecutor.execute(() -> eventService.addDeviceEvent(deviceEvent));
                    break;
                default:
                    log.error("Invalid event type, {}", deviceEvent.getType());
                    break;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
