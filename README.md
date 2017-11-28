# happybot [![Build Status](https://travis-ci.org/WheezyGold7931/happybot.svg?branch=master)](https://travis-ci.org/WheezyGold7931/happybot) [![Jenkins Download](https://img.shields.io/badge/jenkins-download-blue.svg)](http://142.44.162.101:8080/job/happybot//)
The moderation bot used on the happyheart discord server located here: https://discord.gg/smZmhKa
# Dependencies
We handle all dependencies via gradle but if you must have all of them here they are:
* [JDA](https://github.com/DV8FromTheWorld/JDA)
* [JDA-Utilities](https://github.com/JDA-Applications/JDA-Utilities)
* [Hypixel4J](https://github.com/KevinPriv/HypixelApi4J)
* [SLF4J](https://www.slf4j.org/)
* [sql2o](https://github.com/aaberg/sql2o)
* [Twitter4J](https://github.com/yusuke/twitter4j)
* [gson](https://github.com/google/gson)
* [JUnit 4](https://github.com/junit-team/junit4/)
* [EvalEx](https://github.com/uklimaschewski/EvalEx)

# Building
To make building from gradle as easy as possible we like to use shadowJar as our build option. Here are the commands you would use to build this yourself:

* Windows: ```gradlew.bat shawdowJar```
* Linux/Mac: ```./gradlew shadowJar```

# Exit Codes
This program uses System.exit() to use custom exit codes. While this is not recommended for people to use, (with non-java compliant codes), we did it anyway.
Our bash script that manages this bot uses the following exit codes to decide how to take action upon the program exiting.
* 10 - Download Update from a Testing Point (I use Dropbox)
* 20 - Download Update from a Jenkins Server (Grabs the latest artifact)
* 30 - Stop the loop of the boot script.

# Note
You are free to make pull requests and/or report issues here, and reuse the code at your own will, just please provide credit back to me!
# 
[![forthebadge](http://forthebadge.com/images/badges/built-with-love.svg)](http://forthebadge.com)[![forthebadge](http://forthebadge.com/images/badges/60-percent-of-the-time-works-every-time.svg)](http://forthebadge.com)
