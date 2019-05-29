import java.util.ArrayList;

public class Game {

  // En tvådimensionell array som håller spelbrädets tillstånd.
  char[][] board;

  // Arrays som håller alla olika spelobjekt.
  private Player[] players;
  private Pizza[] pizzas;
  private Table[] tables;
  private Oven[] ovens;
  private Counter[] counters;

  // Indikerar vilken spelares tur det är genom index i arrayen players. 0 är spelare 1 (chef), 1 är spelare 2 (waiter).
  private int currentPlayerTurn;

  public Game() {
    board = new char[80][24];

    // Instantierar alla objekten i spelet.
    players = new Player[] {new Player(50, 5, 0), new Player(20, 3, 1)};
    pizzas = new Pizza[] {new Pizza(50, 14), new Pizza(53, 10), new Pizza(45, 16),
                          new Pizza(49, 12), new Pizza(55, 13)};
    tables = new Table[] {new Table(10, 5), new Table(10, 13), new Table(26, 5),
                          new Table(26, 13), new Table(18, 9)};
    ovens = new Oven[] {new Oven(59, 2), new Oven(59, 5)};
    counters = new Counter[] {new Counter(41, 4), new Counter(41, 5), new Counter(41, 6)};

    // För att testa game over:
//    players = new Player[] {new Player(42, 5, 0), new Player(40, 5, 1)};
//    pizzas = new Pizza[] {new Pizza(45, 7)};
//    tables = new Table[] {new Table(37, 5)};
//    ovens = new Oven[] {new Oven(45, 3)};
//    counters = new Counter[] {new Counter(41, 4), new Counter(41, 5), new Counter(41, 6)};

    // Spelet börjar med att det är spelare etts tur.
    currentPlayerTurn = 0;

    // Populera spelbrädesmatrisen.
    BoardGenerator.populateBoard(this, board, counters, currentPlayerTurn, ovens, pizzas, players, tables);
  }

  // Uppdaterar spelets tillstånd.
  public void update() {
    for (Oven oven : ovens) {
      oven.tick();
    }
    BoardGenerator.populateBoard(this, board, counters, currentPlayerTurn, ovens, pizzas, players, tables);
  }

  // Uppdatera spelet utefter den förflyttning som skickas som indata.
  public void move(char move) {
    if (moveIsLegal(move)) {
      switch (move) {
        case 'U':
          players[currentPlayerTurn].moveUp();
          break;
        case 'R':
          players[currentPlayerTurn].moveRight();
          break;
        case 'D':
          players[currentPlayerTurn].moveDown();
          break;
        case 'L':
          players[currentPlayerTurn].moveLeft();
          break;
      }
    }
  }

  // Uppdatera spelet utefter den interaktion som just utfördes.
  public void interact() {
    // Hitta spelarens närliggande objekt.
    GameObject[] adjacentObjects = getCurrentPlayerAdjacentObjects();

    // Om spelaren har pizza i händerna.
    if (players[currentPlayerTurn].getHands() != null) {
      // Kolla igenom alla närliggande objekt.
      for (GameObject object : adjacentObjects) {
        if (object instanceof Oven) { // Om objektet är en ugn.
          Oven oven = (Oven) object;
          if (oven.getStatus() == Oven.OvenStatus.EMPTY) { // Om ugnen är tom.
            // Fyll ugnen med spelarens pizza och töm spelarens händer.
            oven.fill(players[currentPlayerTurn].emptyHands());
            break;
          }
        } else if (object instanceof Table) { // Om objektet är ett bord.
          Table table = (Table) object;
          if (table.getStatus() == Table.TableStatus.EMPTY) { // Om bordet är tomt.
            // Placera spelarens pizza på bordet och töm spelarens händer.
            table.serve(players[currentPlayerTurn].emptyHands());
            break;
          }
        } else if (object instanceof Counter) { // Om objektet är en disk.
          Counter counter = (Counter) object;
          if (counter.getStatus() == Counter.CounterStatus.EMPTY) { // Om disken är tom.
            // Placera spelarens pizza på disken och töm spelarens händer.
            counter.occupy(players[currentPlayerTurn].emptyHands());
            break;
          }
        }
      }

      // Om spelaren fortfarance har pizza i händerna efter att ha kollat efter lediga objekt.
      if (players[currentPlayerTurn].getHands() != null) {
        int playerXPos = players[currentPlayerTurn].getXPos();
        int playerYPos = players[currentPlayerTurn].getYPos();
        boolean pizzaIsPlaced = false;
        // Kolla igenom alla närliggande golvrutor.
        for (int i = -1; i <= 1; i++) {
          for (int j = -1; j <= 1; j++) {
            // Om golvrutan är tillåten att placera pizzan på och spelaren inte står på rutan.
            if (characterIsWalkable(board[playerXPos + j][playerYPos + i]) && (i != 0 && j != 0)) {
              Pizza pizza = players[currentPlayerTurn].emptyHands(); // Ta pizzan från spelarens händer.
              pizza.setPosition(playerXPos + j, playerYPos + i); // Ändra pizzans position till golvrutans koordinater.
              pizzaIsPlaced = true; // Markera att pizzan blivit placerat, och båda for-looparna ska brytas.
              break;
            }
          }
          // Sluta försöka placera ut pizzor när en pizza redan placerats ut.
          if (pizzaIsPlaced) {
            break;
          }
        }
      }
    } else { // Om spelaren inte har pizza i händerna.
      // Kolla igenom alla närliggande objekt.
      for (GameObject object : adjacentObjects) {
        if (object instanceof Oven) { // Om objektet är en ugn.
          Oven oven = (Oven) object;
          if (oven.getStatus() == Oven.OvenStatus.FULL) {
            players[currentPlayerTurn].setHands(oven.empty());
            break;
          }
        } else if (object instanceof Table) { // Om objektet är ett bord.
          Table table = (Table) object;
          if (table.getStatus() == Table.TableStatus.SERVED) {
            players[currentPlayerTurn].setHands(table.retreive());
            break;
          }
        } else if (object instanceof Counter) { // Om objektet är en disk.
          Counter counter = (Counter) object;
          if (counter.getStatus() == Counter.CounterStatus.OCCUPIED) {
            players[currentPlayerTurn].setHands(counter.retreive());
            break;
          }
        }
      }

      // Om spelaren fortfarance har tomma händer efter att ha kollat efter pizzor i närliggande objekt.
      if (players[currentPlayerTurn].getHands() == null) {
        int playerXPos = players[currentPlayerTurn].getXPos();
        int playerYPos = players[currentPlayerTurn].getYPos();
        // Kolla igenom alla närliggande golvrutor.
        for (GameObject object : adjacentObjects) {
          if (object instanceof Pizza) {
            Pizza pizza = (Pizza) object;
            players[currentPlayerTurn].setHands(pizza);
            break;
          }
        }
      }
    }
  }

  // Genererar och returnerar en spelplan utan att utföra några drag.
  public String getBoard() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        sb.append(board[i][j]);
      }
    }
    return sb.toString();
  }

  // Returnerar huruvida spelet är slut eller inte. Spelet är slut när alla bord blivit serverade.
  public boolean gameIsOver() {
    for (Table table : tables) {
      if (table.getStatus() != Table.TableStatus.SERVED) {
        return false;
      }
    }
    return true;
  }

  public int getCurrentPlayerTurn() {
    return currentPlayerTurn;
  }

  public void changeCurrentPlayerTurn() {
    currentPlayerTurn = Math.abs(currentPlayerTurn - 1);
  }

  // Returnerar true om den givna förflyttningen är tillåten.
  private boolean moveIsLegal(char move) {
    int playerXPos = players[currentPlayerTurn].getXPos();
    int playerYPos = players[currentPlayerTurn].getYPos();
    switch (move) {
      case 'U':
        // Titta på rutan ovanför spelaren.
        if (!characterIsWalkable(board[playerXPos][playerYPos - 1])) {
          return false;
        }
        break;
      case 'R':
        // Titta på rutan till höger om spelaren.
        if (!characterIsWalkable(board[playerXPos + 1][playerYPos])) {
          return false;
        }
        break;
      case 'D':
        // Titta på rutan under spelaren.
        if (!characterIsWalkable(board[playerXPos][playerYPos + 1])) {
          return false;
        }
        break;
      case 'L':
        // Titta på rutan till vänster om spelaren.
        if (!characterIsWalkable(board[playerXPos - 1][playerYPos])) {
          return false;
        }
        break;
    }
    // Om förflyttningen inte upptäcktes vara otillåten, returnerna true.
    return true;
  }

  // Returnerar true om det givna tecknet är en punkt, och alltså en golvruta som går att gå på.
  private boolean characterIsWalkable(char character) {
    return character == '.';
  }

  // Returnerar de objekt som ligger nära spelaren, alltså i en intilliggande ruta (även diagonalt).
  private GameObject[] getCurrentPlayerAdjacentObjects() {
    ArrayList<GameObject> adjacentObjects = new ArrayList<GameObject>();
    // Kolla igenom alla objekt. Om de ligger bredvid spelaren läggs de till i listan.

    for (GameObject pizza : pizzas) {
      if (isAdjacentToCurrentPlayer(pizza)) {
        adjacentObjects.add(pizza);
      }
    }

    for (Table table : tables) {
      if (isAdjacentToCurrentPlayer(table)) {
        adjacentObjects.add(table);
      }
    }

    for (Oven oven : ovens) {
      if (isAdjacentToCurrentPlayer(oven)) {
        adjacentObjects.add(oven);
      }
    }

    for (Counter counter : counters) {
      if (isAdjacentToCurrentPlayer(counter)) {
        adjacentObjects.add(counter);
      }
    }
    // Returnera alla intilliggande objekt i form av en array.
    GameObject[] result = new GameObject[adjacentObjects.size()];
    return adjacentObjects.toArray(result);
  }

  // Returnerar true om avståndet d mellan objektet och nuvarande spelare är -1 <= d <= 1.
  private boolean isAdjacentToCurrentPlayer(GameObject object) {
    int playerXPos = players[currentPlayerTurn].getXPos();
    int playerYPos = players[currentPlayerTurn].getYPos();
    return Math.abs(object.getXPos() - playerXPos) <= 1 && Math.abs(object.getYPos() - playerYPos) <= 1;
  }
}
