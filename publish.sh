#!/usr/bin/env bash
./gradlew -PpolyfillPublish=polyfill -PdryRun=false :polyfill:clean :polyfill:bintrayUpload
./gradlew deployRelease