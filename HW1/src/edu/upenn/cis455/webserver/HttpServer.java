package edu.upenn.cis455.webserver;

import java.io.IOException;
import java.net.*;

class HttpServer {
	static BlockingQueue taskQueue = null;
	static ServerSocket serverSocket;
	static ThreadPool threadPool;
	static boolean isTerminate;
	static int portNum;

	public static void main(String args[]) throws IOException {
		/* your code here */
		if (args.length < 2) {
			System.out.println("Name: Ao Sun");
			System.out.println("SEAS login name: sunao1");
			return;
		}
		portNum = Integer.parseInt(args[0]);
		String rootDirectory = args[1];
		System.out.println(rootDirectory);
		int noOfThreads = 10;
		int maxNoOfTasks = 1000;
		taskQueue = new BlockingQueue(maxNoOfTasks);
		org.apache.log4j.BasicConfigurator.configure();
		serverSocket = new ServerSocket(portNum, 1000);
		threadPool = new ThreadPool(noOfThreads, taskQueue, rootDirectory);
		System.out.println("waiting connections....");
		isTerminate = false; // create a flag in main thread

		while (!isTerminate) {
			Socket socket = null;

			try {
				if (!isTerminate) {
					socket = serverSocket.accept();
					socket.setSoTimeout(50000);
				}
			} catch (IOException e) {
				System.err.println("Accept failed.");
			}

			try {
				taskQueue.enqueue(socket);
				System.out.println("enqueue task");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		threadPool.stop();
		serverSocket.close();

		System.out.println("All threads terminated");

	}

	public synchronized static void stop() {
		if (isTerminate) {
			return;
		}
		isTerminate = true;
		threadPool.stop();
		while (!threadPool.isStop()) {
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
