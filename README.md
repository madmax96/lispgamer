# ( LispGamer )
Fun game made for lisp programmers, by lisp programmer.
## About
As a LispGamer your main job wil be to collect lambdas. Lot's of lambdas. Lambdas are good!!!
To collect them you will use a good ol' pair of parentheses ( )

You should also avoid bugs. Bugs are bad, they will shrink your parentheses and take your precious free time (and score).


You can try this game here: https://madmax96.github.io/lispgamer/

## Further developing / extending this game


## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. 

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
