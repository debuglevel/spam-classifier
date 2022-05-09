#! /usr/bin/env python
import json
from black import Iterator
import requests
from pprint import pprint
from library import SmsCollection, TwitterCollection


def process_collection(collection: Iterator):
    stats = dict()
    stats["true_positive"] = 0
    stats["false_positive"] = 0
    stats["true_negative"] = 0
    stats["false_negative"] = 0

    for sample in collection:
        text = sample["text"]
        category = sample["category"]

        json_ = classify(text)

        # x = json_["scores"]["naive-bayes"]
        x = json_["scores"]["OpenNLP-DocumentCategorizer"]
        classified_category = list(x.keys())[0]

        if category == "Spam":
            if classified_category == category:
                stats["true_positive"] += 1
                # print(":-) ", end="")
                print(".", end="", flush=True)
            else:
                stats["false_negative"] += 1
                print("x", end="", flush=True)
                # print("    ", end="")
        else:
            if classified_category == category:
                stats["true_negative"] += 1
                print(".", end="", flush=True)
                # print(":-) ", end="")
            else:
                stats["false_positive"] += 1
                print("x", end="", flush=True)
                # print("    ", end="")

        # print(f"{category} -> {json_}")
        # print(".", end="", flush=True)

    print()
    print_confusion_matrix(stats, "spam", "ham")


def print_confusion_matrix(stats, positive_label, negative_label):
    TP = stats["true_positive"]
    FP = stats["false_positive"]
    TN = stats["true_negative"]
    FN = stats["false_negative"]

    predicted_negative = TN + FN
    predicted_positive = TP + FP
    actual_positives = TP + FN
    actual_negatives = FP + TN
    all = predicted_negative + predicted_positive

    print(f"                 ║      predicted      ║         ")
    print(f"                 ║ {positive_label:^8} │ {negative_label:^8} ║   all   ")
    print(f"═════════════════╬══════════╪══════════╬═════════")
    print(f" actual {positive_label:^8} ║ {TP: >8} │ {FN:>8} ║ {actual_positives:>8}")
    print(f" actual {negative_label:^8} ║ {FP: >8} │ {TN:>8} ║ {actual_negatives:>8}")
    print(f"═════════════════╬══════════╪══════════╬═════════")
    print(
        f"        {'all':^8} ║ {predicted_positive:>8} │ {predicted_negative:>8} ║ {all:>8}"
    )


def classify(text: str):
    data = {
        "text": text,
    }

    response = requests.post("http://localhost:8080/texts/unclassified/", json=data)

    return response.json()


def main():
    process_collection(SmsCollection())
    # process_collection(TwitterCollection())


if __name__ == "__main__":
    main()
