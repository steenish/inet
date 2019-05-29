// En klass som representerar en spelare i spelet.
public class Player extends GameObject {
  // Den pizzan spelaren håller i händerna.
  private Pizza hands;

  // Skapar en spelare på en given position med tomma händer och det givna numret.
  // Numret bestämmer tecknet. 0 innebär att spelaren får tecknet @, annars får den tecknet &.
  public Player(int x, int y, int playerNum) {
    super(x, y);

    if (playerNum == 0) {
      objectCharacter = '@';
    } else {
      objectCharacter = '&';
    }

    hands = null;
  }

  // Ger spelaren den givna pizzan om spelarens händer är tomma.
  public void setHands(Pizza pizza) {
    if (hands == null) {
      hands = pizza;
      hands.setPosition(xPos, yPos);
      hands.take();
    }
  }

  // Tömmer spelarens händer om de är tomma och returnerar pizzan.
  public Pizza emptyHands() {
    if (hands != null) {
      Pizza pizza = hands;
      hands = null;
      pizza.place();
      return pizza;
    }
    return null;
  }

  // Returnerar pizzan som spelaren har i händerna.
  public Pizza getHands() {
    return hands;
  }

  // Förflyttar spelaren och det spelaren har i händerna uppåt ett steg.
  public void moveUp() {
    yPos--;
    if (hands != null) {
      hands.setPosition(xPos, yPos);
    }
  }

  // Förflyttar spelaren och det spelaren har i händerna till höger ett steg.
  public void moveRight() {
    xPos++;
    if (hands != null) {
      hands.setPosition(xPos, yPos);
    }
  }

  // Förflyttar spelaren och det spelaren har i händerna nedåt ett steg.
  public void moveDown() {
    yPos++;
    if (hands != null) {
      hands.setPosition(xPos, yPos);
    }
  }

  // Förflyttar spelaren och det spelaren har i händerna till vänster ett steg.
  public void moveLeft() {
    xPos--;
    if (hands != null) {
      hands.setPosition(xPos, yPos);
    }
  }
}
