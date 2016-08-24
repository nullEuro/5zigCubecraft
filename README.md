# 5zigCubecraft

This is a plugin for the Minecraft mod [5zig](http://5zig.net/) that adds a few
display items on the [CubeCraftGames Server](https://www.cubecraft.net/).

This is not an official plugin and I am not affiliated with Cubecraft or 5zig.

## Features

### General

* **LeaveKey** configurable hotkey for `/leave`

### SkyWars

* **ChestType** shows the currently active chest type (basic, normal, overpowered) during the game
* **Voters** shows a list of players that can vote for chest types and what they voted for
* **Kit** shows the currrently selected kit (or shows that no kit has been selected)
* **Stalker** shows how often other players in the game have killed you and how often you killed them previously

### Upcoming

* Write messages in the chat during the countdown and they will be posted when the game starts
* Summary after the game, for example "You killed 5 players in 6 minutes and 32 seconds."
* Support for other game modes

![5zig CubeCraft screenshot 1](https://raw.githubusercontent.com/nullEuro/5zigCubecraft/master/docs/screenshots/5zigCubecraft1.png)

![5zig CubeCraft screenshot 2](https://raw.githubusercontent.com/nullEuro/5zigCubecraft/master/docs/screenshots/5zigCubecraft2.png)

## Installation

Install the [current version of 5zig](http://5zig.net/downloads).

Download the [latest release of 5zigCubecraft](https://github.com/nullEuro/5zigCubecraft/releases) and place the .jar file in your `.minecraft/the5zigmod/plugins` folder.

To activate the items click on Options -> The 5zig Mod... -> Customize Display and select the module you want to add the items to or crate a new module.
Then click on Settings -> Items -> Add new Item... and select the items you want do add (our items are in the category Server General).

[Video: How to activate the items](https://www.youtube.com/watch?v=72-4OYyKLl8)


## Building (developers only)
Download the latest version of the [5zig api jar](https://github.com/5zig/The-5zig-API/releases)
Place the jar file in the `libs` folder of this project.

You can now open the project in an IDE with Gradle support (like _IntelliJ IDEA_).

If you just want to compile a JAR file, open the project directory in
a terminal and execute this command:

Windows:

    gradlew jar

Linux:

    ./gradlew jar
