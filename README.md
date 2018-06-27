# nio-chatroom
A single server multi clients chat room based on java nio

#### Specifications implemented
 * The server
    * all requests from clients showed in server side
    * server handle connection/ disconnection with out disrup other clients
    * inform changes to all clients
 * The client
    * list of online user displayed to all user
    * display connection/disconnection to all user
    * display chat contents
    * able to receive message while typing a message
    * disconnect with out interrupt server
 
 
#### points worth focus and improvements
 *  synchronization. Server, client both multi-threaded
    * server
        * ConcurrentHashMap for client nickname and socket channel relation shared by multi clients
    * client
        * LinkedBlockingQueue<ChatLog> shared by input and listen threads
        * ConcurrentHashMap<Receiver, ArrayBlockingQueue> same as above
    * nio tool internally multi thread safe
 *  exception handling
    * server
        * first priority : server can keep running correctly
            * what exception can disturb server?
            * what exception can make server run not correct?
        * accurate show what, where and why of exception
    * client
        * throw to top of the stack, warp the exception as human language
 *  testing
    * concurrency environment testing 
    * integrating testing
 * resource safely close
    * socket leak
    * thread 

 
 #### design pattern used
 * template method pattern 
 * static factory method(messy, any management solution?)
 
 #### SOLID principle review whole design
 