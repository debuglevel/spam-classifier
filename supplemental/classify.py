#! /usr/bin/env python
import json
from black import Iterator
import requests
from pprint import pprint
from library import SmsCollection, TwitterCollection


def ensure_dict(d, classifier):
    if classifier not in d:
        d[classifier] = {}
        d[classifier]["true_positive"] = 0
        d[classifier]["false_positive"] = 0
        d[classifier]["true_negative"] = 0
        d[classifier]["false_negative"] = 0


def process_collection(collection: Iterator):
    stats = dict()

    for sample in collection:
        text = sample["text"]
        category = sample["category"]

        json_ = classify(text)

        for classifier in json_["scores"].keys():
            classification = json_["scores"][classifier]
            classified_category = list(classification.keys())[0]

            ensure_dict(stats, classifier)

            if category == "Spam":
                if classified_category == category:
                    stats[classifier]["true_positive"] += 1
                    # print(":-) ", end="")
                    print(".", end="", flush=True)
                else:
                    stats[classifier]["false_negative"] += 1
                    print("x", end="", flush=True)
                    # print("    ", end="")
            else:
                if classified_category == category:
                    stats[classifier]["true_negative"] += 1
                    print(".", end="", flush=True)
                    # print(":-) ", end="")
                else:
                    stats[classifier]["false_positive"] += 1
                    print("x", end="", flush=True)
                    # print("    ", end="")

        print("|", end="", flush=True)
        # print(f"{category} -> {json_}")
        # print(".", end="", flush=True)

    for classifier in stats.keys():
        print()
        print_confusion_matrix(stats[classifier], "spam", "ham", classifier)


def print_confusion_matrix(
    stats, positive_label: str, negative_label: str, classifier: str
):
    TP = stats["true_positive"]
    FP = stats["false_positive"]
    TN = stats["true_negative"]
    FN = stats["false_negative"]

    predicted_negative = TN + FN
    predicted_positive = TP + FP
    actual_positives = TP + FN
    actual_negatives = FP + TN
    all = predicted_negative + predicted_positive

    print(f"==== {classifier:^39} ====")
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
