
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class TabButton extends JButton {

  private boolean isOn;

  private Color offBG;
  private Color offFG;
  private Color offBD;
  private Color onBG;
  private Color onFG;
  private Color onBD;

  public TabButton(
      String text,
      ViewSettings s
    ) {

    super(text);

    this.offBG = s.e2.bg;
    this.offFG = s.e2.fg;
    this.offBD = s.e2.bd;
    this.onBG = s.e3.bg;
    this.onFG = s.e3.fg;
    this.onBD = s.e3.bd;

    setFont(s.f2);
    setOpaque(true);
    setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, offBD));
    setFocusPainted(false);
    setHorizontalTextPosition(SwingConstants.CENTER);
    setBorder(null);
    setContentAreaFilled(false);

    setOff();
  }

  public void toggle() {
    isOn = !isOn;
  }

  public void setOff() {
    isOn = false;
  }

  public void setOn() {
    isOn = true;
  }

  protected void paintComponent(
      Graphics g
    ) {

    if (getModel().isPressed()) {
      setOn();
    }
    if (isOn) {
      g.setColor(onBG);
      setForeground(onFG);
      setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, onBD));
    }
    else {
      g.setColor(offBG);
      setForeground(offFG);
      setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, offBD));
    }
    g.fillRect(0, 0, getWidth(), getHeight());
    super.paintComponent(g);
  }
}
