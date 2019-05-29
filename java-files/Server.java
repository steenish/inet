import org.json.JSONObject;

import java.io.*;
import java.net.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

// En klient som representerar en server för spelet.
public class Server {
  // Mainmetoden exekveras för att köra servern.
  public static void main(String arg[]) throws IOException {
    System.out.println("Starting server...");
    Server server = new Server();
    while (true) {
      try {
        // Starta serverns process.
        server.host();
      } catch (SocketTimeoutException e) {
        // Om det blir timeout.
        System.out.println("Connection timed out.");
      } catch (BadResponseException e) {
        // Någon data som skickas eller tas emot följer inte specifikationen.
        System.out.println("Incorrect communication.");
      } catch (SocketException e) {
        System.out.println("Connection to a client was broken.");
      } catch (Exception e) {
        System.out.println("An unexpected error occurred.");
        e.printStackTrace();
      }
      // Bryt anslutningarna och återställ spelet.
      System.out.println("Breaking connections to clients.");
      try {
        server.closeConnections();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private ServerSocket serverSocket;
  private ClientContainer[] clients;

  // Skapar en server med en serversocket på port 1337, timeout på 60 sekunder och inga klienter.
  public Server() throws IOException {
    serverSocket = new ServerSocket(1337);
    serverSocket.setSoTimeout(60000); // Sätt en timeout på 60 sekunder.
    clients = null;
  }

  // Hostar spelet genom att ansluta till klienter, skicka till och ta emot från dem och uppdatera spelet.
  public void host() throws Exception {
    System.out.println("Server started.");
    System.out.println("Connecting to clients...");
    connectToClients(); // Lyssna efter anslutningar via socket på port 1337, vänta på två anslutningar.
    System.out.println("Clients connected.");
    System.out.println("Initializing game...");
    Game game = new Game();
    System.out.println("Game initialized.");

    // Loopen som håller igång send/receive-processen.
    while (true) {
      int currentClientTurn = game.getCurrentPlayerTurn();
      System.out.println("Currently client " + (currentClientTurn + 1) + "'s turn.");

      // Skicka spelplanen och att det är klienten vars tur det ärs tur till klienten vars tur det är.
      // Skapar ett JSON-objekt och sätter fälten enligt specifikation.
      JSONObject toCurrentClient = new JSONObject();
      toCurrentClient.put("yourTurn", true);
      toCurrentClient.put("gameOver", game.gameIsOver());
      toCurrentClient.put("board", game.getBoard());
      clients[currentClientTurn].send(toCurrentClient.toString() + "\n");

      System.out.println("Sent packet to client " + (currentClientTurn + 1) + ".");

      // Skicka spelplanen och att det inte är klientens tur till klienten vars tur det inte är.
      // Skapar ett JSON-objekt och sätter fälten enligt specifikation.
      JSONObject toOtherClient = new JSONObject();
      toOtherClient.put("yourTurn", false);
      toOtherClient.put("gameOver", game.gameIsOver());
      toOtherClient.put("board", game.getBoard());
      clients[Math.abs(currentClientTurn - 1)].send(toOtherClient.toString() + "\n");

      System.out.println("Sent packet to client " + (Math.abs(currentClientTurn - 1) + 1) + ".");

      // Kontrollera huruvida spelet är slut.
      if (game.gameIsOver()) {
        System.out.println("Game is over. Restarting...");
        break;
      }

      System.out.println("Waiting for response from " + (currentClientTurn + 1) + ".");

      //Vänta på svar från klienten vars tur det är via socket, ignorera all annan kommunikation.
      String clientResponse = clients[currentClientTurn].receive();

      System.out.println("Response received.");

      // Kontrollera att datan som klienten skickade är formatterad enligt specifikationen.
      if (clientResponse == null || !responseIsCorrect(clientResponse)) {
        throw new BadResponseException();
      }

      // Konvertera klientens data till ett JSON-objekt och extrahera datan.
      JSONObject clientJSON = new JSONObject(clientResponse);
      Object potentialMove = clientJSON.get("move");
      Character move = null; // Move hanteras speciellt eftersom den kan vara null.
      if (potentialMove != JSONObject.NULL) {
        move = ((String) potentialMove).charAt(0);
      }
      boolean interact = clientJSON.getBoolean("interact");

      // Utför klienten vars tur det ärs drag.
      if (interact) {
        game.interact();
      } else {
        game.move(move);
      }

      // Ändra vilken klients tur det är.
      game.changeCurrentPlayerTurn();

      // Uppdatera spelets tillstånd.
      game.update();
    }
  }

  // Ansluter till två klienter.
  private void connectToClients() throws IOException {
    Socket connection1 = null;
    Socket connection2 = null;

    // Vänta på två anslutningar.
    while (connection1 == null || connection2 == null) {
      // Om klient 1 inte är ansluten, acceptera en anslutning och tilldela den till klient 1.
      if (connection1 == null) {
        connection1 = serverSocket.accept();
      }
      // Samma sak för klient 2.
      if (connection2 == null) {
        connection2 = serverSocket.accept();
      }
    }

    // Populera listan med klienter.
    clients = new ClientContainer[] {
      new ClientContainer(connection1),
      new ClientContainer(connection2)
    };
  }

  // Returnerar true om den givna datan som klienten skickade var korrekt formatterad.
  private boolean responseIsCorrect(String response) {
    // Se till att datan från klienten vars tur det är är en korrekt formaterad sträng.
    String regex = "((\\{\"move\":\"[UDLR]\",\"interact\":false})|(\\{\"move\":null,\"interact\":true}))";
    Matcher matcher = Pattern.compile(regex).matcher(response);
    return matcher.matches();
  }

  // Stänger anslutningarna till klienterna.
  private void closeConnections() throws IOException {
    if (clients != null) {
      for (ClientContainer client : clients) {
        if (client != null) {
          client.close();
        }
      }
      clients = null;
    }
  }
}
