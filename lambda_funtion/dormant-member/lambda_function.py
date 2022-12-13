import os
import requests


def lambda_handler(event, context):
    token = os.environ["TOKEN"]
    header = {"Authorization": "Bearer " + token}

    call("DEV", header)
    # call("STAGE", header)
    # call("PROD", header)


def call(environment, header):
    url = os.environ["URL_" + environment]
    response = requests.patch(url, headers=header)
    data = response.json()
    print("[%s] DormantMember Count: %d" % (
        environment, data["number"]))
