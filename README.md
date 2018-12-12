# Application_Load_Balancer
An application load balancer for a group chat application. Chat application uses Socket.io & Load balancer is built using Node.js.
The goal of the project was to apply Horizontal scaling,So that the number of connection to application is only limited by 
the hardware limitations.

Various instance of chat applications were launched on different machines and an application load balancer was used to distribute the
requests load between them. Further a basic message queue was used to form a connection b/w different app intances.

A laod test was performed using Artillery.io to create virtual sockets. Virtual sockets(user) send a test message 
and disconnects after certain time.

project video Link:
https://drive.google.com/file/d/1X6BJYGIKz1JepqQ3S9d9eSzauaDunXMv/view?usp=sharing

Packages used for managing and testing.
*PM2 process manager:
  http://pm2.keymetrics.io/  
*Load Testing:
  https://artillery.io/
