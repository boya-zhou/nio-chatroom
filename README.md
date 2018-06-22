# nio-chatroom
A single server multi clients chat room based on java nio

 Specifications implemented
 * The server
    * all requests from clients showed in server side
    * server handle connection/ disconnection with out disrup other clients
    * inform changes to all clients
 * The client
    * list of online user displayed to all user
    * connection/disconnection displayed to all user
    * chat contents displayed
    * able to receive message while typing a message
    * disconnect with out interrupt server
 
 
 points worth focus and improvements
 *  server side synchronization
 *  exception handling
 
 
 design pattern usesd
 * template method pattern
 * factory design pattern