# udelcoursetrack

A program developed in Clojure that allows you to track courses for opening seats (at the University of Delaware only, for now!) without the stress of having to check yourself.

It uses the following libraries:

enlive

http-kit 

postal

clojure.string


## Installation

Download from https://github.com/metauser1/Zoom-CourseTrack/archive/master.zip and extract the files.

## Usage

Using your favourite terminal, navigate to the directory where you extracted the files.

Run the uberjar:

    $ java -jar udelcoursetrack-0.2.0-SNAPSHOT-standalone.jar

Or

Run the project directly (requires boot):

    $ boot run

Build your own uberjar in /target (requires boot as well):

    $ boot build    

A decent tutorial for boot can be found on their github https://github.com/boot-clj/boot#install


## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
