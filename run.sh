#!/usr/bin/env bash

set -e

jar_path=$(ls cli/target/*-jar-with-dependencies.jar)

java -jar ${jar_path} $@
