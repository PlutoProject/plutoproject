#!/bin/bash

# Fetch the current branch's remote version
git fetch origin "$1"
if [ $? -ne 0 ]; then
    echo "Fetch failed!"
    exit 1
fi

# Rebase onto the fetched branch
git rebase "origin/$1"
if [ $? -ne 0 ]; then
    echo "Rebase failed!"
    exit 1
fi

echo "Rebase completed successfully!"