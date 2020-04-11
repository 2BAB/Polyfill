#!/usr/bin/env bash
./gradlew -PpolyfillPublish=polyfill -PdryRun=false :polyfill:clean :polyfill:bintrayUpload
./gradlew -PpolyfillPublish=polyfill-arsc -PdryRun=false :polyfill-arsc:clean :polyfill-arsc:bintrayUpload
./gradlew deployRelease