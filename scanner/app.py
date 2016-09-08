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

logging.basicConfig(format='%(asctime)s [%(levelname)8s] %(message)s', level=logging.DEBUG)
log = logging.getLogger('Scanner')

CYCLE_SPEED = 0.5


def get_random_point(ref=None, step=0):
    if ref:
        return (
            round(random.uniform(ref[0] - step, ref[0] + step), 6),
            round(random.uniform(ref[1] - step, ref[1] + step), 6),
        )
    return (
        round(random.uniform(48.8438105, 48.8883955), 6),
        round(random.uniform(2.2635173, 2.3857733), 6)
    )

class Spawnpoint(namedtuple('Spawnpoint', ['pokemon_ids', 'ttl', 'frequency'])):
    def cycle(self, ref, i):
        if i % self.frequency == 0:
            # Create a new pokemon around the spawnpoint
            log.debug('spawning pokemons')
            pos = get_random_point(ref, 0.0001)
            id = random.choice(list(self.pokemon_ids))
            log.debug('Spawning {} at {}'.format(id, pos))
            return (id, pos)

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
        return [(p, s) for p, s in self.spawnpoints.items() if s.ttl > 0]

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

log.info('connecting to kafka')
kafka = Producer(bootstrap_servers='0.0.0.0',
                 key_serializer=lambda x: str(x).encode(),
                 value_serializer=json.dumps)
log.info('connected to kafka')

spawner = Spawner()
i = 0

while True:
    log.info('> {}'.format(i))
    spawner.cycle()
    log.debug('# activated spawners:')
    for p, s in spawner.activated():
        log.debug('## {}'.format(s))
        p = s.cycle(p, i)
        if p:
            p = {
                'encounterId': 'test',
                'id': p[0],
                'geo': {
                    'lat': p[1][0],
                    'lon': p[1][1]
                },
                'expireAt': (datetime.now() + timedelta(minutes=5)).isoformat(),
            }
            kafka.send('pokemons', key='test', value=p)
            kafka.flush()
    log.debug('# timer until new spawnpoint: {}'.format(spawner.timer))
    time.sleep(CYCLE_SPEED)
    i += 1
