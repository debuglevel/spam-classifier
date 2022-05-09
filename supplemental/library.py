import csv
from itertools import chain
from pprint import pprint


class SmsCollection:
    # See https://www.kaggle.com/datasets/uciml/sms-spam-collection-dataset?resource=download

    def __init__(self):
        self._filehandler = open("data/spam.csv", errors="ignore")
        self._reader = csv.DictReader(self._filehandler)

    def __iter__(self):
        return self

    def __next__(self):
        row = next(self._reader)

        category = row["v1"]
        text = row["v2"]

        if category == "ham":
            category_ = "Ham"
        elif category == "spam":
            category_ = "Spam"

        d = {"category": category_, "text": text}
        return d


class TwitterCollection:
    # See https://www.kaggle.com/datasets/yashaswi3010/spam-detection-on-twitter?resource=download

    def __init__(self):
        self._filehandler_spam = open(
            "data/twitter/Training_data/spammers_tweets.txt", errors="ignore"
        )
        self._reader_spam = csv.DictReader(
            self._filehandler_spam,
            delimiter="\t",
            fieldnames=["f1", "f2", "text", "f4"],
        )
        self._list_spam = list(self._reader_spam)

        self._filehandler_ham = open(
            "data/twitter/Training_data/legitimate_users_tweets.txt", errors="ignore"
        )
        self._reader_ham = csv.DictReader(
            self._filehandler_ham, delimiter="\t", fieldnames=["f1", "f2", "text", "f4"]
        )
        self._list_ham = list(self._reader_ham)

    def __iter__(self):
        return self

    def __next__(self):
        try:
            row = self._list_ham.pop()
            category = "Ham"
        except IndexError:
            try:
                row = self._list_spam.pop()
                category = "Spam"
            except IndexError:
                raise StopIteration()

        text = row["text"]

        d = {"category": category, "text": text}
        return d
