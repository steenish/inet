// En klass som representerar en disk i spelet.
public class Counter extends GameObject {
  // Enum med de olika tillstånd disken kan ha.
  public enum CounterStatus {
    EMPTY, OCCUPIED
  }

  private CounterStatus status;
  private Pizza contents;

  // Skapar en disk på given position, startar disken som tom och med tecknet %.
  public Counter(int x, int y) {
    super(x, y);
    status = CounterStatus.EMPTY;
    objectCharacter = '%';
    contents = null;
  }

  // Ställer den givna pizzan på disken.
  public void occupy(Pizza pizza) {
    status = CounterStatus.OCCUPIED;
    contents = pizza;
    contents.setPosition(xPos, yPos);
  }

  // Tar bort pizzan från disken och returnerar den.
  public Pizza retreive() {
    status = CounterStatus.EMPTY;
    Pizza pizza = contents;
    contents = null;
    return pizza;
  }

  public CounterStatus getStatus() {
    return status;
  }
}
