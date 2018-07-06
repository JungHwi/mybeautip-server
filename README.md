# mybeautip Server #

## Build local ##
```
./gradlew clean build
docker build -t dev.jocoos.com/mybeautip-server:develop .
docker push dev.jocoos.com/mybeautip-server:develop

// in dev server
docker pull dev.jocoos.com/mybeautip-server:develop
docker rm mybeautip-server
docker run -d --name mybeautip-server -v /var/log/mybeautip-server:/mybeautip-server/logs --net=host dev.jocoos.com/mybeautip-server:develop

```

## Run ##

### Run with in memory h2 database ###
```
$ ./gradlew build buildDocker
$ docker run -it --name mybeautip-server -e SERVER_PROFILE=local -p 8080:8080 mybeautip-server
```

### Run with local mysql ###
```
$ ./gradlew build buildDocker
$ docker run -it --net=host --name mybeautip-server -e SERVER_PROFILE=local-mysql -p 8080:8080 mybeautip-server
```

## API Documents ##

```
{server_ip:port}/swagger-ui.html
```