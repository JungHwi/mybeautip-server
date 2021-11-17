FROM java:8

WORKDIR /mybeautip-server

ADD build/libs/mybeautip-server-1.1.0.jar app.jar

ENV SERVER_PROFILE local-mysql

ENV PORT 8080

EXPOSE 8080

CMD java -jar -Xms1024m -Xmx2048m app.jar --spring.profiles.active=$SERVER_PROFILE --port=$PORT