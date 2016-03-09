#!/bin/bash

curl -XPUT "http://$1/users/" -d '{
    "settings" : {
        "number_of_shards" : 3,
        "number_of_replicas" : 0,
        "routing.allocation.include.zone" : "main",
        "query.default_field" : "_all"
    },

    "mappings" : {
        "user" : {
            "properties" : {
                "email" : {
                    "type" : "multi_field",
                    "fields" : {
                        "email" : {"type" : "string", "analyzer" : "standard"},
                        "raw" : {"type" : "string", "analyzer" : "keyword"}
                    }
                },
                "token" : {
                    "type" : "string",
                    "analyzer" : "keyword"
                }
            }
        }
    }
}'
