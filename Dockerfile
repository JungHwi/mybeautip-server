FROM public.ecr.aws/amazoncorretto/amazoncorretto:17

WORKDIR /mybeautip-server

ADD build/libs/mybeautip-server.jar app.jar

ENV SERVER_PROFILE dev

ENV PORT 8080

EXPOSE 8080

CMD java -jar app.jar --spring.profiles.active=$SERVER_PROFILE --port=$PORT
#ENTRYPOINT ["java", "-Dspring.profiles.active=${SERVER_PROFILE}", "-Xms1024M", "-Xmx2048M", "-jar", "app.jar"]
