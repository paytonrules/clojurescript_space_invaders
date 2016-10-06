# Clojurescript Space Invaders

More of an experiment than anything. Port of Space Invaders in ClojureScript.

## App

Run with figwheel. For dev and testing run:

```
rlwrap lein figwheel devcards-test dev
```

No server yet, just figwheel. Run lein figwheel and browse to localhost:3449/index.html. The in-browser tests are at localhost:3449/tests.html. Click `runners.browser` for all the tests.

## Tests

In the browwser - browse to localhost:3449/tests.html

Using the doo test runner.

`lein doo phantom test once`
`lein doo phantom test auto` - For auto runner.

## Requirements

Phantomjs 2.0

## Todo

- Fix stretchyness
- Get the canvas bigger, with a proper resolution
- HTML reloading.
- Make sure vim talks to the repl by figwheel (nREPL)
- Game over on crash into ground
- Travis CI cause why not?
- Add gunner
- Blockers

