import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import javax.swing.*;
import java.io.*;

public class Demo {
  public static void main(String[] args) {
    Terminal terminal = null;
    try {
      DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
      terminal = defaultTerminalFactory.createTerminal();
      terminal.enterPrivateMode();
      terminal.clearScreen();
      terminal.setCursorVisible(false);

      while (true) {
        while (terminal.pollInput() != null);
        com.googlecode.lanterna.input.KeyStroke k = terminal.readInput();
        System.out.println(k);
        Thread.sleep(5);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (terminal != null) {
        try {
          terminal.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
