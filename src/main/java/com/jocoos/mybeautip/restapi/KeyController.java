package com.jocoos.mybeautip.restapi;

import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/1")
public class KeyController {

    @Autowired
    private AWSSecurityTokenService awsSTS;

    @Value("${mybeautip.addr.confirm-key}")
    private String confirmKey;

    @Value("${mybeautip.aws.s3.bucket}")
    private String bucketName;

    @Value("${mybeautip.aws.s3.region}")
    private String region;

    @Value("${mybeautip.aws.sts.token-validity-duration-seconds}")
    private String tokenValidityDurations;


    @GetMapping("/keys")
    public ResponseEntity<KeyInfo> getAddressConfirmKey(@RequestParam String name) {
        log.debug("confirmKey: {}", confirmKey);

        switch (name) {
            case "addr_confirm":
                return new ResponseEntity<>(new KeyInfo(confirmKey), HttpStatus.OK);

            case "storage":
                GetSessionTokenRequest request = new GetSessionTokenRequest();
                request.withDurationSeconds(Integer.parseInt(tokenValidityDurations));
                GetSessionTokenResult result = awsSTS.getSessionToken(request);
                KeyInfo keyInfo = new KeyInfo(bucketName, region, result.getCredentials());
                return new ResponseEntity<>(keyInfo, HttpStatus.OK);

            default:
                throw new BadRequestException("invalid request name - " + name);
        }
    }

    @NoArgsConstructor
    @Getter
    static class KeyInfo {
        private String confirmKey;
        private String bucket;
        private String region;
        private String accessKeyId;
        private String secretAccessKey;
        private String sessionToken;
        private Long expiration;

        private KeyInfo(String confirmKey) {
            this.confirmKey = confirmKey;
        }

        private KeyInfo(String bucket, String region, Credentials credentials) {
            this.bucket = bucket;
            this.region = region;
            this.accessKeyId = credentials.getAccessKeyId();
            this.secretAccessKey = credentials.getSecretAccessKey();
            this.sessionToken = credentials.getSessionToken();
            this.expiration = credentials.getExpiration().getTime();
        }
    }
}
