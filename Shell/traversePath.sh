#!/bin/bash
#遍历指定目录

traversePath(){
    targetPath=$1
    echo "$targetPath"
    if [ -d "$targetPath" ];then
        cd "$targetPath" || exit -1
        absolutePath=$(pwd)
    fi
    echo "$absolutePath"
    for element in $absolutePath/*
    do
        echo "$element"
    done
}

traversePath "/Users/chentairan/Dropbox/enterprise/work"
