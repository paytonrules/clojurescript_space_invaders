# Clojurescript Space Invaders

More of an experiment than anything. Port of Space Invaders in ClojureScript.

## App

Run with figwheel. For dev and testing run:

```
rlwrap lein figwheel devcards-test dev
```

No server yet, just figwheel. Run lein figwheel and browse to localhost:3449/index.html. The in-browser tests are at localhost:3449/tests.html. Click `runners.browser` for all the tests.

## Tests

Using the doo test runner.

`lein doo phantom test once`
`lein doo phantom test auto` - For auto runner.

## Requirements

Phantomjs 2.0

## Todo

x Make an in browser test runner with figwheel, so I don't need to run doo simultaneously.
x Fix up gitignore file (resources is probably wrong)
x Remove with-redefs in async tests, it doesn't work
- Redesign
  - Game Loop takes a record that contains :state and :transitions
  - Any update can return a :state and :transitions
  - Transitions are all executed after state changes
  - Possible Downsides
    - Future Updates can lose info from past updates
    - Transition dependencies have to be explicitly managed in the state machine
    - Game Loop is getting pretty gnarly
    - Testing the "transition" means seeing if it's present, which doesn't necessarily show it works.
  - Upsides
    - Updates are now pure functions again
    - Transitions can allow dependency injection without being ugly
    - Mocking is straightfoward for game loop and transitions
    - Side Effects are localized
- Clean the image-loader tests
  - You can move the take after the onload call, images is a synchronous call
  - Make the setup-created-images
- Game Loop isn't clean (refactor tests in particular)
  - update without :tick should just call update
  - take-event! can actually return an event
- Make sure your game actually fires an event in testing
- Handle the image loaded event, and go to the playing state.
- Once the game is in playing state, draw the damn aliens
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

