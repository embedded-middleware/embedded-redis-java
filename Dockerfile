FROM shoothzj/compile:jdk17-mvn AS build
COPY . /opt/compile
WORKDIR /opt/compile
RUN mvn -B package -Dmaven.test.skip=true

FROM shoothzj/base:jdk17

COPY --from=build /opt/compile/embedded-redis-core/target/embedded-redis-core-0.0.1-jar-with-dependencies.jar /opt/redis/embedded-redis.jar
COPY docker-build /opt/redis

CMD ["/usr/bin/dumb-init", "bash", "-vx","/opt/redis/scripts/start.sh"]
