# FROM maven:3.6.3-jdk--slim AS MAVEN_TOOL_CHAIN
# Build Container Reference is from https://hub.docker.com/_/maven
FROM maven:3.8.4-openjdk-17-slim AS MAVEN_TOOL_CHAIN
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn clean package

# Run Container reference is from 
# FROM openjdk:8-jdk-alpine
FROM openjdk:17-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
ARG JAR_NAME=@project.name@-@project.version@.@project.packaging@ 
ARG DEPLOY_PATH=/deploy/data/
ENV ENTRY_POINT=${DEPLOY_PATH}${JAR_NAME}
RUN mkdir ${DEPLOY_PATH} -p && chown -R spring:spring /deploy
COPY --chown=spring:spring --from=MAVEN_TOOL_CHAIN /tmp/target/${JAR_NAME} ${DEPLOY_PATH}
USER spring:spring
EXPOSE @expose.port@
ENTRYPOINT exec java -jar ${ENTRY_POINT}

# run this to build your container for you local docker environment
# docker build -t services/@repository.name@:@project.version@ -t services/@repository.name@:latest .
# Run the container over port @expose.port@
# docker run --name @repository.name@ -p @expose.port@:@expose.port@ -t services/@repository.name@:latest
# If you have issues with your container, run this command to inject the shell as the entry point.  This will allow you to connect to the container and debug.
# docker run --name @repository.name@ -p @expose.port@:@expose.port@ --entrypoint /bin/sh -t services/@repository.name@:latest
