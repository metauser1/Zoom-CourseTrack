# udelcoursetrack

A program developed in Clojure that allows you to track courses for opening seats (currently at the University of Delaware only) without the stress of having to check yourself.

It uses the following libraries:

enlive 
http-kit 
postal

## Installation

Download from https://github.com/metauser1/Zoom-CourseTrack/archive/master.zip

## Usage

Zoom is capable of tracking any course from the University of Delaware for open seats. If a spot is or becomes available, an email will be sent to you.

Run the project directly:

    $ boot run

Build an uberjar from the project:

    $ boot build

Run the uberjar:

    $ java -jar target/udelcoursetrack-0.1.0-SNAPSHOT-standalone.jar [args]


## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
