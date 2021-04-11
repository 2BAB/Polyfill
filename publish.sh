#!/usr/bin/env bash

# Keep the order as followed one may depend on previous one
MODULE_ARRAY=('polyfill-matrix' 'polyfill-gradle' 'polyfill-arsc' 'polyfill-manifest' 'polyfill-agp' 'polyfill')
for module in "${MODULE_ARRAY[@]}"
do
./gradlew :"$module":publishAllPublicationsToSonatypeRepository
done

./gradlew aggregateJars releaseArtifactsToGithub