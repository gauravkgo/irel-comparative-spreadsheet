
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class MultiCellTableModel extends DefaultTableModel {

  public MultiCellTableModel() {
    super();
  }

  public Class<?> getColumnClass(int columnIndex) {
    return (getRowCount() > 0)
      ? getValueAt(0, columnIndex).getClass()
      : MultiCell.class;
  }
  
  public boolean isCellEditable(int row, int column) {
    Object o = getValueAt(row, column);
    return o instanceof MultiCell && ((MultiCell) o).getType() == CellType.CORRESPONDENCE;
  }

  public boolean cellExists(Point p) {

    return !((p.x < 0 || p.x >= getRowCount()) && (p.y < 0 || p.y >= getColumnCount()));
  }

  public boolean cellIsOccupied(Point p) {
    
    Object o = getValueAt(p.x, p.y);
    if (o instanceof MultiCell) return !(((MultiCell) o).getPrimary().isEmpty());
    if (o instanceof String) return !(((String) o).isEmpty());
    return true;
  }

  public int getIndexOfLabel(String l) {

    for (int i = 0; i < getRowCount(); i ++) {
      Object o = getValueAt(i, 0);
      String currLabel = (o instanceof MultiCell) ? ((MultiCell) o).getPrimary() : (String) o;
      if (currLabel.equals(l))
        return i;
    }
    return -1;
  }

  public MultiCell getMC(Point p) {

    return (MultiCell) getValueAt(p.x, p.y);
  }

  public String getS(Point p) {

    return (String) getValueAt(p.x, p.y);
  }

  public List<Point> getCellsWithSecWithRange(String s, Point p0, Point p1) {

    List<Point> cells = new ArrayList<Point>();
    for (int i = p0.x; i <= p1.x && i < getRowCount(); i ++) {
      for (int j = p0.y; j <= p1.y && j < getColumnCount(); j ++) {
        Point cell = new Point(i, j);
        if (!(getValueAt(i, j) instanceof MultiCell)) continue;
        if (getMC(cell).getSecondaries().contains(s)) cells.add(cell);
      }
    }
    return cells;
  }

  public List<String> getLabels() {
    List<String> col = new ArrayList<String>();
    for (int i = 0; i < getRowCount(); i ++) {
      Object o = getValueAt(i, 0);
      col.add((o instanceof MultiCell) ? ((MultiCell) o).getPrimary() : (String) o);
    }
    return col;
  }
}
