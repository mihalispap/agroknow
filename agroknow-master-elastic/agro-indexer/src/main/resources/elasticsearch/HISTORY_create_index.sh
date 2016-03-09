#!/bin/bash

curl -XPUT "http://$1/history/" -d '{
    "settings" : {
        "number_of_shards" : 3,
        "number_of_replicas" : 0,
        "routing.allocation.include.zone" : "main",
        "query.default_field" : "_all"
    },

    "mappings" : {
        "query" : {
        }
    }
}'
