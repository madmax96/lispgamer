# ( LispGamer )
Fun game made for lisp programmers, by lisp programmer.
## About
As a LispGamer your main job wil be to collect lambdas. Lot's of lambdas. Lambdas are good!!!

To collect them you will use a good ol' pair of parentheses ( )

You should also avoid bugs. Bugs are bad, they will shrink your parentheses and take your precious free time (and score).

You can pause/continue game at any time by pressing space button. 

You can try this game here: https://madmax96.github.io/lispgamer/

## Further developing / extending this game
When making this game, one of my primary goals was to make maintaining and extending this game easy, 
especially two parts of it: Adding new levels, and adding new falling object,
together with custom behaviour and impact that new object will have on the gameplay.

## Adding new object

Currently, there are 3 objects in a game: Lambda, Bug and Rock.

In `config.cljs` file you can see that all objects are implementing FallingObject protocol,
so to add new object first thing to do is define new record type that implements all methods from FallingObject protocol.

Second thing that needs to be done is to extend `OBJECT-CONSTANTS` map with new object. You need to add three keys:

`:speed-range` Vector of two integers. Speed of objects is actually represented as time (in milliseconds) that object will be falling (main reason for doing this is to have responsive game, speed of objects should be same on any screen size)
In order to make game more dynamic, speed of any object is not a constant, rather it is random number chosen from an interval. This key is exactly that. For any object type, it's speed will be some random number within this interval.

`:img` Html5 Image element.

`:good?` Boolean. Is this object good or bad for user ? Or,  should user catch it or avoid it, used currently only to know which sound to play on certain events.

Finally, you should add constructor for your new object type in `constructors` map also in `config.cljs` file

## Adding new level
Currently there are 5 levels, but adding new level is easy:
Just add the level map configuration to the levels-config vector defined in `config.cljs`


Level map must have three keys:


`:objects` This is a map where key is type of object and value is number of occurrences of that object in that level. 

`:speed-factor`  This key should have a number of milliseconds as a value, and that number will be subtracted from the actual speed of any object when calculating the final speed of the object.
For example, if object has 1400 as it's speed, and `:speed-factor` of current level is 400, the final actual speed of object will be 1000, so object will fall for 1 second. In this way we can increase general speed of objects for that level and controll how hard level is.
Higher levels should have higher value for this key. 


`:object-gen-iterval` Time interval of generating new falling objects.This could be a constant and in that case new object would be generated at each N milliseconds.
 But again, similarly to objects speed, in order to have more dynamic game, this is an interval and random number is chosen from it.

Example level config map:
```
{:objects {Lambda 20 Bug 25 New-Object-Type 15}
 :speed-factor 120
 :object-gen-interval [1200 1400]}
```

## Setup dev env

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
Copyright © 2019 Simonovic Mladjan

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
