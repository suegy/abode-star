package abode.debug;

import java.io.IOException;
import java.net.ServerSocket;

public class ExecutionTracer implements Runnable{

	private ServerSocket serverSocket;
	private Thread serverThread;
	private boolean active;
	
	public ExecutionTracer() {
		
	}
	
	public boolean initServer(int port) {
		try {
		    serverSocket = new ServerSocket(port);
		    serverSocket.accept();
		    active = true;
		} 
		catch (IOException e) {
		    System.out.println("Could not listen on port: "+ port);
		    System.exit(-1);
		    return false;
		}
		
		serverThread = new Thread(this);
		
		return true;
	}
	
	protected boolean disconnect() {
		if (serverSocket instanceof ServerSocket && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
				active  = false;
				
			} catch (IOException e) {
				System.err.println("Could not close server socket");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
			}
		return false;
	}

	@Override
	public void run() {
		while(active) {
			readExecutionData();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				System.err.println("ExecutionTracer will not sleep!");
			}
		}
	}

	private void readExecutionData() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
