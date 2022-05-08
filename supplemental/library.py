import csv


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
