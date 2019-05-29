// En superklass som representerar ett abstrakt objekt inuti spelet, som har en position och ett tecken.
public abstract class GameObject {
  protected int xPos;
  protected int yPos;

  protected char objectCharacter;

  public GameObject(int xPos, int yPos) {
    this.xPos = xPos;
    this.yPos = yPos;
  }

  public void setPosition(int x, int y) {
    xPos = x;
    yPos = y;
  }

  public int getXPos() {
    return xPos;
  }

  public int getYPos() {
    return yPos;
  }

  public char getCharacter() {
    return objectCharacter;
  }
}
