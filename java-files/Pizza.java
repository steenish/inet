// En klass som representerar en pizza i spelet.
public class Pizza extends GameObject {
  // Enum med de olika tillstånd pizzan kan ha.
  public enum PizzaStatus {
    FROZEN, COOKED
  }

  private PizzaStatus status;
  private boolean isPlaced;

  // Skapar en pizza på given position, startar pizzan som frusen och med tecknet p.
  public Pizza(int x, int y) {
    super(x, y);
    status = PizzaStatus.FROZEN;
    objectCharacter = 'p';
    isPlaced = true;
  }

  // Tillagar pizzan och ändrar teckent till P.
  public void cook() {
    status = PizzaStatus.COOKED;
    objectCharacter = 'P';
  }

  // Placerar pizzan någonstans, vilket tillåter att den skrivs ut.
  public void place() {
    isPlaced = true;
  }

  // Plockar upp pizzan, vilket hindrar den från att skrivas ut på spelbrädet.
  public void take() {
    isPlaced = false;
  }

  public boolean getIsPlaced() {
    return isPlaced;
  }
}
