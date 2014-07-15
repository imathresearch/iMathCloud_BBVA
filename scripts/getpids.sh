#!/bin/sh

ps -eo pid,cmd | grep user=$1 | grep -o '^.....' > pids_$1.txt
