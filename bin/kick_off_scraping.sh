#!/bin/bash

URL="${SCRAPE_URL:-http://localhost:9000/scrape}"

curl --write-out "%{http_code}\n" $URL
