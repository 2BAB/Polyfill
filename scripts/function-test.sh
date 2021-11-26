# Currently we are working on alpha/beta/rc versions,
# because the Polyfill project is under incubating.

# One for current min support version
./gradlew clean functionTest -PagpVersion=7.0.3
# One for latest version
./gradlew clean functionalTest -PagpVersion=7.1.0-beta04