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

logging.basicConfig(format='%(asctime)s [%(levelname)8s] %(message)s', level=logging.INFO)
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


class Pokemon(namedtuple('Pokemon', ['id', 'pos'])):
    pass


class Spawnpoint(namedtuple('Spawnpoint', ['spawn_id', 'pokemon_ids', 'ttl', 'frequency'])):
    def cycle(self, ref, i):
        if i % self.frequency == 0:
            # Create a new pokemon around the spawnpoint
            log.info('spawning pokemons')
            pos = get_random_point(ref, 0.0001)
            id = random.choice(list(self.pokemon_ids))
            p = Pokemon(id, pos)
            log.info('spawned {}'.format(p))
            return (self.spawn_id, p)
        return (None, None)


class Spawner:
    def __init__(self, kafka):
        self.timer = 0
        self.kafka = kafka
        """kafka client"""
        self.spawnpoints = {}
        """{ (lat, lon): Spawnpoint(pokemon_ids, ttl, frequency) }"""

    def cycle(self):
        """Create new spawnpoints and delete expired ones
        """
        for p, s in self.spawnpoints.items():
            id, ids, ttl, fcy = s
            if ttl <= 0:
                # Remove expired spawn
                log.info("Expired spawnpoint @ {}".format(p))
                kafka.send('spawnpoints', key='delete', value=id)
                del self.spawnpoints[p]
            else:
                self.spawnpoints[p] = Spawnpoint(id, ids, ttl - 1, fcy)

        if self.timer <= 0:
            # Time to create a new spawnpoint
            point = get_random_point()
            spawnpoint = Spawnpoint(
                base64.b64encode(str(time.time())), # SpawnpointId
                {random.randint(1, 150) for i in range(random.randint(1, 3))},
                                          # Random pokemon ids
                random.randint(30, 200),  # Random ttl
                random.randint(10, 20)    # Random frequency
            )
            log.info('New spawnpoint with {} @ {}'.format(spawnpoint, point))
            kafka.send('spawnpoints', key='create', value=spawnpoint.spawn_id)
            self.spawnpoints[point] = spawnpoint

            # Generate a new seed
            self.timer = random.randint(1, 100)
        self.timer -= 1

    def activated(self):
        """List all active spawnpoints
        """
        return [(p, s) for p, s in self.spawnpoints.items() if s.ttl > 0]


log.info('connecting to kafka')
kafka = Producer(bootstrap_servers='127.0.0.1',
                 key_serializer=lambda x: str(x).encode(),
                 value_serializer=json.dumps)
log.info('connected to kafka')

spawner = Spawner(kafka)
i = 0

while True:
    log.info('> {}'.format(i))
    spawner.cycle()
    log.info('# activated spawners:')
    for p, s in spawner.activated():
        log.info('## {}'.format(s))
        spawn_id, pokemon = s.cycle(p, i)
        if spawn_id and pokemon:
            p = {
                'id': pokemon.id,
                'geo': {
                    'lat': pokemon.pos[0],
                    'lon': pokemon.pos[1]
                },
                'expireAt': (datetime.now() + timedelta(minutes=5)).isoformat(),
            }
            kafka.send('pokemons', key=spawn_id, value=p)
    log.info('# timer until new spawnpoint: {}'.format(spawner.timer))
    kafka.flush()
    time.sleep(CYCLE_SPEED)
    i += 1
