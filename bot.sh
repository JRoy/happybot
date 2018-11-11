#!/bin/bash

while true
do
java -Djava.util.logging.config.file=logging.properties -jar HappyBot.jar
OUT=$?
if [ ${OUT} -eq 10 ]
then
echo "[HappyBot] [Bash] Grabbing Update from SSH..."
cp -u tmp.jar HappyBot.jar
echo "[HappyBot] [Bash] Update Done."
fi
if [ ${OUT} -eq 20 ]
then
echo "[HappyBot] [Bash] Grabbing Update from Git[dev]..."
{
git clone --single-branch -b dev https://github.com/JRoy/happybot.git
cd happybot/
./gradlew shadowJar
cp build/libs/happybot-all.jar ../HappyBot.jar
cd ..
rm -r happybot/
} &> /dev/null
echo "[HappyBot] [Bash] Update Done."
fi
if [ ${OUT} -eq 25 ]
then
echo "[HappyBot] [Bash] Grabbing Update from Git[master]..."
{
git clone https://github.com/JRoy/happybot.git
cd happybot/
./gradlew shadowJar
cp build/libs/happybot-all.jar ../HappyBot.jar
cd ..
rm -r happybot/
} &> /dev/null
echo "[HappyBot] [Bash] Update Done."
fi
echo "[HappyBot] [Bash] Rebooting in:"
for i in 3 2 1
do
echo "[HappyBot] [Bash] $i..."
sleep 1
done
echo "[HappyBot] [Bash] Rebooting!"
done
