#!/usr/bin/env bash

# Keep the order as followed one may depend on previous one
MODULE_ARRAY=('android-xml-parser' 'android-manifest-parser' 'polyfill')
for module in "${MODULE_ARRAY[@]}"
do
./gradlew clean :"$module":publishToMavenLocal
done