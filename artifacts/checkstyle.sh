#!/bin/bash

CURRENT_DIR=$( dirname "${BASH_SOURCE[0]}" )

java -jar "$CURRENT_DIR"/checkstyle-8.36-all.jar \
  -c "$CURRENT_DIR"/style_checks.xml \
  com.puppycrawl.tools.checkstyle.gui.Main \
  "$CURRENT_DIR"/../src/main/java/castle/comp3021/assignment/*
