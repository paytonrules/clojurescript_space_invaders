# Clojurescript Space Invaders

More of an experiment than anything. Port of Space Invaders in ClojureScript.

## App

No server yet, just figwheel. Run lein figwheel and browse to localhost:3449

## Tests

Using the doo test runner.

`lein doo phantom test once`
`lein doo phantom test auto` - For auto runner.

## Requirements

Phantomjs 2.0

## Todo

- Make an in browser test runner with figwheel, so I don't need to run doo simultaneously.
- Fix up gitignore file (resources is probably wrong)
- HTML reloading.
- Make sure vim talks to the repl by figwheel (nREPL)
- Test the game loop - which had a bug.
- Make an asset loader.
- Break up core (which is not main), the game loop (into utillities), a game layer (which will have the height and width of the images for collision, matching those of the images for now).
- Get the entire grid on screen
- Start moving them
- Add all the alien
- Game over on crash into ground
- Add gunner
- Travis CI cause why not?

