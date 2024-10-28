import http.client
import ssl
import json

ccp_host = '172.16.100.21'
ccp_api = '/AIMWebService/api/Accounts?AppID=TestCCP-HUYDO&Safe=TestingCP_Safe&Folder=Root&Object=testcp01'

#context = ssl.create_default_context()
#Using code below to load https CA
#context.load_verify_locations(cafile='C:\certs\cachain.crt')

#Using code below to disable https cert validation
context = ssl._create_unverified_context()
#Using code below to load client certificate for http request
context.load_cert_chain('../test/clients.cybr.huydo.net.crt', '../test/clients.cybr.huydo.net.key')

try:
    conn = http.client.HTTPSConnection(ccp_host, context = context)
    headers = {'Content-type': 'application/json'}
    conn.request('GET', ccp_api, '', headers)
    res = conn.getresponse()
    data = res.read()
    status_code = res.status
    conn.close()

    if status_code != 200:
        raise Exception('ERROR: Status Code: {}'.format(status_code))
 
# Capture Any Exceptions that Occur
except Exception as e:
    # Print Exception Details and Exit
    print(Exception(e))
    exit()
        
# Deal with Python dict for return variable
result = json.loads(data.decode('UTF-8'))

print('Username: {}'.format(result['UserName']))
print('Password: {}'.format(result['Content']))

