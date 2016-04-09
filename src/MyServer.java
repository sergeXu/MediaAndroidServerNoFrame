
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.net.*;
public class MyServer {
	
	 public static void main(String[] args)
	   {
		 workThreads w= new workThreads();
			try {
				w.serverRun();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("IOException in main\n");
			}
	   }
		 	
}
