# CS 425 / ECE 428 MP1 - Distributed Log Querier

## Description
Covfefe! Inc. has requested a distributed log querier to help debug their system. This application runs on a distributed system of 10 virtual machines, each with a log file named vmx.log where x represents the machine number. It allows the machines to act as both client and server to query the logs on all machines in the system. The user simply types their query into the client machine in the form of a grep command. 


## Usage
To run the application, open a terminal on each machine that will be used. If a single machine will act as both a client and a server, a separate terminal must be opened for the client and server respectfully.

In each terminal, navigate to `/home/mjmoy2/mp1/src`.

Please check to make sure you are in the `main` branch. 

For each Server, compile using `javac Server.java`, then start the server with `java Server`. 

For each client, compile using `javac Client.java`, then start the client using `java client`. 

The client terminal should prompt the user to enter a query. Type `-desiredflags  'insert query phrase here'` and hit enter to send the query.

The results of query will be printed to the client's terminal. It will consist of the number of matching lines found in each VM, along with the total matching lines found in the whole system. 

After the results of each query are printed, the user will be prompted to enter a new query. To stop the client, type `exit`. 

## Unit Tests
There are a few unit tests included in this project located at `/home/mjmoy2/mp1/tests`. Two are currently complete and there are a few that are in progress.


The completed tests include -

**ServerTest.java**

This test creates a simple server and client using your laptop localhost address "127.0.0.1". When the client starts, it attempts to connect to the server and sends a message. The server replies with a message if the client successfully connected and its message was received by the server. The test uses the server reply to determine if the test passes. 

**QueryTestVM1234.java**

This test file requires the user to first start servers on VMs 1-4. 

The first test in this file checks whether the correct servers are marked as active / inactive when a client is made and given a test query. The result should be VMs 1-4 are marked as active, and VMs 5-10 are inactive. If this test fails, all other tests will fail. Verify that only VMs 1-4 are running servers, run this test again and verify it passes before trying the following tests.

The next tests send queries using phrases with varying frequency such as  - 
* Frequent query 'a'
* Frequent query 'com'
* Infrequent query 'org'
* Infrequent query 'wang'

It tests whether the query was processed correctly by comparing the total line counts for VMs 1-4 against the expected result from running the grep command with the same query outside the application. 

## Report
There is a pdf report included with this application. The report discusses the overall design of the application, a brief description of unit tests, and an analysis on average query latency when running QueryTestVM1234.java. 

In the report, only 6/8 of the query tests provided in the QueryTestVM1234.java file are used. This is because infrequent queries using 'wang', 'snyder', and 'miller' give results that appear very close together when plotted on a graph. 'wang' had the lowest total line occurrence count, so the 'wang' test was selected for latency analysis. 

## Notes
* Log files should be located at `/home/mjmoy2/mp1/logfiles`.
* The application assumes only 1 log file with the name `vmx.log` (where x is a number 1-10 corresponding to the vm number) will exist in the logfile folder.
* Once a machine is down, the application will mark it as inactive and assume that it remains down. So further queries will not attempt to query that server until a new client is started. 
* The application assumes the client machine will not fail. 


## Creators
* Harsh Agarwal
* Maya Moy
