#!/usr/bin/env bash

set -euxf -o pipefail

export PARAM1=5
[[ "$(lein run -m urbandictionary.malli-env.demo)" =~ "Success" ]]

unset PARAM1
[[ "$(lein run -m urbandictionary.malli-env.demo)" =~ "missing required key" ]]