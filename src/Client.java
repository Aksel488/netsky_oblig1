import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {

        try {
            //open a socket
            System.out.println("Client started");
            Socket soc = new Socket("localhost", 9806);

            //get text from user
            BufferedReader userInnput = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("write a URL");
            String str = userInnput.readLine();

            //String str = "https://www.cs.hioa.no/data/bachelorprosjekt/reg/";
            System.out.println();

            //sending string to server
            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
            out.println(str);

            //reading answer from server
            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            String code = in.readLine();
            if (code.equals("0")) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                }
            } else if (code.equals("1")) {
                System.out.println("!!!No email address found on the page!!!");
            } else if (code.equals("2")) {
                System.out.println("!!!Server couldn’t find the web page!!!");
            } else {
                System.out.println("Something went wrong");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}























