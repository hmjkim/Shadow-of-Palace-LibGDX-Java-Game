# Shadow of Palace - LibGDX Java Game

Shadow of Palace is a **COMP 2522** term project developed with Java and LibGDX for desktop platforms. In this single-player survival game, you play as the lowest-ranked royal consort and rise through the ranks by collecting resources and strategically using items to eliminate rival consorts, ultimately becoming the Queen.

## Authors

**Min Ji Kim (Heather)**
- StudentID: A01063140
- GitHub name: hmjkim


**Min Seo Lee (Zoey)**
- StudentID: A01405148
- GitHub name: ZoeyLee66

## Setup Requirements

- **Java 8 or higher**
- **LibGDX 1.13.1**
- **Project Generator**: [gdx-liftoff](https://libgdx.com/wiki/start/project-generation)
    - This project was generated with gdx-liftoff, which provides a template including simple application launchers and an `ApplicationAdapter` extension.

## Overview

### Game Story Background

During the Joseon Dynasty (1392–1910) in ancient Korea, the king followed a polygamous system, maintaining both a primary wife (the queen) and multiple concubines (HuGung) ranked by status. You begin as a **Noble Lady (Gwi-in)**, the lowest-ranked consort, and must ascend the ranks to claim the throne.

### Core Mechanics

- **Day/Night Cycle**: Different gameplay mechanics by time (daytime collection vs. nighttime crafting and poisoning).
- **Gathering Ingredients**: Collect ingredients during the day around the palace.
- **Mystery Box Crafting**: Craft at night to potentially obtain different special items.
- **Health System**: You start with **5 hearts**. Collisions with NPCs reduce hearts differently by day/night.
- **Rank Promotion**: Poison and eliminate higher-ranked consorts to inherit their rank.
- **Win Condition**: Defeat three consorts, then poison the Queen to achieve ultimate victory.


## How to Install and Run the Game

1. Clone or download this repository.  
   You can use Git to clone the project:
   ```bash
   https://github.com/hmjkim/COMP-2522-202510-Term-Project-ZH.git
   ```
   Or download it as a ZIP from GitHub and unzip it locally.
2. Open the project in IDE of your choice with Gradle support. (eg. IntelliJ IDEA, Android Studio)
3. Extend the `Gradle` tab on the right side of your window.
4. Expand the project folder and navigate to: 
    
    ```
    lwjgl3 -> Tasks -> application -> run
    ```
    Selecting `run` will launch the game.

## How to Play

### Move

Use the arrow keys (`↑`, `↓`, `←`, `→`) to move the character around the palace. You will only be able to walk on the designated paths.

### Collect Ingredients (Day)  

Move onto an ingredient (like `water`, `herb`, or `stone`) and press the action key `Space` to pick it up.

### Inventory & Items
- Press `I` to use an **Invincibility** item (if available).
- Press `H` to use a **Heart Recovery** item (if available).

### Craft Mystery Box (Night)

Go to the kitchen to check the current recipe and press `M` to craft a mystery box. Crafting consumes your ingredients and gives one of the following random outcomes:

- Failure (no item)
- Poison
- Invincibility item
- Heart recovery item

### Poisoning Consorts (Night)

Press `X` near a target consort to use a poison item. Eliminating the correct consort promotes your rank.

### Health & Suspicion

You start with **5 hearts**.
Collision with NPCs causes:
- **0.5 heart** loss during the day
- **1 heart** loss at night

You also lose hearts when poisoning someone out of your rank.

## Dependencies
- Box2D: Provides physics for smooth player movement and collisions.
- FreeType: Allows custom fonts for in-game text.

Whenever you add more dependencies, ensure you `Sync All Gradle Projects` so the packages are properly recognized.

## LibGDX Details 

### Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.


### Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:


- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).


Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
