#!/usr/bin/env bash

# Keep the order as followed one may depend on previous one
MODULE_ARRAY=('android-arsc-parser' 'android-manifest-parser' 'polyfill-backport' 'polyfill' 'polyfill-test-plugin')
for module in "${MODULE_ARRAY[@]}"
do
./gradlew clean :"$module":publishAllPublicationsToMyMavenlocalRepository
done