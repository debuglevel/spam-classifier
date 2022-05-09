#! /usr/bin/env python
from typing import Iterator
import requests
from library import SmsCollection
from library import TwitterCollection
import httpx

# httpClient = requests.Session()
httpClient = httpx.Client()


def process_collection(collection: Iterator):
    for sample in collection:
        # print(sample)
        learn(sample["text"], sample["category"])


def learn(text: str, category: str):
    data = {
        "text": text,
        "classification": category,
    }

    response = httpClient.post("http://localhost:8080/texts/classified/", json=data)

    if response.status_code == 201:
        print(".", end="", flush=True)
    else:
        print(f" {response.status_code} ", end="")


def main():
    process_collection(SmsCollection())
    process_collection(TwitterCollection())


if __name__ == "__main__":
    main()
