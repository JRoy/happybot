# happybot [![Travis](https://img.shields.io/travis/JRoy/happybot.svg?style=for-the-badge)](https://travis-ci.org/JRoy/happybot) [![JavaDocs](https://img.shields.io/badge/javadocs-view-brightgreen.svg?style=for-the-badge)](https://jroy.github.io/happybot/javadocs/) [![Jenkins Download](https://img.shields.io/badge/jenkins-download-blue.svg?style=for-the-badge)](http://142.44.162.101:8080/job/happybot//)  [![Codacy grade](https://img.shields.io/codacy/grade/8c61619d7c67461083fc9386bd5b6c87.svg?style=for-the-badge)](https://app.codacy.com/project/JRoy/happybot/dashboard)
A multi-purpose, feature-full, and powerful, ***highly guild-specific***, Discord Bot written in Java.

# Features
This bot has lots of cool & random features; Here are some:
* Custom Command Implementation of a Command Implementation
* YouTube Upload Monitor - *Easy Channel Adding*
* Random Reddit Post Grabber - *Easy Subreddit Adder*
* League of Legends Summoner Info Grabber
* Twitter Tweet Monitor
* Gamble/Economy/Shop System
* Reporting System
* Warning System
* Staff Management
* Moderation Commands
* AutoMod
* Message "Gilding"/"Staring"
* Message Submissions Pin-er
* Random Welcome Message System
* Custom Server Themes using [discord-themer](https://github.com/JRoy/discord-themer) - *File-based system to make themes easy*
* Custom Logger
* Update System - *Custom boot script allows for reliable jarfile updating*

# Setup
This bot is made to work with one server, as we depend on very specific roles/channels and their respective ids.
If you still would like to use this bot, despite you needing to basically copy exactly how happyheart's guild looks and works follow these steps:
* Pretty much change all of the util package. This is where most of the ids are exposed, especially the Roles class. If you enjoy stack traces but also enjoy basic function, I suggest you inspect and change the classes: Constants and Roles for any exposed ids and replace them for your own use case. The bot will still not work 100% but will at least run and not break.
* Run the bot once, and allow for its files to be created and follow the configuration section below.
* Restart the bot and watch as it creates many stack traces in your console.

Some things may not still work such as the gamble system, as the create table statement is not provided with this code, that may change in the future but that's how it is now.

# Configuration
All of the config options are stored inside of a yml file called: "setting.yml" All the settings in there must be set if you want the bot to load. The options will generate on the bot's first run.

# Building
To make sure all of our dependencies get included in our jar files, we use @johnrengelman's shadow plugin for gradle. Here are the commands you would use to build this yourself:

* Windows: ```gradlew.bat shawdowJar```
* Linux/Mac (Any UNIX-Based OS): ```./gradlew shadowJar```

# Exit Codes
This program uses System.exit() to use custom exit codes. While this is not recommended for people to use, (with non-java compliant codes), we did it anyway.
Our bash script that manages this bot uses the following exit codes to decide how to take action upon the program exiting.
* 10 - Download Update from a Testing Point (I use Dropbox)
* 20 - Download Update from a Jenkins Server (Grabs the latest artifact from /master)
* 25 - Download Update from a Jenkins Server (Grabs the latest artifact from /dev)
* 30 - Stop the loop of the boot script.

# Note
While I understand that making this whole bot work in its entirety may be impractical for you, using certain snippets may be very helpful for your own projects. All ask of you if you do decide to use some of my code, please make sure to respect the Apache Licence and, optionally, mention this repository ;).

# Donate
While it's perfectly optional, if you like what I do and want to support me feel free to buy my a pizza:

Patreon: https://www.patreon.com/JRoy

PayPal.me: https://www.paypal.me/JoshuaRoy1

Bitcoin: `32J2AqJBDY1VLq6wfZcLrTYS8fCcHHVDKD`

[![forthebadge](http://forthebadge.com/images/badges/built-with-love.svg)]()
