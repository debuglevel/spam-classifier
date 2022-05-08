#! /usr/bin/env python
import requests
from library import SmsCollection


def process_sms_collection():
    smsCollection = SmsCollection()
    for sample in smsCollection:
        # print(sample)
        learn(sample["text"], sample["category"])


def learn(text: str, category: str):
    data = {
        "text": text,
        "classification": category,
    }

    response = requests.post("http://localhost:8080/texts/classified/", json=data)

    if response.status_code == 201:
        print(".", end="", flush=True)
    else:
        print(f" {response.status_code} ", end="")


def main():
    process_sms_collection()


if __name__ == "__main__":
    main()
