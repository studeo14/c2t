#!/usr/bin/env bash

textFile=$1

#
sort -t ';' -bg -k 1,1 -k 2,2 $textFile > sorted_$textFile
