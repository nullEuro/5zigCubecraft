# 5zigCubecraft

This is a plugin for the Minecraft mod [5zig](http://5zig.net/) that adds a few
display items on the [CubeCraftGames Server](https://www.cubecraft.net/).

This is not an official plugin and I am not affiliated with Cubecraft or 5zig.

## Features

|Item     |SkyWars|Survival Games|EggWars|Duels|Assassination|
|---------|:-----:|:------------:|:-----:|:---:|:-----------:|
|Voters   |✓      |✓             |✓      |✗    |✗            |
|Modifiers|✓      |✓             |✓      |✗    |✗            |
|Kit      |✓      |✓             |✓      |✗    |✗            |
|Stalker  |✓      |✓             |✓      |✓    |✗            |
|Countdown|✓      |✓             |✓      |✓    |✗            |
|Summary  |✓      |✓             |✓      |✓    |✗            |
|LeaveKey |✓      |✓             |✓      |✓    |✓            |
|QuickChat|✓      |✓             |✓      |✓    |✓            |
|Money    |✗      |✗             |✗      |✗    |✓            |
|Opponent |✗      |✗             |✗      |✓    |✗            |

### Voters

![Voters](https://i.imgur.com/RQXPqXd.png)

Shows a list of players that can vote for item types, the time or heath and what they voted for.

### Modifiers

![Modifiers](https://i.imgur.com/arRBl5d.png)

Shows the currently active item type (basic, normal, overpowered) and time or health during the game.

### Kit

![Kit](https://i.imgur.com/uTHjKJe.png)

Shows the currently selected kit (or shows that no kit has been selected).

### Stalker

![Stalker](https://i.imgur.com/P5CMX3t.png)

Shows how often other players in the game have killed you and how often you killed them previously in the current gamemode.

### Countdown

![Countdown](https://i.imgur.com/qd2nQev.png)

Enables the 5zig countdown item.

### Summary

![Summary](https://i.imgur.com/AVsSvZV.png)

Displays a short summary in the chat after the game ends.

### LeaveKey

Press a configurable key (the default is L) to execute `/leave`.

### QuickChat

Use a set configurable keys (the default set is numpad 1-9) to send custom chat messages.

### Money

![Money](https://i.imgur.com/EeriJU9.png)

Shows how much money you have in your inventory.

### Opponent

![Opponent](https://i.imgur.com/Ap8RcXF.png)

Shows your opponents name, his ping and the number of previous kills/deaths against this player.

### Upcoming

* Write messages in the chat during the countdown and they will be posted when the game starts

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
