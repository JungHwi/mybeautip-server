#!/usr/bin/python -tt

import sys
import requests
import json


# Exit with message
def exit(message):
    print(message)
    sys.exit(1)


# Check string value length
def validation(value, count):
    if len(value) == 0 or len(value) > count:
        print("invalid value: " + value)
        sys.exit(1)


def main():
    if len(sys.argv) != 1:
        exit("usage: ./admin.py")

    basic_token = {"android": "bXliZWF1dGlwLWFuZHJvaWQ6YWtkbHFieGxxZGtzZW1maGRsZW0=",
                   "ios": "bXliZWF1dGlwLWlvczpha2RscWJ4bHFka2RsZGhkcHRt",
                   "web": "bXliZWF1dGlwLXdlYjpha2RscWJ4bHFkamVtYWxz"}
    valid_hosts = ["localhost", "dev", "1", "2"]
    default_host = "localhost"
    valid_resources = ["banner", "member", "goods", "motd", "1", "2", "3", "4"]
    default_resource = "banner"
    # valid_operations = ["insert", "delete", "1", "2"]
    # default_operation = "insert"

    host = raw_input("Enter host(1: localhost*|2: dev): ")
    if len(host) == 0 or "1" == host:
        host = default_host
    if host not in valid_hosts:
        exit("invalid host: " + host)
    if host == "dev" or "2" == host:
        host = "13.125.200.105"

    url = "http://" + host + ":8080/api/1/token"
    headers = {"Authorization": "Basic " + basic_token["web"],
               "Content-Type": "application/x-www-form-urlencoded",
               "Accept": "application/json"}
    data = {"grant_type": "admin", "admin_id": "mybeautip.tv", "password": "akdlqbxlq#1@Jocoos"}

    response = requests.post(url, headers=headers, data=data)
    if 200 == response.status_code:
        token = response.json()["access_token"]
    else:
        exit("{} {}".format(response.status_code, response.json()))

    resource = raw_input("Enter recommended resource(1: banner*|2: member|3: goods|4: motd): ")
    if len(resource) == 0:
        resource = default_resource

    resource = resource.lower()
    if resource not in valid_resources:
        exit("{}: {}".format("invalid resource", resource))

    # operation = raw_input("Enter operation(i: insert*|d: delete): ")
    # if len(operation) == 0:
    #     operation = default_operation
    # if operation not in valid_operations:
    #     exit("{}: {}".format("invalid operation", operation))

    headers = {"Authorization": "Bearer " + token,
               "Content-Type": "application/json",
               "Accept": "application/json"}

    if "banner" == resource or "1" == resource:
        url = "http://" + host + ":8080/api/admin/manual/banners"
        title = raw_input("Enter title(max = 22): ")
        validation(title, 22)
        description = raw_input("Enter description(max = 34): ")
        validation(description, 34)
        thumbnail_url = raw_input("Enter thumbnail_url(max = 255): ")
        validation(thumbnail_url, 255)
        link = raw_input("Enter link(max = 255): ")
        validation(thumbnail_url, 255)
        category = raw_input("Enter category(1: post, 2: goods, 3: goods list, 4: video): ")
        try:
            category = int(category)
        except ValueError as e:
            exit(e)

        seq = raw_input("Enter seq(int): ")
        try:
            seq = int(seq)
        except ValueError as e:
            exit(e)

        started_at = raw_input("Enter started_at(yyyyMMdd HHmmss): ")
        ended_at = raw_input("Enter started_at(yyyyMMdd HHmmss): ")

        data = {"title": title,
                "description": description,
                "thumbnail_url": thumbnail_url,
                "link": link,
                "category": category,
                "seq": seq,
                "started_at": started_at,
                "ended_at": ended_at}

    if "member" == resource or "2" == resource:
        url = "http://" + host + ":8080/api/admin/manual/members"
        member_id = raw_input("Enter member_id: ")
        if len(member_id) == 0:
            exit("invalid member_id")

        seq = raw_input("Enter seq(int): ")
        try:
            seq = int(seq)
        except ValueError as e:
            exit(e)

        started_at = raw_input("Enter started_at(yyyyMMdd HHmmss): ")
        ended_at = raw_input("Enter started_at(yyyyMMdd HHmmss): ")

        data = {"member_id": member_id,
                "seq": seq,
                "started_at": started_at,
                "ended_at": ended_at}

    if "goods" == resource or "3" == resource:
        url = "http://" + host + ":8080/api/admin/manual/goods"
        goods_no = raw_input("Enter goods_no: ")
        if len(goods_no) == 0:
            exit("invalid goods_no")

        seq = raw_input("Enter seq(int): ")
        try:
            seq = int(seq)
        except ValueError as e:
            exit(e)

        started_at = raw_input("Enter started_at(yyyyMMdd HHmmss): ")
        ended_at = raw_input("Enter started_at(yyyyMMdd HHmmss): ")

        data = {"goods_no": goods_no,
                "seq": seq,
                "started_at": started_at,
                "ended_at": ended_at}

    if "motd" == resource or "4" == resource:
        url = "http://" + host + ":8080/api/admin/manual/motd"
        video_key = raw_input("Enter video_key: ")
        if len(video_key) == 0:
            exit("invalid video_key")

        seq = raw_input("Enter seq(int): ")
        try:
            seq = int(seq)
        except ValueError as e:
            exit(e)

        started_at = raw_input("Enter started_at(yyyyMMdd HHmmss): ")
        ended_at = raw_input("Enter started_at(yyyyMMdd HHmmss): ")

        data = {"video_key": video_key,
                "seq": seq,
                "started_at": started_at,
                "ended_at": ended_at}

    response = requests.post(url, headers=headers, data=json.dumps(data))
    print(host)
    print(response.status_code)
    print(response.json())


if __name__ == '__main__':
    main()
