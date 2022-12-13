import os
import requests


def lambda_handler(event, context):
    token = os.environ["TOKEN"]
    header = {"Authorization": "Bearer " + token}

    call("DEV", header)
    call("STAGE", header)
    call("PROD", header)


def call(environment, header):
    url = os.environ["URL_" + environment]
    response = requests.patch(url, headers=header)
    data = response.json()
    print("[%s] EventStartEnd Response Status: %d, Start: %d, Stop: %d" % (
        environment, response.status_code, data["start_count"], data["end_count"]))
