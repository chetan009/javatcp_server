#!/usr/bin/env python3

import socket
import time

HOST = '127.0.0.1'  # The server's hostname or IP address
PORT = 8004       # The port used by the server

for i in range(100):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((HOST, PORT))
        s.sendall(b'000100010040007CDB0841534D30313130397130000001200930FB3434EDB40B3B8DFEED5503103E18E4B33B2E69ADA7E5024920CBBBCF1072D1D3F24EDE3FCAC8E36956C1853E77B3614F13E88384850EFA5ECD9C0F91D3D0845AD459B7B4FD40248D2793C96F19B532025E64B42D544000B04627CE376C3BE24A746A116D2500F4E160')
        #time.sleep(1)


#print('Received', repr(data))