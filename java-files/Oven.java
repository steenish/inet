// En klass som representerar en ugn inuti spelet.
public class Oven extends GameObject {
  // Enum med olika tillstånd ugnen kan ha.
  public enum OvenStatus {
    EMPTY, FULL
  }

  private OvenStatus status;
  private Pizza contents;

  private static final int TURNS_TILL_DONE = 10;
  private int turnsCooked;

  // Skapar en ugn på given position, startar ugnen som tom och med tecknet U.
  public Oven(int x, int y) {
    super(x, y);
    status = OvenStatus.EMPTY;
    objectCharacter = 'U';
    contents = null;
  }

  // Placerar den givna pizzan i ugnen.
  public void fill(Pizza pizza) {
    status = OvenStatus.FULL;
    contents = pizza;
    pizza.setPosition(xPos, yPos);

    turnsCooked = 0;
  }

  // Tar ut pizzan ur ugnen och returnerar den.
  public Pizza empty() {
    status = OvenStatus.EMPTY;

    turnsCooked = 0;

    Pizza pizza = contents;
    contents = null;
    return pizza;
  }

  // Utför ett tidssteg i pizzans tillagningsprocess.
  public void tick() {
    if (status != OvenStatus.FULL) {
      return;
    }

    if (++turnsCooked >= TURNS_TILL_DONE) {
      contents.cook();
    }
  }

  public OvenStatus getStatus() {
    return status;
  }
}
