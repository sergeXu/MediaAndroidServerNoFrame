import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JTextArea;

/**
 * This class handles the client input for one server socket connection.
 */
class ThreadedEchoHandler implements Runnable {
	private Socket incoming;

	private BufferedReader br = null;

	private DataOutputStream toclient;

	/**
	 * Constructs a handler.
	 */
	public ThreadedEchoHandler(Socket i)throws IOException {
		incoming = i;
		br = new BufferedReader(new InputStreamReader(incoming.getInputStream(), "utf-8"));
	}

	public void run() {
		try {
			try {
				
				OutputStream outStream = incoming.getOutputStream();

				//return message
				//PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);

				//out.println("Hello! Enter BYE to exit.");

				// echo client input
				String msg = null;
				while ((msg = readFromClient()) != null) {

					// send message to all
					Socket s=null;
					//no list or socket
					if(workThreads.socketList==null||workThreads.socketList.size()==0) 
					{
						return;
					}
					synchronized (workThreads.socketlock) {
						//send message to all
						for(int num=0;num<workThreads.socketList.size();num++)
						//for (Socket s : ServerFrame.socketList) {		//remove error				
							try {
								s = workThreads.socketList.get(num);
								System.out.println("*** socket: "+s.getRemoteSocketAddress()+" "+s.getPort());
								if(s==null||s.isClosed())
								{
									workThreads.socketList.remove(s);
									System.out.println("*** remove socket "+s.getRemoteSocketAddress()+" "+s.getPort());
									continue;
								}							
								OutputStream os = s.getOutputStream();
								// os.write((content + "\n").getBytes("utf-8"));
								toclient = new DataOutputStream(os);
								toclient.writeUTF((msg + "\n"));
							} catch (Exception e) {
								workThreads.socketList.remove(s);
								e.printStackTrace();
								System.out.println("socketList num: "+workThreads.socketList.size());
								System.out.println("sent packege malfunctional");
							}
						}
				}
					System.out.println("sent packege finish");
			} finally {
				incoming.close();
			}
		} catch (IOException e) {			//catch incoming.close() io exception
			e.printStackTrace();
		}
	}

	private String readFromClient() {

		try {
			String content = "";
			String temp;
			// head message
			char code[] = { '1', '1', '1', '1' };
			while (((temp = br.readLine()) != null) && !(temp.equals("%%##%%"))) {
				//
				content += temp + "\n";
				// System.out.println("temp="+temp);
				temp = "";
			}
			// void data
			if (content.equals(""))
				return null;
			System.out.println("content=" + content);
			if ((content.charAt(0) == code[0]) && (content.charAt(1) == code[1]) && (content.charAt(2) == code[2])
					&& (content.charAt(3) == code[3])) {
				return content;
			} else {
				System.out.println("Invalid informations");
				return null;
			}
			// return br.readLine();

			// return fromclient.readUTF();
		}
		// if catch exception��Socket should be ended
		catch (Exception e) {
			// delete Socket
			synchronized (workThreads.socketlock) 
			{
				workThreads.socketList.remove(incoming);
			}
			e.printStackTrace();
			System.out.println("readFromClient malfunctional");
		}
		return null;
	}
}