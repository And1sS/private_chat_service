FROM openjdk:11
WORKDIR /usr/privatechat_service
COPY build/libs/privatechat_service-0.0.1.jar service.jar
EXPOSE 8083
CMD ["java", "-jar", "service.jar"]