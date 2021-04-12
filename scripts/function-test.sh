# Currently we are working on alpha/beta/rc versions,
# because the Polyfill project is under incubating.

# One for current min support version
./gradlew clean functionTest -PagpVersion=4.2.0-rc01
# One for latest version
./gradlew clean functionalTest -PagpVersion=7.0.0-alpha14