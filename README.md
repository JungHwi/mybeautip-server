# mybeautip Server #

## Build ##
```
./gradlew build
```

## DB setting ##
```
create database mybeautip charset=utf8;
create user mybeautip@'%' identified by 'akdlqbxlq#1@Jocoos';
grant all privileges on mybeautip.* to mybeautip@'%';
```

## Run with java application ##
```
./gradlew build && java -jar build/libs/mybeautip-server-0.0.1.jar

```

## API for developers ##

```
{server_ip:port}/swagger-ui.html
```


## Build and run with docker ##
```
./gradlew clean build
docker build -t dev.jocoos.com/mybeautip-server:develop .
docker run --name mybeautip-server --net=host -d dev.jocoos.com/mybeautip-server:develop
```

## distribute for devleop ##
```
// in dev server
docker pull dev.jocoos.com/mybeautip-server:develop
docker rm -f mybeautip-server
docker run --name mybeautip-server -v /var/log/mybeautip-server:/mybeautip-server/logs --net=host -d dev.jocoos.com/mybeautip-server:develop

or

docker-compose -f mybeautip-server-dev.yml up -d

```

## distribute for production ##
```
./gradlew clean build
docker build -t dev.jocoos.com/mybeautip-server:production .
docker push dev.jocoos.com/mybeautip-server:production .

// in prod server
docker pull dev.jocoos.com/mybeautip-server:production
docker rm -f mybeautip-server

docker run --name mybeautip-server -e SERVER_PROFILE=production -v /var/log/mybeautip-server:/mybeautip-server/logs --net=host -d dev.jocoos.com/mybeautip-server:production

or

docker-compose -f mybeautip-server-prod.yml up -d
```
