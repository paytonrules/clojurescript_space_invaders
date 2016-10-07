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

- Get position changing
  - Start with an initial position
  - Move it each tick, should it be called ticks?
- Get the spacing right
  - Probably a lookup table
  - Invaders namespace
- Game over on crash into ground
- Travis CI cause why not?
- Try out using Eclipse
- Add gunner
- Blockers

