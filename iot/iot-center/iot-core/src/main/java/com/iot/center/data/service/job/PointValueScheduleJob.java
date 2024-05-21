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

package  com.iot.center.data.service.job;

import com.iot.center.data.service.PointValueService;
import com.dc3.common.bean.point.PointValue;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author njrsun20240123
 */
@Slf4j
@Component
public class PointValueScheduleJob extends QuartzJobBean {

    @Value("${data.point.batch.speed}")
    private Integer batchSpeed;
    @Value("${data.point.batch.interval}")
    private Integer interval;

    @Resource
    private PointValueService pointValueService;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    public static List<PointValue> pointValues = new ArrayList<>();
    public static ReentrantReadWriteLock valueLock = new ReentrantReadWriteLock();
    public static AtomicLong valueCount = new AtomicLong(0), valueSpeed = new AtomicLong(0);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // Statistical point value receive rate
        long speed = valueCount.getAndSet(0);
        valueSpeed.set(speed);
        speed /= interval;
        if (speed >= batchSpeed) {
            log.debug("Point value receiver speed: {} /s, value size: {}, interval: {}", speed, pointValues.size(), interval);
        }

        if (null != pointValue) {
            pointValue.setCreateTime(new Date());
            threadPoolExecutor.execute(() -> {
                try {
                    dataCustomService.postHandle(pointValue);
                } catch (Exception e) {
                    log.error("Save point values to post handle error {}", e.getMessage());
                }
            });
            threadPoolExecutor.execute(() -> {
                try {
                    savePointValueToMongo(pointValue.getDeviceId(), pointValue);
                } catch (Exception e) {
                    log.error("Save point values to mongo error {}", e.getMessage());
                }
            });
            threadPoolExecutor.execute(() -> {
                try {
                    savePointValueToRedis(pointValue);
                } catch (Exception e) {
                    log.error("Save point values to redis error {}", e.getMessage());
                }
            });
        }

        // Save point value array to Redis & MongoDB
        threadPoolExecutor.execute(() -> {
            valueLock.writeLock().lock();
            if (pointValues.size() > 0) {
                pointValueService.savePointValues(pointValues);
                pointValues.clear();
            }
            valueLock.writeLock().unlock();
        });
    }
}
