// En klass som representerar ett bord i spelet.
public class Table extends GameObject {
  // Enum med de olika tillstånd bordet kan ha.
  public enum TableStatus {
    EMPTY, SERVED
  }

  private TableStatus status;
  private Pizza contents;

  // Skapar ett bord på given position, startar bordet som tomt och med tecknet T.
  public Table(int x, int y) {
    super(x, y);
    status = TableStatus.EMPTY;
    objectCharacter = 'T';
    contents = null;
  }

  // Serverar den givna pizzan till bordet.
  public void serve(Pizza pizza) {
    status = TableStatus.SERVED;
    contents = pizza;
    contents.setPosition(xPos, yPos);
  }

  // Tar bort pizzan från bordet och returnerar den.
  public Pizza retreive() {
    status = TableStatus.EMPTY;
    Pizza pizza = contents;
    contents = null;
    return pizza;
  }

  public TableStatus getStatus() {
    return status;
  }
}
