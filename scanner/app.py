# -*- coding: utf-8; -*-
from __future__ import unicode_literals
from kafka import KafkaProducer as Producer

from datetime import datetime
from datetime import timedelta
import time
import json
import random
import base64

encounters = {}
cycle_size = 2

def scan():
    global encounters
    t = datetime.now()

    if t.second == 0 and t.minute % cycle_size:
        encounters = {}

    encounters[t.second] = encounters.get(t.second,
                                          (base64.b64encode(str(round(time.time()))),
                                           random.randint(1, 150)))

    return {
        'encounterId': encounters.get(t.second)[0],
        'id': encounters.get(t.second)[1],
        'geo': {
            'lat': round(random.uniform(48.8438105, 48.8883955), 6),
            'lon': round(random.uniform(2.2635173, 2.3857733), 6)
        },
        'expireAt': (datetime.now() + timedelta(minutes=cycle_size, seconds=10)).isoformat(),
    }

print('connecting to kafka')
p = Producer(bootstrap_servers='0.0.0.0',
             key_serializer=lambda x: str(x).encode(),
             value_serializer=json.dumps)
print('connected to kafka')
while True:
    pokemon = scan()
    print('sending {}'.format(pokemon))
    p.send('pokemons', key=pokemon['encounterId'], value=pokemon)
    p.flush()
    time.sleep(0.5)
print('The End.')
