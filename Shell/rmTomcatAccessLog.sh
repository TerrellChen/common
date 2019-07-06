#!/bin/sh
#****************************************************************#
# ScriptName: rm_access_h5-collector.sh
# Create Date: 2018-12-09 15:55
# Modify Date: 2018-12-09 15:55
# Author: chentairan
# 删除指定分组下第 DAYDIFF 天前的tomcat access 日志
#***************************************************************#
APP_NAME=$1
APP_HOST=$2
DAYDIFF=$3
echo ""
echo "#################### Params ####################"
echo "APP_NAME: $APP_NAME"
echo "APP_HOST: $APP_HOST"
echo "DAYDIFF: $DAYDIFF"
if [ -z $APP_NAME ] || [ -z $APP_HOST ] || [ -z $DAYDIFF ];then
	echo ""
	echo "#################### Usage ####################"
	echo "sh script.sh APP_NAME APP_HOST DAYDIFF"
	echo ""
	exit 1
fi

DATE=`date -d "-$DAYDIFF day" +%Y-%m-%d`
echo ""
echo "#################### Execute ####################"
echo "/usr/bin/pssh -g $APP_HOST \"rm -f /home/www/$APP_NAME/logs/localhost_access_log.$DATE.txt\""

/usr/bin/pssh -g $APP_HOST "rm -f /home/www/$APP_NAME/.server/logs/localhost_access_log.$DATE.txt"

echo ""
echo "#################### End ####################"