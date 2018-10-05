AWS Lambda 함수를 위한 코드로, 빌드 및 배포는 아래 사용법에 따라 진행한다.

### AWS Lambda 함수 정보(Dev)
- 지역: ap-northeast-2
- 람다 함수명: mybeautip-dev-update-image-url
- 주요 기능: S3에 이미지가 생성되면 버전 ID를 포함한 이미지 경로를 myBeautip 서버에 업데이트 한다.


### 사용법
빌드 후 압축하여 해당 함수 코드에 zip 파일로 업로드 한다.
```
npm install
rm ../updateimageurl.zip
zip -r ../updateimageurl.zip *
```

### 기타
- 압축할 때 파일명은 임의로 변경해도 가능하다.
- 기본적으로 AWS Web Console을 사용하지만, 다른 방식의 업로드도 가능하다.