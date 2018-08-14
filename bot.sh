#!/bin/bash

DROPBOX_URL=*censored*
JENKINS_URL=http://127.0.0.1:8080/job/happybot//lastSuccessfulBuild/artifact/build/libs/happybot-all.jar
JENKINS_DEV_URL=http://127.0.0.1:8080/job/happybot-dev//lastSuccessfulBuild/artifact/build/libs/happybot-dev-all.jar

while true
do
java -jar HappyBot.jar
OUT=$?
if [ ${OUT} -eq 10 ]
then
echo "[HappyBot] [Bash] Grabbing Update from Dropbox..."
wget -q -O HappyBot.jar ${DROPBOX_URL}
echo "[HappyBot] [Bash] Update Done."
fi
if [ ${OUT} -eq 20 ]
then
echo "[HappyBot] [Bash] Grabbing Update from Jenkins..."
wget -q -O HappyBot.jar ${JENKINS_URL}
echo "[HappyBot] [Bash] Update Done."
fi
if [ ${OUT} -eq 25 ]
then
echo "[HappyBot] [Bash] Grabbing Update from Jenkins [DEV]..."
wget -q -O HappyBot.jar ${JENKINS_DEV_URL}
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
