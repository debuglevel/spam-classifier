## `spam_dataset`

Download <https://www.kaggle.com/datasets/uciml/sms-spam-collection-dataset?resource=download> to `data/spam.csv`.

## `twitter`

Download <https://www.kaggle.com/datasets/yashaswi3010/spam-detection-on-twitter/download> to `data/twitter/`.

## Start learning

```bash
curl -X POST http://localhost:8080/classifiers/start-learning && python3 learn.py && curl -X POST http://localhost:8080/classifiers/stop-learning && python3 classify.py
```
