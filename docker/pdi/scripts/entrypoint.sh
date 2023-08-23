#!/bin/sh
set -e
/scripts/setup.sh && ${KETTLE_HOME}/kitchen.sh $@