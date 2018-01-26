#!/bin/bash

echo -e "-----------------------------------------------------------------------"
echo "Building Data Enrichment"
echo -e "-----------------------------------------------------------------------"

rm -rf target

# Leiningen

lein do clean, compile, install, uberjar
