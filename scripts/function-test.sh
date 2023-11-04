# Currently we are working on alpha/beta/rc versions,
# because the Polyfill project is under incubating.

# One for current min support version
./gradlew clean functionalTest -PagpVersion=8.0.1
# One for latest version
./gradlew clean functionalTest -PagpVersion=8.1.2