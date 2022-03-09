#!/bin/bash
   
curl --write-out "%{http_code}\n" "http://localhost:9000/scrape" 
