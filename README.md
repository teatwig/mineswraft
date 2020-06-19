# Mineswraft
JavaFX Minesweeper with a couple of twists!

## Build

Building currently requires JDK 11 or newer.

Run application:
```sh
./gradlew run
```
Build distribution packages which require a local JRE (`build/distributions/*-java.{tar.gz,zip}`):
```sh
./gradlew build
```
Build images with jlink which include a slimmed down JRE (`build/*-rt.zip`):
```sh
./gradlew jlinkZip
```

## Versioning

This project uses [Semantic Versioning](https://semver.org/).

The build version is automatically determined based upon the last tag (i.e. release version):

* if the current commit is tagged, the tag will be used
* if the current commit is not tagged the version will contain the last tag, the number of new commits since this tag, as well as the current commit's hash
* if there are any uncommited changes the suffix "-dirty" will be appended
