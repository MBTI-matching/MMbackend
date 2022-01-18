#!/usr/bin/env bash

source /home/ubuntu/.bash_profile

REPOSITORY=/home/ubuntu/experiment

echo "> Build 파일 복사"
echo "> cp $REPOSITORY/deploy/*.jar $REPOSITORY/"

cp $REPOSITORY/deploy/*.jar $REPOSITORY/

echo "> 현재 구동중인 Set 확인"
CURRENT_PROFILE=$(curl -s https://sixzombies.shop/profile)
echo "> $CURRENT_PROFILE"

# 쉬고 있는 dev 찾기: set1이 사용중이면 set2가 쉬고 있고, 반대면 set1이 쉬고 있음
if [ $CURRENT_PROFILE == set1 ]
then
  IDLE_PROFILE=set2
  IDLE_PORT=8082
elif [ $CURRENT_PROFILE == set2 ]
then
  IDLE_PROFILE=set1
  IDLE_PORT=8081
else
  echo "> 일치하는 Profile이 없습니다. Profile: $CURRENT_PROFILE"
  echo "> set1 할당합니다. IDLE_PROFILE: set1"
  IDLE_PROFILE=set1
  IDLE_PORT=8081
fi

echo "> 새 어플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $IDLE_PROFILE 에서 구동중인 애플리케이션 pid 확인"
IDLE_PID=$(lsof -ti tcp:${IDLE_PORT})

if [ -z $IDLE_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $IDLE_PID"
  kill -15 $IDLE_PID
  sleep 15
fi

echo "> $IDLE_PROFILE 배포"
nohup java -jar -Dspring.profiles.active=$IDLE_PROFILE $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &

# Nginx Port 스위칭을 위한 스크립트
echo "> 스위칭"
sleep 10
/home/ubuntu/experiment/deploy/Nswitch.sh