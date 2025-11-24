# Industrion Strike

A retro space shooter with strategic elements.


## How to Build

You need the following tools to be installed:

* JDK 8 
* ant

Execute ```probe``` target to build:

```
ant probe
```

The ```build``` target is mostly the same, but also increases
build number in app.properties:

```
ant build
```


## How to Play

```
./start
```

Unfortunately, it is virtually impossible to run an applet in a modern
web browser. Java applets were deprecated a long time ago (the last practical specimen I used was my bank web client, 
which still was based on applets back in 2017
until they finally moved to HTML5 & JS).

Sadly, the whole game structure was based on applets -
You start with a landing page and navigate through
HTML-linked menus to different pages
explaining the back story, how to play, and other crucial info.
Each mission was deployed as an applet on a separate .html page with customized parameters. And each mission brief was another page.

You still can navigate the game's static HTML pages to learn about the mission, but the mission page with the applet won't work.

However, it is possible to run the applet directly with the applet viewer still supplied with JDK 8.

So you can run any mission as an applet directly
by using a helper ```./start``` script
and passing the path to the mission profile as a parameter.

Just make sure that you have the proper path for ```applet viewer```
defined for APPLET_VIEWER env variable inside the ```start``` script.

For example, run:

```
./start ibattle_1/b1m2/b1m2.mis
```

To launch an Industion Battle 1 Mission 2.

I created a simple bash script to list the possible mission paths:

```
./missions
```



## Development Story

The game was created in 2005 as a web-deployable Java applet.

Java applets were still alive back in the early 2000s
and I used them for a variety of small games, experiments, and university projects.
My university thesis was an applet providing a gamified experience for learning a mix of physical phenomena.

By the end of 2004, I ended up with a simple software-based retro-looking 3D engine running on the web.
It was way before WebGL and even the time people discovered
that JavaScript is actually a nice and flexible language.

There were some efforts to bring 3D to the web, none particularly successful.
VRML and X3D standards were mostly dead by then
with plugins not supported in modern browsers (that was a scourge for all plugin-based technologies, since they all had
to spend a lot of effort to keep up compatibility with a fast-changing browser landscape).
You could do something 3D-like in Flash.
One of the most spectacular efforts was the Blender Web Plug-in, but it was relatively unknown.

So I was really satisfied by the results when the game finally
came around. All my friends were also impressed by the little
flat-shaded 3D shapes flying in the browser.

It was a nice little engine that could be used for a nice little web game.
Unfortunately, I knew nothing about game design back then
and made some terrible mistakes. Tried to build a huge genre-bending game.

I should have explored more references, made controls more intuitive, goals clearer, and tutorials just better.

I was looking at my favorite Tie Fighter, Urban Assault, Wing Commander, Advanced NetWars, and Elite Plus for inspiration.

But I'd never achieved that tight WWI-style
space dogfight feel of the original Tie Fighter.
The limitations in the technology never allowed me to reach the scale of battles seen in Urban Assault and Homeworld.

And the speeds and distances I'd defined turned the battles
more into a meticulous sim like F-19 rather than
a more tightly packed arcade similar to Advanced NetWars.
You can engage enemy ships with missiles from 20km away, when they look like a bunch of pixel flies. 


So my advice to 20-years-yonger me would be:

* Simplify controls and make a drop-down hint window with key shortcuts.
* Make the current goal clear and visible.
* Reduce speeds, distances, and increase turn rates - make battles close-quoters WWI-style dogfights instead of beyond visual range shootouts.
* Lasers should not overheat that fast - allow to shoot more.
* The laser colors should indicate the team and not the laser type
* Radar should be at the bottom in the middle for better situational awareness
* HUD must provide more info, especially the crosshair - like in Starlancer and Descent: Freespace.
* More feedback in general - flashes, dims, camera shakes... All that was possible even with my primitive engine.
* Tutorials, tutorials, tutorials - the onboarding MUST be smooth and highly curated, no possibility to deviate or be lost.
* Learn basic color design - using #FF0000, #00FF00, and #0000FF just means that I have no color palette to talk about - unless I'm targeting ZX-Spectrum aesthetics.

Also, I should have abandoned the high-resolution mode and embraced the PS1-like low-poly style. I figured it would be computationally hard to achieve texturing on existing screen resolutions, but I'd never realized that I don't need hi-res and should target a retro-styled lower resolution instead. But retro-style was not popular back then; everyone was chasing the pseudo-realistic trend, so how could I have known?

Additionally, I should have promoted the game on all platforms I could reach at the time. Somehow, I kept the development secret, and by the time of the Big Reveal, nobody was there to listen, except for a few friends (and how many friends do we, geeks, have?).
