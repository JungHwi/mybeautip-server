#!/usr/bin/python -tt

import sys
import requests

# Exit with message
def exit(message):
    print(message)
    sys.exit(1)


def main():
    if len(sys.argv) != 1:
        print('usage: ./get_token.py')
        sys.exit(1)

    valid_granttypes = ["facebook", "kakao", "naver", "client", "admin", "1", "2", "3", "4", "5"]
    default_granttype = "facebook"
    # valid_platforms = ["android", "ios", "web", "1", "2", "3"]
    default_plaform = "android"
    valid_hosts = ["localhost", "dev", "1", "2"]
    default_host = "localhost"
    basic_token = {"android":"bXliZWF1dGlwLWFuZHJvaWQ6YWtkbHFieGxxZGtzZW1maGRsZW0=",
        "ios": "bXliZWF1dGlwLWlvczpha2RscWJ4bHFka2RsZGhkcHRt",
        "web": "bXliZWF1dGlwLXdlYjpha2RscWJ4bHFkamVtYWxz"}

    host = raw_input("Enter host(1: localhost*|2: dev): ")
    if len(host) == 0 or "1" == host:
        host = default_host

    if host not in valid_hosts:
        exit("{}: {}".format("invalid host", host))

    if "dev" == host or "2" == host:
        host = "13.125.200.105"

    # platform = raw_input("Enter platform(android*|ios|web): ")
    # if len(platform) == 0:
    #     platform = "android"
    # platform = platform.lower()
    # if platform not in valid_platforms:
    #     print("invalid platform")
    #     sys.exit(1)
    platform = default_plaform

    granttype = raw_input("Enter grant_type(1: facebook*|2: naver|3: kakao|4: client|5: admin): ")
    if len(granttype) == 0:
        granttype = "facebook"
    if granttype not in valid_granttypes:
        print("invalid granttype")
        sys.exit(1)

    url = "http://" + host + ":8080/api/1/token"
    headers = {"Authorization": "Basic " + basic_token[platform],
               "Content-Type": "application/x-www-form-urlencoded",
               "Accept": "application/json"}

    if "client" == granttype or "4" == granttype:
        data = {"grant_type": "client"}

    if "admin" == granttype or "5" == granttype:
        headers = {"Authorization": "Basic " + basic_token["web"],
                   "Content-Type": "application/x-www-form-urlencoded",
                   "Accept": "application/json"}
        data = {"grant_type": "admin", "admin_id": "mybeautip.tv", "password": "akdlqbxlq#1@Jocoos"}

    if "facebook" == granttype or "1" == granttype:
        facebook_id = raw_input("Enter facebook_id(facebook-test-id*): ")
        if len(facebook_id) == 0:
            facebook_id = "facebook-test-id"
        data = {"grant_type": "facebook", "facebook_id": facebook_id}

    if "naver" == granttype or "2" == granttype:
        naver_id = raw_input("Enter naver_id(naver-test-id*): ")
        if len(naver_id) == 0:
            naver_id = "naver-test-id"
        data = {"grant_type": "naver", "naver_id": naver_id}

    if "kakao" == granttype or "3" == granttype:
        kakao_id = raw_input("Enter kakao_id(kakao-test-id*): ")
        if len(kakao_id) == 0:
            kakao_id = "kakao-test-id"
        data = {"grant_type": "kakao", "kakao_id": kakao_id}

    response = requests.post(url, headers=headers, data=data)
    if 200 == response.status_code:
        print(response.json()["access_token"])
    else:
        print(response.status_code)
        print(response.json())


if __name__ == '__main__':
    main()
