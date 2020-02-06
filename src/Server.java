import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class Server {

    public static void main(String[] args) {

        try {
            //Searching for connection
            ServerSocket ss = new ServerSocket(9806);

            //Making new socket
            while (true) {
                System.out.println("Waiting for clients.. ");
                Socket soc = ss.accept();
                System.out.println("Connection established with : "+ soc);
                Thread newClient = new Thread() {
                    @Override
                    public void run() {
                        try {
                            clientHandler(soc); // Main program
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                newClient.start(); //Start new thread for each new client
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void clientHandler(Socket soc) throws IOException, InterruptedException {
        //Reads innputstream to a string
        BufferedReader inClient = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        String str = inClient.readLine();

        //verify url
        List<String> mails = new ArrayList<>();
        PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
        int urlCheck = validURL(str);
        if (urlCheck == 0) { //valid url
            URL url = new URL(str);

            //find mail-addresses in text
            mails = findMail(mails, url);

            //Sends mail-addresses back
            if (mails.isEmpty()) {
                out.println("Found no eMail addresses on this URL : " + url);
            } else {
                for (String Mail : mails) {
                    out.println(Mail);
                }
            }
        } else if (urlCheck == 1){
            out.println("The URL is not in a valid form : "+ str);
        } else {
            out.println("A connection to "+ str +" couldn't be established");
        }
        out.close();


        /**
         * send all website text back to client
         * for debugging purposes
         */
            /*String inputLine;
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((inputLine = in.readLine()) != null) {
                out.println(inputLine);
            }
            in.close(); //close url-reader
            */

        //Closes client socket
        soc.close();
        System.out.println("Disconnected from client : "+ soc.getInetAddress() +"\n");
    }

    private static int validURL(String str) {

        try {
            URL url = new URL(str);
            URLConnection conn = url.openConnection();
            conn.connect();
            return 0;
        } catch (MalformedURLException e) {
            // the URL is not in a valid form
            return 1;
        } catch (IOException e) {
            // the connection couldn't be established
            return 2;
        }
    }

    private static List<String> findMail(List<String> mail, URL url) throws IOException {

        BufferedReader inURL = new BufferedReader(new InputStreamReader(url.openStream()));
        System.out.println("searching for emails..");

        //Uses pattern to find emails from stream
        Pattern p = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b",
                Pattern.CASE_INSENSITIVE);
        String inputLine;
        while ((inputLine = inURL.readLine()) != null) {
            Matcher matcher = p.matcher(inputLine);
            while (matcher.find()) {
                mail.add(matcher.group());
            }
        }

        return mail;
    }
}
