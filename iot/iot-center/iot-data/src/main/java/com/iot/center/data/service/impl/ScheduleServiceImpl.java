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

package  com.iot.center.data.service.impl;

import com.iot.center.data.service.ScheduleService;
import com.iot.center.data.service.job.PointValueScheduleJob;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author njrsun20240123
 */
@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Value("${data.point.batch.interval}")
    private Integer interval;

    @Resource
    private Scheduler scheduler;

    @Override
    public void initial() {
        createScheduleJobWithInterval("ScheduleGroup", "PointValueScheduleJob", interval, PointValueScheduleJob.class);
        try {
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
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

        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(name, group).build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(name, group)
                .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval).repeatForever())
                .startNow().build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * Create schedule job
     *
     * @param group    group
     * @param name     name
     * @param interval interval
     * @param jobClass class
     */
    @SneakyThrows
    public void createScheduleJobWithInterval(String group, String name, Integer interval, Class<? extends Job> jobClass) {

        TsPointValue tsRawValue = new TsPointValue(metric, value);
        tsRawValue.setTimestamp(timestamp)
                .addTag("point", point)
                .addTag("valueType", "rawValue");
        tsPointValues.add(tsRawValue);
    }

}
