# -*- coding: utf-8; -*-
from __future__ import unicode_literals
from kafka import KafkaProducer as Producer

from datetime import datetime, timedelta
from collections import namedtuple
import time
import json
import random
import base64
import logging

logging.basicConfig(format='%(asctime)s [%(levelname)8s] %(message)s',
                    level=logging.DEBUG)
log = logging.getLogger('Scanner')

CYCLE_SPEED = 0.5


def get_random_point():
    return (
        round(random.uniform(48.8438105, 48.8883955), 6),
        round(random.uniform(2.2635173, 2.3857733), 6)
    )

class Spawnpoint(namedtuple('Spawnpoint', ['pokemon_ids', 'ttl', 'frequency'])):
    pass

class Spawner:
    def __init__(self):
        self.timer = 0
        self.spawnpoints = {}
        """{ (lat, lon): Spawnpoint(pokemon_ids, ttl, frequency) }"""

    def cycle(self):
        """Create new spawnpoints and delete expired ones
        """
        for p, s in self.spawnpoints.items():
            ids, ttl, fcy = s
            if ttl <= 0:
                # Remove expired spawn
                log.info("Expired spawnpoint @ {}".format(p))
                del self.spawnpoints[p]
            else:
                self.spawnpoints[p] = Spawnpoint(ids, ttl - 1, fcy)

        if self.timer <= 0:
            # Time to create a new spawnpoint
            point = get_random_point()
            spawnpoint = Spawnpoint(
                {random.randint(1, 150) for i in range(random.randint(1, 3))},
                                          # Random pokemon ids
                random.randint(30, 200),  # Random ttl
                random.randint(1, 10)     # Random frequency
            )
            log.info('New spawnpoint with {} @ {}'.format(spawnpoint, point))
            self.spawnpoints[point] = spawnpoint

            # Generate a new seed
            self.timer = random.randint(1, 100)
        self.timer -= 1

    def activated(self):
        """List all active spawnpoints
        """
        return [s for p, s in self.spawnpoints.items() if s.ttl > 0]

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

spawner = Spawner()
i = 0
while True:
    log.info('> {}'.format(i))
    spawner.cycle()
    log.debug('# activated spawners:')
    for s in spawner.activated():
        log.debug('## {}'.format(s))
    log.debug('# timer until new spawnpoint: {}'.format(spawner.timer))
    #scan()
    time.sleep(0.5)
    i += 1

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
