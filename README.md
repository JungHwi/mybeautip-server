# mybeautip Server #

## Download MySQL Image ##
```
docker pull mysql
```

## DB Run ##
```
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=PASSWORD --name mybeautip-db -v ~/mysql8:/var/lib/mysql mysql --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

## DB on docker with charset ##
```
docker exec -it mybeautip-db mysql -u mybeautip -p mybeautip --default-character-set=utf8
```

## DB setting ##
```
create database mybeautip charset=utf8;
create user mybeautip@'%' identified by 'PASSWORD'; 
grant all privileges on mybeautip.* to mybeautip@'%';
```

## Redis on docker ##
```
$ docker run --name mybeautip-redis -d -p 6379:6379 -v /your/dir:/data redis redis-server --requirepass akdlqbxlq#1@Jocoos --appendonly yes
$ docker exec -it mybeautip-redis redis-cli

> auth akdlqbxlq#1@Jocoos
> config get requirepass
```


## Build ##
```
./gradlew build
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

## Build and run with docker to ECR ##
``` 
./gradlew clean build
docker build -t {ecr_address}/mybeautip-api:develop .

// Requrired aws cli installed and configured by user iam access_key
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin {ecr_address}
docker push {ecr_address}/mybeautip-api:develop

// in dev server
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin {ecr_address}
docker pull {ecr_address}/mybeautip-api:develop

docker rm -f mybeautip-server
docker run --name mybeautip-server -v /var/log/mybeautip-server:/mybeautip-server/logs --net=host -d {ecr_address}/mybeautip-api:develop

or

docker-compose -f mybeautip-server-dev.yml up -d
```

## distribute for production to ECR ##
```
./gradlew clean build
docker build -t {ecr_address}/mybeautip-api:production .

// Requrired aws cli installed and configured by user iam access_key
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin {ecr_address}
docker push {ecr_address}/mybeautip-api:production


// in prod server
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin {ecr_address}
docker pull dev.jocoos.com/mybeautip-server:production
docker rm -f mybeautip-server
docker run --name mybeautip-server -e SERVER_PROFILE=production -v /var/log/mybeautip-server:/mybeautip-server/logs --net=host -d dev.jocoos.com/mybeautip-server:production

or

docker-compose -f mybeautip-server-prod.yml up -d
```


