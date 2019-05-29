import java.util.Arrays;

public class BoardGenerator {
  public static void populateBoard(Game game, char[][] board, Counter[] counters, int currentPlayerTurn, Oven[] ovens, Pizza[] pizzas, Player[] players, Table[] tables) {
    // Tomma rutor ska vara mellanrum.
    for (int i = 0; i < 80; i++) {
      Arrays.fill(board[i], ' ');
    }

    // Golvet
    for (int i = 0; i < 60; i++) {
      for (int j = 0; j < 18; j++) {
        board[i][j] = '.';
      }
    }

    // Kanter
    for (int i = 0; i < 80; i++) {
      board[i][0] = '#';
      board[i][18] = '#';
      board[i][23] = '#';
    }

    for (int i = 0; i < 24; i++) {
      board[0][i] = '#';
      board[79][i] = '#';
    }

    for (int i = 0; i < 18; i++) {
      board[60][i] = '#';
    }

    // Väggar
    for (int i = 0; i < 18; i++) {
      board[40][i] = '#';
      board[41][i] = '#';
      board[42][i] = '#';
    }

    for (int i = 44; i < 60; i++) {
      board[i][8] = '#';
    }

    // Bardisk (golv)
    for (int i = 4; i < 7; i++) {
      board[40][i] = '.';
      board[42][i] = '.';
    }

    // Bardisk
    for (Counter counter : counters) {
      board[counter.getXPos()][counter.getYPos()] = counter.getCharacter();
    }

    // Spelare
    for (Player player : players) {
      board[player.getXPos()][player.getYPos()] = player.getCharacter();
    }

    // Ugnar
    for (Oven oven : ovens) {
      board[oven.getXPos()][oven.getYPos()] = oven.getCharacter();
    }

    // Bord
    for (Table table : tables) {
      board[table.getXPos()][table.getYPos()] = table.getCharacter();
    }

    // Pizzor
    for (Pizza pizza : pizzas) {
      if (pizza.getIsPlaced()) {
        board[pizza.getXPos()][pizza.getYPos()] = pizza.getCharacter();
      }
    }

    // Teckenförklaring
    board[62][1] = '@';
    board[64][1] = '-';
    board[66][1] = 'c';
    board[67][1] = 'h';
    board[68][1] = 'e';
    board[69][1] = 'f';
    board[71][1] = '(';
    board[72][1] = 'P';
    board[73][1] = '1';
    board[74][1] = ')';

    board[62][2] = '&';
    board[64][2] = '-';
    board[66][2] = 'w';
    board[67][2] = 'a';
    board[68][2] = 'i';
    board[69][2] = 't';
    board[70][2] = 'e';
    board[71][2] = 'r';
    board[73][2] = '(';
    board[74][2] = 'P';
    board[75][2] = '2';
    board[76][2] = ')';

    board[62][3] = '%';
    board[64][3] = '-';
    board[66][3] = 'c';
    board[67][3] = 'o';
    board[68][3] = 'u';
    board[69][3] = 'n';
    board[70][3] = 't';
    board[71][3] = 'e';
    board[72][3] = 'r';

    board[62][4] = 'T';
    board[64][4] = '-';
    board[66][4] = 't';
    board[67][4] = 'a';
    board[68][4] = 'b';
    board[69][4] = 'l';
    board[70][4] = 'e';

    board[62][5] = 'U';
    board[64][5] = '-';
    board[66][5] = 'o';
    board[67][5] = 'v';
    board[68][5] = 'e';
    board[69][5] = 'n';

    board[62][6] = 'p';
    board[64][6] = '-';
    board[66][6] = 'f';
    board[67][6] = 'r';
    board[68][6] = 'o';
    board[69][6] = 'z';
    board[70][6] = 'e';
    board[71][6] = 'n';
    board[73][6] = 'p';
    board[74][6] = 'i';
    board[75][6] = 'z';
    board[76][6] = 'z';
    board[77][6] = 'a';

    board[62][7] = 'P';
    board[64][7] = '-';
    board[66][7] = 'c';
    board[67][7] = 'o';
    board[68][7] = 'o';
    board[69][7] = 'k';
    board[70][7] = 'e';
    board[71][7] = 'd';
    board[73][7] = 'p';
    board[74][7] = 'i';
    board[75][7] = 'z';
    board[76][7] = 'z';
    board[77][7] = 'a';

    // Info
    board[2][19] = 'P';
    board[4][19] = '\'';
    board[5][19] = 's';
    board[7][19] = 't';
    board[8][19] = 'u';
    board[9][19] = 'r';
    board[10][19] = 'n';

    if (currentPlayerTurn == 0) {
      board[3][19] = '1';
    } else {
      board[3][19] = '2';
    }

    if (game.gameIsOver()) {
      board[2][21] = 'G';
      board[3][21] = 'a';
      board[4][21] = 'm';
      board[5][21] = 'e';
      board[7][21] = 'o';
      board[8][21] = 'v';
      board[9][21] = 'e';
      board[10][21] = 'r';
      board[11][21] = '!';
    }

    // Vad spelarna har i händerna
    board[50][19] = 'P';
    board[51][19] = '1';
    board[52][19] = ':';
    if (players[0].getHands() != null) {
      board[54][19] = players[0].getHands().getCharacter();
    }

    board[50][21] = 'P';
    board[51][21] = '2';
    board[52][21] = ':';
    if (players[1].getHands() != null) {
      board[54][21] = players[1].getHands().getCharacter();
    }
  }
}
