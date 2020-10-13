# SpaceShips
A space-themed infinite runner for Android  

Current development level: Alpha


**In August 2020 I decided to revisit this project and bring it to publication. Because I have other projects to work on, I don't currently have the time to realize the full vision of the game, which is pretty sophisticated and intricate. Therefore, the code is undergoing lots of simplification, and I am cutting out many features that were half-implemented and would take too long to finish. As part of this "pivot", I am renaming the app itself to "GalaxyRun". The source of GalaxyRun is in the "GalaxyRun" folder. Essentially, GalaxyRun is a stripped-down, polished revision of the original SpaceShips code.***

Android files can be found in the android folder. The java folder contains graphics and sources for the earlier Java version of this game.

Vector (.svg) graphics are used for launcher icons as well as layout components (buttons, logos, etc.). These should be stored in android/res/drawable. 

Other graphics (sprites and animations) should start in vector format and be placed in the vector-graphics folder. The android/res folder contains folders for drawables for different screen densities (-hdpi, -ldpi, -mdpi, -xhdpi) and the vector graphics should be scaled and placed in appropriate folders (see [Provide Alternative Bitmaps](http://developer.android.com/training/multiscreen/screendensities.html#TaskProvideAltBmp) in the Android Documentation). 
