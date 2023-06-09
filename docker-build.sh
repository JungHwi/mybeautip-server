echo "env: $1"

ENV=$1
TAG="${ENV:=develop}"

echo "docker tag: ${TAG}"

./gradlew build -x test 

aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 776875668468.dkr.ecr.ap-northeast-2.amazonaws.com

docker buildx build --platform linux/amd64,linux/arm64 -t 776875668468.dkr.ecr.ap-northeast-2.amazonaws.com/mybeautip-api:$TAG . --push 

