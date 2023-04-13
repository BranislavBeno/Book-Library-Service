FROM gradle:8.0.2-jdk19-jammy AS build
RUN mkdir /project
COPY . /project
WORKDIR /project
# create fat jar
RUN gradle app:build -x app:test
# move the jar file
RUN cd app/build/libs/ && cp book-library-service.jar /project/
# extrect layered jar file
RUN java -Djarmode=layertools -jar book-library-service.jar extract

FROM azul/zulu-openjdk-alpine:20-jre
# install dumb-init
RUN apk add --no-cache dumb-init=1.2.5-r1

RUN mkdir /database
WORKDIR /database
COPY --from=build /project/database/ ./

# add specific non root user for running application
RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
# set work directory
RUN mkdir /bls
WORKDIR /bls
# copy jar from build stage
COPY --from=build /project/dependencies/ ./
COPY --from=build /project/snapshot-dependencies/ ./
COPY --from=build /project/spring-boot-loader/ ./
COPY --from=build /project/application/ ./
# change owner for jar directory
RUN chown -R javauser:javauser /bls
# switch user
USER javauser
# run application, where dumb-init occupies PID 1 and takes care of all the PID special responsibilities
ENTRYPOINT ["dumb-init", "java", "org.springframework.boot.loader.JarLauncher"]