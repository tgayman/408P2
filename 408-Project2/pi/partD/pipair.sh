#!/bin/bash
opt -print-callgraph $1 1> /dev/null 2>$1.callgraph
java -cp ~/cs408/p2/408P2/proj-skeleton/pi/partD/ -Xms128m -Xmx128m Pi $1.callgraph $@
