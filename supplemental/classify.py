#! /usr/bin/env python
import json
import requests
from pprint import pprint
from library import SmsCollection


def process_sms_collection():
    smsCollection = SmsCollection()

    stats = dict()
    stats["true_positive"] = 0
    stats["false_positive"] = 0
    stats["true_negative"] = 0
    stats["false_negative"] = 0

    for sample in smsCollection:
        text = sample["text"]
        category = sample["category"]

        json_ = classify(text)
        # pprint(json_)

        # x = json_["scores"]["naive-bayes"]
        x = json_["scores"]["OpenNLP-DocumentCategorizer"]
        classified_category = list(x.keys())[0]

        if category == "Spam":
            if classified_category == category:
                stats["true_positive"] += 1
                print(":-) ", end="")
            else:
                stats["false_negative"] += 1
                print("    ", end="")
        else:
            if classified_category == category:
                stats["true_negative"] += 1
                print(":-) ", end="")
            else:
                stats["false_positive"] += 1
                print("    ", end="")

        print(f"{category} -> {json_}")

    print(f"     |          |      predicted     ")
    print(f"     |          | positive | negative")
    print(f" act | positive | {stats['true_positive']} | {stats['false_negative']}")
    print(f" ual | negative | {stats['false_positive']} | {stats['true_negative']}")


def classify(text: str):
    data = {
        "text": text,
    }

    response = requests.post("http://localhost:8080/texts/unclassified/", json=data)

    return response.json()


def main():
    process_sms_collection()


if __name__ == "__main__":
    main()
