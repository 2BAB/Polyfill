#!/usr/bin/env bash
./gradlew -PpolyfillPublish=polyfill :polyfill:clean  :polyfill:publishReleasePublicationToMavenLocal
./gradlew -PpolyfillPublish=polyfill-arsc :polyfill-arsc:clean  :polyfill-arsc:publishReleasePublicationToMavenLocal