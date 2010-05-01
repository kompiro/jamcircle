#!/bin/sh
ant -buildfile run-test.xml -DbuildDirectory=/tmp/org.kompiro.jamcircle.builder -Dosgi.configuration.area=/tmp/configuration
