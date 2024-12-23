# Copyright 2024 shoothzj <shoothzj@gmail.com>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM shoothzj/compile:jdk17-mvn AS build
COPY . /opt/compile
WORKDIR /opt/compile
RUN mvn -B package -Dmaven.test.skip=true

FROM shoothzj/base:jdk17

COPY --from=build /opt/compile/embedded-redis-core/target/embedded-redis-core-0.0.3-jar-with-dependencies.jar /opt/redis/embedded-redis.jar
COPY docker-build /opt/redis

CMD ["/usr/bin/dumb-init", "bash", "-vx","/opt/redis/scripts/start.sh"]
