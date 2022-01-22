#!/usr/bin/env bash

echo "> 현재 구동중인  Port 확인"
CURRENT_PROFILE=$(curl -s https://sixzombies.shop/profile)

if [ $CURRENT_PROFILE == set1 ]
then
  IDLE_PORT=8082
elif [ $CURRENT_PROFILE == set2 ]
then
  IDLE_PORT=8081
else
  echo "> 일치하는 Profile이 없습니다. Profile: $CURRENT_PROFILE"
  echo "> 8081을 할당합니다."
  IDLE_PORT=8081
fi

echo "> 전환할 Port: $IDLE_PORT"
echo "> Port 전환"
echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" |sudo tee /etc/nginx/conf.d/service-url.inc

echo "> Nginx Reload"
sudo service nginx reload # reload는 설정만 재적용하기 때문에 바로 적용이 가능합니다.

PROXY_PORT=$(curl -s https://sixzombies.shop/profile)
echo "> Nginx Current Proxy Port: $PROXY_PORT"

echo "> Nginx Reload"
sudo service nginx reload # reload는 설정만 재적용하기 때문에 바로 적용이 가능합니다.