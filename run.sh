#!/bin/bash

echo -e "-----------------------------------------------------------------------"
echo "Running Data Enrichment"
echo -e "-----------------------------------------------------------------------"
echo -e
echo "Mappings:"
echo -e "1 -> Books"
echo -e "2 -> TVs"
echo -e "3 -> Desktop Computers"
echo -e "4 -> Furniture"
echo -e "5 -> Smartphones"
echo -e
read -p 'Please select a category: ' category
if [ "$category" -ge 1 -a "$category" -le 5 ]
    then
        java -jar target/data-enrichment-0.1.0-SNAPSHOT-standalone.jar $category;
else
    echo -e "Please select a number between 1 and 5" >&2 && exit 1
fi
