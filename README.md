# TCPClientServer
Simple TCP Client-Server application fot JetBrains Internship test task

This appliacation uses sockets to communicate between client and server.
Telnet support included (connection closes by "exit" keyword)

Runnable .jar stored in jars directory
## **Usage:** </br>
java -jar fib.jar -c | -s -h \<host\> -p \<port\> </br>
         &nbsp;&nbsp;To run **server**: </br>
         &nbsp;&nbsp;&nbsp; fib.jar -s -p 7777 </br>
         &nbsp;&nbsp;&nbsp;fib.jar --server --port 1234 </br>
         &nbsp;&nbsp;To run **client**: </br>
                   &nbsp;&nbsp;&nbsp;fib.jar -c -h localhot -p 7777 </br>
                   &nbsp;&nbsp;&nbsp;fib.jar --client --port 1234 --host localhost</br>
