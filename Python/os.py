#!/usr/bin/python
# -*- coding: utf-8 -*-
###########################
#  操作系统相关操作
###########################
import os

# 遍历目录指定
def traversePath(targetPath):
    if not os.path.exists(targetPath):
        print("目标目录不存在")
        return
    for (root, dirs, files) in os.walk(targetPath):
        print(root)
        print(dirs)
        print(files)
    

if __name__ == '__main__':
    traversePath("/Users/chentairan/Dropbox/enterprise/work/gitlabapi/")