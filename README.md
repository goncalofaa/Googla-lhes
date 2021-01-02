# Googla-lhes-2019
Desktop grid wich allows to search words on a big set of news articles.
In this project it is intended that a desktop grid be developed that allows the search for words in a large set of news.

Grid Computing
Grid computing is a distributed form of computing where there are several workers (Workers) who perform the tasks defined by a customer. In the case of a desktop grid these workers run on an organization's computers when they are not being used. In addition to the workers, there is a server that ensures the entire management of job assignments to be made to workers and to return responses to customers. Clients connect to the server and send them the tasks they want to see performed by workers and wait for the server to send them the results of the run.

System Description
The application to be implemented allows using a desktop grid to search for words in a group news. So the user must run a client that allows him to enter the words and start the demand. The client must send this word to the server that will create tasks that are performed by the workers. Each task consists of searching for an expression or phrase in a news story. O worker must return to the server a list containing all indexes of occurrences of the word in the news text. After all tasks have been performed, the server groups the results and sends to the customer. The information that the server must send to the client consists of a list with the titles the news in which the word occurs as well as a list of the occurrence indexes for each of that news. After receiving the headlines of the news where the word appears, the client shows the these results. When the user selects one of the news from the list, the customer must send a message to the server asking for the news text. When the server starts up it should read all the news that are part of the corpus. These news are in a set of files in a folder that is passed to the server. The server on receiving a new search request will create a set of tasks, one for each news item, consisting of to look for the expression in one of the news.

Server
The solution to be developed should follow a client-server architecture. There should be a server to which the various clients and workers will be connected. The server must accept requests for connection by clients and workers, through a TCP / IP connection in a port with a well-defined number (and knowledge of customers and workers). When a connection, clients and workers inform the server whether they are clients or workers. For the purposes of For simplification, no authentication process is required for clients and workers. The server must operate in multi-threading, allowing to receive new connections, to receive requests from multiple clients and send jobs to workers. To do this, you must create a set of threads that allow the server to perform all these tasks concurrently. The server must be able to be launched in a normal Java process, for example by running:

java Server news_ folder

The Server starts and loads all the news in the news_ folder into memory. After read the news the server is available to receive calls from clients and workers. If a client connects, the server must be available to accept new requests from that client. When you receive one of these requests, you must create a set of tasks to be submitted to workers. Each of these tasks consists of looking for expression in each of the news read from the news folder. The news where the expression to be found was found, must be ordered by relevance, number of times the word occurs, and sent to the client. If a worker connects, the server must be available to receive job requests. If you receive an order must send the next task that has not yet been executed to the worker and wait for the result. When you receive the result it must be placed in the results list. When all the results of the question from a given client have reached the server this must return the response to the customer.

Client
The client must be able to be launched in a normal Java process, for example by executing:

java Client server_server

Where server_address is the IP address of the machine where the server is running. When the client is launched, it must connect to the server and send the indication that it is a client. After startup should show the graphical interface which should contain the following elements:

• Text box where the user can type the expression to search for
• Button to start the search
• List of news search results
• Text area where the text of the news selected from the results list appears
In the list of answers it should be possible to see the number of times the word appears in the news as well as the news headline.

You must implement the necessary threads so that the client application can wait for the results without blocking the graphical interface.

Worker
The worker should be able to be launched in a normal Java process, for example by executing:

java Worker server_server

Where server_address is the IP address of the machine where the server is running. When the worker is launched must connect to the server and send the indication that it is a worker. After making the startup should repeat the following steps:

wait for a new task,
perform the task,
return the result of its execution to the server.
If the connection to the server fails the worker should try to reestablish this connection by ignoring the job that you may have done in the meantime.
