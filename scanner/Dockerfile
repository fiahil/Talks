FROM python:2.7

ENV PYTHONUNBUFFERED 1

ADD . /src
WORKDIR /src

RUN pip install -r requirements.txt

CMD python app.py
