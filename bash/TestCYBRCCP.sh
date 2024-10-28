#!/bin/bash

curl -k --header 'application/json; charset=utf-8' \
    --key /Users/Huy.Do/Documents/HUYDO/Working/Coding/TestCybrCCP/test/clients.cybr.huydo.net.key \
    --cert /Users/Huy.Do/Documents/HUYDO/Working/Coding/TestCybrCCP/test/clients.cybr.huydo.net.crt \
    'https://172.16.100.21/AIMWebService/api/Accounts?AppID=TestCCP-HUYDO&Safe=TestingCP_Safe&Folder=Root&Object=testcp01'
