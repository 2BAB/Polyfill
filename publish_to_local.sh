#!/usr/bin/env bash

# Keep the order as followed one may depend on previous one
MODULE_ARRAY=('android-arsc-parser' 'android-manifest-parser' 'polyfill-backport')
for module in "${MODULE_ARRAY[@]}"
do
./gradlew clean :"$module":publishAllPublicationsToMyMavenlocalRepository
done

./gradlew clean :polyfill:publishPluginMavenPublicationToMavenLocalRepository
./gradlew clean :polyfill:publishMe.2bab.polyfillPluginMarkerMavenPublicationToMyMavenlocalRepository
./gradlew clean :polyfill-test-plugin:publishAllPublicationsToMyMavenlocalRepository