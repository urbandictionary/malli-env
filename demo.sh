#!/usr/bin/env bash

set -euxf -o pipefail

export PARAM1=5
[[ "$(lein run -m malli-env.demo)" =~ "Success" ]]

unset PARAM1
[[ "$(lein run -m malli-env.demo)" =~ "missing required key" ]]

echo "Passed!"