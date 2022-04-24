# GalaxyRun

A space-themed infinite runner for Android. Pilot your spaceship through an infinite level of obstacles and survive as long as you can!

<p align="center">
  <img src="https://user-images.githubusercontent.com/8965354/164979059-86d64fd6-d7d8-4558-96ce-4f151fb07750.gif" />
</p>

This game is programmed from scratch. There are various interesting little features that I still want to program (check out the Issues). Contributions (code, graphics, sound effects or music) would be very welcome!

A few things about the game and game engine that I'm proud of:
- The entire game is procedurally generated and can go on forever.
- All game logic is executed in one place. This allows us to run the game in a separate thread. I've also designed things so that the game is drawn in a separate thread as well.
- All GUI elements in the game actually live in the Game Engine. The game itself doesn't use any Android UI components, besides a blank view.
- Because I implemented the GUI elements myself, I also had to implement multi-touch. That was non-trivial!

Some interesting things to look at if you're curious:
- [The GameEngine](https://github.com/Stefan4472/GalaxyRun/tree/master/GalaxyRun/app/src/main/java/com/galaxyrun/engine/GameEngine.java)
- [Procedural Map Generation](https://github.com/Stefan4472/GalaxyRun/tree/master/GalaxyRun/app/src/main/java/com/galaxyrun/engine/map/Map.java)
- [A very simple Pathfinding algorithm to decide where to place coin trails](https://github.com/Stefan4472/GalaxyRun/tree/master/GalaxyRun/app/src/main/java/com/galaxyrun/engine/map/PathFinder.java)
- [The in-engine UI](https://github.com/Stefan4472/GalaxyRun/tree/master/GalaxyRun/app/src/main/java/com/galaxyrun/engine/ui/GameUI.java)

## Audio Attributions

- Sound effects: Juhani Junkala (https://opengameart.org/content/512-sound-effects-8-bit-style) (CC0 License)
- Sound effects: phoenix1291 (https://opengameart.org/content/sfx-the-ultimate-2017-16-bit-mini-pack) (CC0 license)
- Game theme: teknoaxe (http://teknoaxe.com/Link_Code_3.php?q=1314&genre=Loop) (CC4.0 https://creativecommons.org/licenses/by/4.0/) (no changes made)
- Main theme: Juhani Junkala (https://opengameart.org/content/4-chiptunes-adventure) (CC0 license)

## Font Attributions

I use the "Galaxy Monkey" font by Tepid Monkey Fonts. See the license in `GalaxyMonkeyLicense.txt`
