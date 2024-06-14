
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ScrollTable extends JScrollPane {

  private MultiCellTable table;
  
  public ScrollTable(ViewSettings s) {

    super();
    setBackground(s.e0.bg);
    setForeground(s.e0.fg);

    getVerticalScrollBar().setBackground(s.e1.bg);
    getVerticalScrollBar().setUI(new ScrollTableScrollBarUI(s.e1.fg, s.e1.bd));
    
    getHorizontalScrollBar().setBackground(s.e1.bg);
    getHorizontalScrollBar().setUI(new ScrollTableScrollBarUI(s.e1.fg, s.e1.bd));

    setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, s.e0.bg));

    table = new MultiCellTable(s);
    setViewportView(table);
  }

  public List<Point> getSelected() {
    return table.getSelectedCells();
  }

  public void clearSelected() {
    table.clearSelectedCells();
  }

  public void updateRowHeights() {
    table.updateRowHeights();
  }

  public void setModel(MultiCellTableModel model) {

    table.setModel(model);
    table.clearSelectedCells();
    table.setFillsViewportHeight(true);
  }

  private class ScrollTableScrollBarUI extends BasicScrollBarUI {

    private Color c, bd;

    public ScrollTableScrollBarUI(Color c, Color bd) {
      super();
      this.c = c;
      this.bd = bd;
    }

    protected void configureScrollBarColors() {
      thumbColor = c;
      thumbHighlightColor = bd;

    }

    protected JButton createDecreaseButton(int orientation) {
      JButton b = new JButton();
      b.setPreferredSize(new Dimension(0, 0));
      b.setMaximumSize(new Dimension(0, 0));
      b.setMinimumSize(new Dimension(0, 0));
      b.setVisible(false);
      return b;
    }

    protected JButton createIncreaseButton(int orientation) {
      JButton b = new JButton();
      b.setPreferredSize(new Dimension(0, 0));
      b.setMaximumSize(new Dimension(0, 0));
      b.setMinimumSize(new Dimension(0, 0));
      b.setVisible(false);
      return b;
    }

  }
}
