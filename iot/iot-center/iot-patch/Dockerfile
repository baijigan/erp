#
#  Copyright 2020-2023 Njrsun. All Rights Reserved.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.njrsun.com
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

FROM registry.cn-beijing.aliyuncs.com/dc3/alpine-java:dragonwell-8.6.6
MAINTAINER pnoker <pnokers.icloud.com>

ENV JAVA_OPS -server -Xms128m -Xmx1024m -XX:CompressedClassSpaceSize=128m -XX:MetaspaceSize=200m -XX:MaxMetaspaceSize=200m

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && mkdir -p /dc3-center/dc3-monitor/dc3/logs

WORKDIR /dc3-center/dc3-monitor

ADD ./target/dc3-monitor.jar ./

EXPOSE 8200
VOLUME /dc3-center/dc3-monitor/dc3/logs

CMD java ${JAVA_OPS} -Djava.security.egd=file:/dev/./urandom -jar dc3-monitor.jar
