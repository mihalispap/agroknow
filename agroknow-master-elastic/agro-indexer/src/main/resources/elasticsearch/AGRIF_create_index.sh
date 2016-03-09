#!/bin/bash

curl -XPUT "http://$1/agrif/" -d '{
    "settings" : {
        "number_of_shards" : 3,
        "number_of_replicas" : 0,
        "routing.allocation.include.zone" : "main",
        "query.default_field" : "_all",

        "analysis":{
            "filter": {
                "edge_ngram" : {
                    "side" : "front",
                    "max_gram" : 10,
                    "min_gram" : 2,
                    "type" : "edgeNGram"
                },

                "en_stop_filter": {
                    "type": "stop",
                    "stopwords": ["_english_"]
                },
                "en_stem_filter": {
                    "type": "stemmer",
                    "name": "minimal_english"
                },

                "el_stop_filter": {
                    "type": "stop",
                    "stopwords": ["_greek_"]
                },
                "el_stem_filter": {
                    "type": "stemmer",
                    "name": "greek"
                }
            },
            "analyzer": {
                "en_analyzer": {
                    "type": "custom",
                    "tokenizer": "icu_tokenizer",
                    "filter": ["icu_folding", "icu_normalizer", "en_stop_filter", "en_stem_filter"],
                    "char_filter": ["html_strip"]
                },

                "el_analyzer": {
                    "type": "custom",
                    "tokenizer": "icu_tokenizer",
                    "filter": ["icu_folding", "icu_normalizer", "el_stop_filter", "el_stem_filter"],
                    "char_filter": ["html_strip"]
                }
            }
        }
    },

    "mappings" : {
        "agrif" : {
            "dynamic_templates" : [
                {
                    "template_languageBlocks" : {
                        "path_match" : "languageBlocks.*",
                        "mapping" : {
                            "properties" : {
                                "title" : { "type" : "string" },
                                "alternativeTitle" : { "type" : "string" },
                                "titleSupplemental" : { "type" : "string" },
                                "keywords" : { "type" : "string", "index_name" : "keyword" },
                                "abstract" : { "type" : "string" },
                                "notes" : { "type" : "string" }
                            }
                        }
                    }
                }
            ],

            "_id" : {
                "path" : "identifier"
            },

            "_source" : {
                "enabled" : true,
                "compress" : true,
                "compress_threshold" : "1536b"
            },

            "_all" : {
                "enabled" : true
            },

            "_routing" : {
                "required" : true,
                "path" : "set"
            },

            "properties" : {
                "status" : {
                    "type" : "string"
                },
                "set" : {
                    "type" : "string"
                },
                "creationDate" : {
                    "type" : "date",
                    "format" : "date"
                },
                "lastUpdateDate" : {
                    "type" : "date",
                    "format" : "date",
                    "include_in_all" : false
                },

                "languageBlocks" : {
                    "type" : "object",
                    "properties" : {
                        "en": {
                            "properties" : {
                                "title" : { "type" : "string", "analyzer" : "en_analyzer" },
                                "alternativeTitle" : { "type" : "string", "analyzer" : "en_analyzer" },
                                "titleSupplemental" : { "type" : "string", "analyzer" : "en_analyzer" },
                                "keywords" : { "type" : "string", "index_name" : "keyword", "analyzer" : "en_analyzer" },
                                "abstract" : { "type" : "string", "analyzer" : "en_analyzer" },
                                "notes" : { "type" : "string", "analyzer" : "en_analyzer" }
                            }
                        },
                        "el": {
                            "properties" : {
                                "title" : { "type" : "string", "analyzer" : "el_analyzer" },
                                "alternativeTitle" : { "type" : "string", "analyzer" : "el_analyzer" },
                                "titleSupplemental" : { "type" : "string", "analyzer" : "el_analyzer" },
                                "keywords" : { "type" : "string", "index_name" : "keyword", "analyzer" : "el_analyzer" },
                                "abstract" : { "type" : "string", "analyzer" : "el_analyzer" },
                                "notes" : { "type" : "string", "analyzer" : "el_analyzer" }
                            }
                        }
                    }
                },

                "expressions" : {
                    "type" : "object"
                },

                "rights" : {
                    "type" : "object"
                },

                "relations" : {
                    "type" : "object"
                },

                "creators" : {
                    "type" : "object"
                },

                "controlled" : {
                    "type" : "object"
                }
            }
        }
    }
}'
