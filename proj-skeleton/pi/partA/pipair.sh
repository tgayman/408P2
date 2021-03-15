#!/bin/bash
opt -print-callgraph $1 1> /dev/null 2>$1.callgraph
java -Xms128m -Xmx128m Pi $1.callgraph $@
