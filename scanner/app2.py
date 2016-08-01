# -*- coding: utf-8; -*-
from __future__ import unicode_literals
from kafka import KafkaConsumer as Consumer

print('connecting to kafka')
consumer = Consumer('dragons', bootstrap_servers='kafka')
print('connected to kafka')
for msg in consumer:
    print('{}={}'.format(msg.key, msg.value))
print('The end')
