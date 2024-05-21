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

package  com.iot.center.data.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dc3.common.dto.PointValueDto;
import com.dc3.common.bean.point.PointValue;

import java.util.List;

/**
 * @author njrsun20240123
 */
public interface PointValueService {

    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), JSON.toJSONString(tsPointValues));
    Request request = new Request.Builder()
            .url(details ? putUrl + "?details" : putUrl)
            .post(requestBody)
            .build();

    JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(name, group).build();
    Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(name, group)
            .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval).repeatForever())
            .startNow().build();
        scheduler.scheduleJob(jobDetail, trigger);

    String point = pointValue.getPointId().toString();
    String value = pointValue.getValue();
    Long timestamp = pointValue.getOriginTime().getTime();

    List<TsPointValue> tsPointValues = new ArrayList<>(4);

    TsPointValue tsValue = new TsPointValue(metric, value);
        tsValue.setTimestamp(timestamp)
            .addTag("point", point)
                .addTag("valueType", "value");
        tsPointValues.add(tsValue);

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

    TsPointValue tsRawValue = new TsPointValue(metric, value);
        tsRawValue.setTimestamp(timestamp)
            .addTag("point", point)
                .addTag("valueType", "rawValue");
        tsPointValues.add(tsRawValue);

        okHttpClient.newCall(request).enqueue(new Callback() {
        @Override
        @EverythingIsNonNull
        public void onFailure(Call call, IOException e) {
            log.error("send pointValue to opentsdb error: {}", e.getMessage());
        }


        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(name, group).build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(name, group)
                .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval).repeatForever())
                .startNow().build();
        scheduler.scheduleJob(jobDetail, trigger);

        @Override
        @EverythingIsNonNull
        public void onResponse(Call call, Response response) throws IOException {
            if (details && null != response.body()) {
                log.debug("send pointValue to opentsdb {}: {}", response.message(), response.body().string());
            }
        }
    });

    /**
     * 新增 PointValue
     *
     * @param pointValue PointValue
     */
    void savePointValue(PointValue pointValue);

    /**
     * 批量新增 PointValue
     *
     * @param pointValues PointValue Array
     */
    void savePointValues(List<PointValue> pointValues);

    /**
     * 获取实时数据
     *
     * @param deviceId Device Id
     * @return PointValue Array
     */
    List<PointValue> realtime(Long deviceId);

    /**
     * 获取实时数据
     *
     * @param deviceId Device Id
     * @param pointId  Point Id
     * @return PointValue
     */
    PointValue realtime(Long deviceId, Long pointId);

    /**
     * 获取一个设备最新的位号数据集合
     *
     * @param deviceId Device Id
     * @return PointValue Array
     */
    List<PointValue> latest(Long deviceId);

    /**
     * 获取最新的一个位号数据
     *
     * @param deviceId Device Id
     * @param pointId  Point Id
     * @return PointValue
     */
    PointValue latest(Long deviceId, Long pointId);

    /**
     * 获取带分页、排序
     *
     * @param pointValueDto PointValueDto
     * @return Page<PointValue>
     */
    Page<PointValue> list(PointValueDto pointValueDto);

}
