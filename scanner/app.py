# -*- coding: utf-8; -*-
from __future__ import unicode_literals
from kafka import KafkaProducer as Producer

import time

dragons = [b'fire', b'earth', b'dabajabazza']

print('connecting to kafka')
p = Producer(bootstrap_servers='kafka')
print('connected to kafka')
i = 0
while True:
    print('sending {}'.format(dragons[i % 3]))
    p.send('dragons', key=b'dragon', value=dragons[i % 3])
    p.flush()
    i += 1
    time.sleep(1)
print('The End.')
