import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import javax.swing.*;

// En klass som representerar en klient för spelet.
public class Client {
  // Mainmetoden exekveras för att köra klienten.
  public static void main(String[] args) throws IOException {
    Client client = new Client();

    try {
      // Starta klientens process.
      client.play();
    } catch (SocketTimeoutException e) {
      // Om det blir timeout, bryt anslutningen genom att stänga socket och avsluta programmet.
      System.out.println("Connection timed out. Exiting.");
    } catch (ConnectException e) {
      // Om ingen server hittas på den givna socketen.
      System.out.println("Connection refused, no server running. Exiting.");
    } catch (BadResponseException e) {
      // Någon data som skickas eller tas emot följer inte specifikationen.
      System.out.println("Incorrect communication. Exiting.");
    } catch (InterruptedException e) {
      // Om något går fel med sleep.
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      // För debugging.
      System.out.println("An unexpected error occurred.");
      e.printStackTrace();
    } finally {
      // Bryt anslutningen.
      if (client.clientSocket != null) {
        try {
          client.clientSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      // Stäng ned terminalen.
      if (client.terminal != null) {
        try {
          client.terminal.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private Socket clientSocket;
  private Terminal terminal;

  // Skapar en klient med en null server och null terminal.
  public Client() {
    clientSocket = new Socket();
    terminal = null;
  }

  // Spelar spelet genom att skicka till och ta emot från servern och skriva ut spelplanen.
  public void play() throws Exception {
    int timeout = 60000; // Sätt en timeout på 60 sekunder.
    // Öppna en socket till serverns IP-adress och port 1337.
    clientSocket.connect(new InetSocketAddress("localhost", 1337), timeout);
    // Skapa en utdataström för att skriva till servern och en indataström för att ta emot från severn.
    DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
    BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    // Skapa och konfigurera terminalen.
    DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
    terminal = defaultTerminalFactory.createTerminal();
    terminal.enterPrivateMode();
    terminal.clearScreen();
    terminal.setCursorVisible(false);
    ((SwingTerminalFrame) terminal).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Loopen som håller igång send/receive-processen.
    while (true) {
      // Vänta på att servern skickar data.
      String serverResponse = fromServer.readLine();

      // Se till att servern har skickat en spelplan som följer specifikationen, annars kasta ett exception och avsluta programmet.
      if (serverResponse == null || !responseIsCorrect(serverResponse)) {
        throw new BadResponseException();
      }

      // Konvertera serverns data till ett JSON-objekt och extrahera datan.
      JSONObject serverJSON = new JSONObject(serverResponse);
      boolean yourTurn = serverJSON.getBoolean("yourTurn");
      boolean gameOver = serverJSON.getBoolean("gameOver");
      String board = serverJSON.getString("board");

      // Printa spelplanen.
      printBoard(board);

      // Om servern skickat att spelet är slut, visa den
      // slutgiltiga spelplanen i fem sekunder och avsluta programmet.
      if (gameOver) {
        Thread.sleep(5000);
        break; // Bryter loopen och avslutar spelet.
      }

      // Om det är klientens tur, lyssna efter input från spelaren.
      if (yourTurn) {
        String formattedInput = generateResponse(); // Formattera spelarens input enligt specifikationen.
        toServer.writeBytes(formattedInput); // Skicka datan till servern.
      }
    }

  }

  // Returnerar true om den givna datan som servern skickade var korrekt formatterad.
  private boolean responseIsCorrect(String response) {
    // Se till att servern har skickat en spelplan som följer specifikationen.
    String regex = "\\{\"yourTurn\":((true)|(false)),\"gameOver\":((true)|(false)),\"board\":\".{1920}\"}";
    Matcher matcher = Pattern.compile(regex).matcher(response);
    return matcher.matches();
  }

  // Skriver ut spelplanssträngen i terminalfönstret enligt specifikationen.
  private void printBoard(String board) throws IOException {
    int k = 0;
    for (int i = 0; i < 80; i++) {
      for (int j = 0; j < 24; j++) {
        terminal.setCursorPosition(i, j);
        terminal.putCharacter(board.charAt(k));
        k++;
      }
    }
    terminal.flush();
  }

  // Läser indata från spelaren och returnerar det svar som klienten ska skicka formatterat enligt specifikation.
  private String generateResponse() throws IOException {
    // Läs indata.
    com.googlecode.lanterna.input.KeyStroke input = null;
    while (terminal.pollInput() != null);
    while (!inputIsCorrect(input)) {
      input = terminal.readInput();
    }

    // Formattera svaret och returnera det.
    return formatJSONResponse(input);
  }

  // Returnerar true om tangenten spelaren tryckt på är en meningsfull knapp för spelet.
  private boolean inputIsCorrect(com.googlecode.lanterna.input.KeyStroke input) {
    return input != null && (input.equals(new KeyStroke(KeyType.ArrowDown)) || input.equals(new KeyStroke(KeyType.ArrowLeft))
                                                                            || input.equals(new KeyStroke(KeyType.ArrowRight))
                                                                            || input.equals(new KeyStroke(KeyType.ArrowUp))
                                                                            || input.equals(new KeyStroke(KeyType.Enter)));
  }

  // Returnerar en sträng formatterad som kompakt JSON och följer specifikationen.
  private String formatJSONResponse(com.googlecode.lanterna.input.KeyStroke input) {
    JSONObject result = new JSONObject();
    // Konvertera koden för knappen som spelaren tryck på till ett tecken eller null.
    Character convertedInput = convertInput(input);

    if (convertedInput != null) { // Om spelaren valt att flytta på sig.
      // Skapa ett svar som motsvarar förflyttningen.
      result.put("move", convertedInput);
      result.put("interact", false);
    } else {
      // Skapa ett svar som motsvarar en interaktion.
      result.put("move", JSONObject.NULL);
      result.put("interact", true);
    }

    // Konvertera JSON-objektet till en sträng och lägg till ett nyradstecken.
    return result.toString() + '\n';
  }

  // Returnerar ett tecken som motsvarar den tangent spelaren tryckte på.
  private Character convertInput(com.googlecode.lanterna.input.KeyStroke input) {
    if (input.equals(new KeyStroke(KeyType.ArrowDown))) {
      return 'D';
    } else if (input.equals(new KeyStroke(KeyType.ArrowLeft))) {
      return 'L';
    } else if (input.equals(new KeyStroke(KeyType.ArrowRight))) {
      return 'R';
    } else if (input.equals(new KeyStroke(KeyType.ArrowUp))) {
      return 'U';
    } else {
      return null;
    }
  }
}
