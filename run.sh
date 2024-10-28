#!/bin/sh
#RUNNING ARGS: java TestCUNRCCP.java <URL> <timeout> <pfxFile> <pfxPassword> <jksFile> <jksPassword> <disableCertificateValidation>
java TestCYBRCCP.java \
    'https://172.16.100.21/AIMWebService/api/Accounts?AppID=TestCCP-HUYDO&Safe=TestingCP_Safe&Folder=Root&Object=testcp01' \
    /Users/Huy.Do/Documents/HUYDO/Working/Coding/TestCybrCCP/test/clients.cybr.huydo.net.pfx \
    ChangeMe123! \
    /Users/Huy.Do/Documents/HUYDO/Working/Coding/TestCybrCCP/test/trust.ccp.jks \
    ChangeMe123! \
    true
