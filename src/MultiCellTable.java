
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.BoxLayout;
import javax.swing.JPanel;



public class MultiCellTable extends JTable {
  
  private List<Point> selectedCells;

  private Color bg1;
  private Color tx1;
  private Color bd1;
  private Color bg2;
  private Color tx2;
  private Color bd2;
  private Color bg3;
  private Color tx3;
  private Color bd3;

  private Font f1;
  private Font f2;

  public MultiCellTable(ViewSettings s) {
    
    super();

    this.bg1 = s.e0.bg;
    this.tx1 = s.e0.fg;
    this.bd1 = s.e0.bd;
    this.bg2 = s.e2.bg;
    this.tx2 = s.e2.fg;
    this.bd2 = s.e2.bd;
    this.bg3 = s.e3.bg;
    this.tx3 = s.e3.fg;
    this.bd3 = s.e3.bd;

    this.f1 = s.f2;
    this.f2 = s.f3;

    setBackground(bg1);
    setForeground(tx1);
    setGridColor(bd1);
    setBorder(new MatteBorder(1, 1, 1, 1, bg1));
    setSelectionBackground(bg3);
    setSelectionForeground(tx3);

    getTableHeader().setBackground(bg1);
    getTableHeader().setForeground(tx1);

    getTableHeader().setDefaultRenderer(new HeaderRenderer());

    getTableHeader().setReorderingAllowed(false);

    selectedCells = new ArrayList<Point>();

    setDefaultRenderer(String.class, new StringCellRenderer());
    setDefaultRenderer(MultiCell.class, new MultiCellRenderer());
  }

  protected void processMouseEvent(MouseEvent e) {

    if (e.getID() != MouseEvent.MOUSE_PRESSED) return;
    JTable t = (JTable) e.getSource();
    Point eP = e.getPoint();
    Point cell = new Point(t.rowAtPoint(eP), t.columnAtPoint(eP));
    if (cell.x < 0 || cell.x >= t.getRowCount()) return;
    if (cell.y < 0 || cell.y >= t.getColumnCount()) return;
    Object o0 = t.getValueAt(cell.x, 0);
    Object o1 = t.getValueAt(cell.x, 1);
    MultiCell c = (o0 instanceof MultiCell) ? (MultiCell) o0 : (MultiCell) o1;
    switch (c.getType()) {

      case WORD:

        if (cell.y == 0) {
          List<Point> rowPoints = new ArrayList<Point>();
          for (int col = 1; col < t.getColumnCount(); col ++) rowPoints.add(new Point(cell.x, col));
          boolean fullRowSelected = selectedCells.containsAll(rowPoints);
          selectedCells.clear();
          if (fullRowSelected) break;
          selectedCells.addAll(rowPoints);
        }
        else if (selectedCells.contains(cell)) {
          selectedCells.remove(cell);
        }
        else {
          boolean columnOccupied = false;
          for (Point p : selectedCells) {
            if (p.y == cell.y) columnOccupied = true;
          }
          if (!columnOccupied) selectedCells.add(cell);
        }
        break;

      case COGNATE:
      case CORRESPONDENCE:

        if (cell.y == 0) {
          List<Point> rowPoints = new ArrayList<Point>();
          for (int col = 1; col < t.getColumnCount(); col ++) rowPoints.add(new Point(cell.x, col));
          boolean fullRowSelected = selectedCells.containsAll(rowPoints);
          selectedCells.removeAll(rowPoints);
          if (fullRowSelected) break;
          selectedCells.addAll(rowPoints);
        }
        else if (selectedCells.contains(cell)) {
          selectedCells.remove(cell);
        }
        else {
          selectedCells.add(cell);
        }
        break;

      default:

        break;
    }

    t.repaint();
  }

  public List<Point> getSelectedCells() {
    return selectedCells;
  }

  public void clearSelectedCells() {
    selectedCells.clear();
  }

  public boolean isCellSelected(int x, int y) {
    return selectedCells.contains(new Point(x, y));
  }

  public boolean isInRow(int x) {
    for (Point c : selectedCells) if (c.x == x) return true;
    return false;
  }

  public void updateRowHeights() {
    for (int row = 0; row < getRowCount(); row ++) {
      int rowHeight = 50;
      for (int col = 1; col < getColumnCount(); col ++) {
        Component comp = prepareRenderer(getCellRenderer(row, col), row, col);
        int preferredHeight = comp.getPreferredSize().height;
        rowHeight = Math.max(rowHeight, preferredHeight);
      }
      setRowHeight(row, rowHeight);
    }
  }



  private class StringCellRenderer extends JLabel implements TableCellRenderer {

    public StringCellRenderer() {

      super();
      setFont(f1);
      setHorizontalAlignment(JLabel.CENTER);
      setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable t, Object o, boolean isSelected, boolean hasFocus, int row, int column) {

      MultiCellTable table = (MultiCellTable) t;
      boolean inSelectedRow = table.isInRow(row);
      String s = (String) o;
      s = s.isEmpty() ? "-" : s;
      setText(s);
      setBackground(isSelected ? bg3 : inSelectedRow ? bg2 : bg1);
      setForeground(isSelected ? tx3 : inSelectedRow ? tx2 : tx1);
      setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, isSelected ? bd3 : inSelectedRow ? bd2 : bg1));
      return this;
    }
  }

  private class HeaderRenderer extends JPanel implements TableCellRenderer {

    private JLabel label;
    private Color gridColor;

    public HeaderRenderer() {

      setBackground(bg1);
      label = new JLabel();
      add(label);
      label.setFont(f1);
      label.setHorizontalAlignment(JLabel.CENTER);
      label.setOpaque(false);
      label.setForeground(tx1);
      gridColor = bd1;
      setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, gridColor));
      label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

      MultiCellTable mct = (MultiCellTable) table;
      mct.updateRowHeights();
      label.setText(((String) value).toUpperCase());
      return this;
    }
  }


  private class SecLab extends JLabel {
    public SecLab(String s, float a, Color c) {
      super(s);
      setAlignmentX(a);
      setOpaque(false);
      setForeground(c);
      setFont(f2);
    }
  }

  private class SecLabSpacer extends JLabel {
    public SecLabSpacer(Color c, int divisor) {
      super(" ");
      setAlignmentX(JLabel.CENTER_ALIGNMENT);
      setOpaque(false);
      setForeground(c);
      setFont(new Font(f2.getName(), f2.getStyle(), f2.getSize() / divisor));
    }
  }

  private class MultiCellRenderer extends JPanel implements TableCellRenderer {

    private JLabel primary;
    private JPanel secondary;

    public MultiCellRenderer() {
      
      setLayout(new BorderLayout());
      setAlignmentX(Component.CENTER_ALIGNMENT);

      setBackground(bg1);
      setForeground(tx1);

      primary = new JLabel();
      primary.setFont(f1);
      primary.setOpaque(false);
      primary.setHorizontalAlignment(JLabel.CENTER);
      primary.setLayout(new BorderLayout());
      add(primary, BorderLayout.CENTER);

      secondary = new JPanel();
      secondary.setOpaque(false);
      secondary.setLayout(new BoxLayout(secondary, BoxLayout.Y_AXIS));
    }

    public Component getTableCellRendererComponent(JTable t, Object o, boolean selected, boolean focus, int row, int col) {

      MultiCell cell = (MultiCell) o;
      MultiCellTable table = (MultiCellTable) t;
      boolean inSelectedRow = table.isInRow(row);

      Color textColor = selected ? tx3 : inSelectedRow ? tx2 : tx1;
      Color bgColor = selected ? bg3 : inSelectedRow ? bg2 : bg1;
      Color bdColor = selected ? bd3 : inSelectedRow ? bd2 : bg1;

      setBackground(bgColor);
      setForeground(textColor);


      secondary.removeAll();

      primary.removeAll();
      String primStr = cell.getPrimary();
      primary.setText(primStr.isEmpty() ? "-" : primStr);
      primary.setForeground(textColor);
      setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, bdColor));

      CellType type = cell.getType();
      switch (type) {
        case WORD:
        case COGNATE:
          primary.add(secondary, BorderLayout.EAST);
          for (String s : cell.getSecondaries())
            secondary.add(new SecLab(s.isEmpty() ? "-" : s.length() > 10 ? s.substring(0, 10) + "..." : s, JLabel.RIGHT_ALIGNMENT, textColor));
          break;
        case CORRESPONDENCE:
          add(secondary, BorderLayout.SOUTH);
          secondary.add(new SecLabSpacer(bgColor, 1));
          for (String s : cell.getSecondaries()) {
            secondary.add(new SecLab(s.isEmpty() ? "-" : s, JLabel.CENTER_ALIGNMENT, textColor));
            secondary.add(new SecLabSpacer(bgColor, 2));
          }
          break;
        default:
          break;
      }
      return this;
    }

    public Dimension getPreferredSize() {
      Dimension defaultDimension = super.getPreferredSize();
      Dimension secondarysDimension = secondary.getPreferredSize();
      Dimension size = new Dimension();
      size.setSize(
        Math.max(
          defaultDimension.getWidth(),
          secondarysDimension.getWidth()
        ) * 1.125,
        Math.max(
          defaultDimension.getHeight(),
          secondarysDimension.getHeight()
        ) * 1.125
      );
      return size;
    }
  }
}
