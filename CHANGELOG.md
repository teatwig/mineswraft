# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/) (at least I try my best).

## Unreleased
### Added
### Changed

## [0.2.0] - 2020-06-12
### Added
- Experimental Nonogram Mode
- JavaFX is now bundled with every release
- Creating a standalone runtime image (RT) which includes a slimmed down JRE
### Changed
- Upgraded to Java 11, JavaFX 14, Gradle 5.6 
- Now using Gradle *plugins DSL*

## [0.1.0] - 2016-04-21
### Changed
- Replaced direct array access whereever possible with new *Coordinate* class
- Started using *Lombok* for cleaner code
- Replaced many for-loops with streams
### Removed
- support for statistics file version 1.0
### Fixed
- statistics saving on non-Windows systems

## [0.0.23] - 2016-04-14
### Changed
- Moved achievements to own branch to restore the *vanilla* version
- Started using git for this project
- statistics layout to horizontal
- Based window size on actual board size so an *initial easy game* and a *later easy game* have the exact same size
- Moved main package into *net* pacakge

## [0.0.22] - 2016-03-31
### Added
- icon for statistics dialog
- new statistics: Longest winning/losing streak, current streak
- import to
### Changed
- statistics file version to *1.2*

## [0.0.21] - 2016-03-30
### Added
- import for old *stats.dat* (referred to as *file version 1.0*) with automatic backup file creation *statsX.X.dat*
### Changed
- Confirm dialogs appear only when user has started a game
- *Best time* is now hidden when the difficulty hasn't been played yet
- Correctly marked fields appear grey after game over
- statistics file version to *1.1*
### Removed
- unnecessary *public* modifiers from *Board*

## [0.0.20] - 2016-03-29
### Added
- visible timer
- statistics saving in *stats.dat* file (did only work on Windows because I'm stupid)
### Changed
- incorrectly marked fields appear red after game over

## [0.0.19] - 2016-03-24
### Added
- statistics for current sessions

## [0.0.18] - 2016-03-07
### Added
- keyboard shortcuts for menus

## [0.0.17] - 2016-03-07
### Added
- chording with middle-click and left+right-click
- icon to custom dialog
- alert when starting a new game or changing difficulty
### Changed
- Can't flag fields before opening at least one

## [0.0.16] - 2016-03-07
### Changed
- flags to actual flag symbol
- Correctly marked bombs are yellow after game over
- Fields are fixed squared
- Window resizes to match difficutly

## [0.0.13] - 2016-03-07
### Added
- Bombs display after game over
- bugs.txt

## [0.0.12] - 2016-03-04
### Added
- game icon (delta)
- functionality for *Colors* menu item
- functionality for *Quit* menu item
### Changed
- flag color to yellow

## [0.0.11] - 2016-03-04
### Added
- button for changing grey numbers to colored ones (winemine style) [no functionality yet]
- dialog for custom difficulties
- field marking with right click
- automatic version number in title
### Changed
- Game starts automatically without having to use *Restart* in menu
- Moved classes to subpackage *mineswraft*

## [0.0.0] - 2016-03-04
Everything happened around this time.
Quit button didn't work and the game field didn't show until clicking *Restart*

## 2016-03-03
Started developement
