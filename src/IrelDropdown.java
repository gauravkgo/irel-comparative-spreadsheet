import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicMenuItemUI;

public class IrelDropdown extends JDialog {
  
  JPanel menu;
  JScrollPane scroll;

  private Color bg1;
  private Color fg1;
  private Color bd1;
  private Color bg2;
  private Color fg2;
  private Font font;

  public IrelDropdown(JFrame f, ViewSettings v) {

    super(f, "", false);
    setUndecorated(true);
    menu = new JPanel();
    menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
    scroll = new JScrollPane(menu);
    getContentPane().add(scroll);


    bg1 = v.e2.bg;
    fg1 = v.e2.fg;
    bd1 = v.e2.bd;
    bg2 = v.e3.bg;
    fg2 = v.e3.fg;
    font = v.f2;

    menu.setBackground(bg1);
    menu.setBorder(new MatteBorder(1, 1, 1, 1, bd1));
  }

  private JMenuItem createItem(String s, ActionListener l) {

    JMenuItem i = new JMenuItem(s);
    i.setContentAreaFilled(false);
    i.setOpaque(true);
    i.setBackground(bg1);
    i.setForeground(fg1);
    i.setUI(new IrelMenuItemUI());
    i.setFont(font);
    i.addActionListener(l);
    return i;
  }

  public void display(
      Collection<String> c,
      JButton b,
      ActionListener addNewAutoNameListener,
      ActionListener addNewListener,
      ActionListener commonListener
    ) {

    menu.removeAll();
    if (addNewAutoNameListener != null)
      menu.add(createItem("New Group Auto-name", addNewAutoNameListener));
    if (addNewListener != null)
      menu.add(createItem("New Group", addNewListener));
    for (String s : c)
      menu.add(createItem(s, commonListener));
    pack();
    setLocationRelativeTo(b);
    setLocation(b.getLocationOnScreen().x, b.getLocationOnScreen().y + b.getHeight());
    setVisible(true);
  }

  public void clear() {

    setVisible(false);
  }

  private class IrelMenuItemUI extends BasicMenuItemUI {
    
    protected void installDefaults() {
      super.installDefaults();
      selectionBackground = bg2;
      selectionForeground = fg2;
    }
    protected void paintBackground(Graphics g, JMenuItem i, Color c) {
      g.setColor(i.getModel().isArmed() ? selectionBackground : i.getBackground());
      g.fillRect(0, 0, i.getWidth(), i.getHeight());
    }
    protected void paintText(Graphics g, JMenuItem i, Rectangle r, String s) {
      g.setColor(i.getModel().isArmed() ? selectionForeground : i.getForeground());
      g.drawString(s, r.x, r.y + g.getFontMetrics().getAscent());
    }
  }

}
