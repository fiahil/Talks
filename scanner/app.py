# -*- coding: utf-8; -*-
from __future__ import unicode_literals
from kafka import KafkaProducer as Producer

from datetime import datetime
import time
import json
import random

pokemons = {
    1: 'Salameche',
    2: 'Bulbizarre',
    3: 'Pikachu',
    4: 'Ponita'
}

print('connecting to kafka')
p = Producer(bootstrap_servers='0.0.0.0',
             key_serializer=lambda x: str(x).encode(),
             value_serializer=json.dumps)
print('connected to kafka')
i = 0
while True:
    pokemon = random.choice(pokemons.items())
    print('sending {}'.format(pokemon))
    message = {
        'id': pokemon[0],
        'name': pokemon[1],
        'geo': {
            'lat': round(random.uniform(48.8374235, 48.9045735), 6),
            'lon': round(random.uniform(2.2464643, 2.4028253), 6)
        },
        'expireAt': datetime.now().isoformat()
    }
    p.send('pokemons', key=pokemon[0], value=message)
    p.flush()
    i += 1
    time.sleep(1)
print('The End.')
