import java.io.*;
import java.net.*;

// En klass som håller en klients socket, indataström och utdataström i syftet att höja abstraktionsnivån.
public class ClientContainer {
  private Socket socket;
  private BufferedReader fromClient;
  private DataOutputStream toClient;

  // Skapar indataström och utdataström för den givna socketen.
  public ClientContainer(Socket socket) throws IOException {
    this.socket = socket;
    fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    toClient = new DataOutputStream(socket.getOutputStream());
  }

  // Returnerar en sträng som klienten skickat. Denna metod blockerar.
  public String receive() throws IOException {
    return fromClient.readLine();
  }

  // Skickar den givna strängen till klienten.
  public void send(String output) throws IOException {
    toClient.writeBytes(output);
  }

  // Stänger klientens socket.
  public void close() throws IOException {
    socket.close();
  }
}
