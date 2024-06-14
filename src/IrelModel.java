
import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IrelModel {

  private MultiCellTableModel wordlistModel;
  private MultiCellTableModel cognateListModel;
  private MultiCellTableModel correspondenceListModel;

  public IrelModel() {

    wordlistModel = new MultiCellTableModel();
    cognateListModel = new MultiCellTableModel();
    correspondenceListModel = new MultiCellTableModel();
  }

  public boolean initWordlist(String csvPath) {

    try {
      File csvFile = new File(csvPath);
      Scanner scan = new Scanner(csvFile);
      String[] csvRow;
      Object[] wordCells;

      csvRow = scan.nextLine().split(",");
      csvRow[0] = "Gloss";
      wordlistModel.setColumnIdentifiers(csvRow);
      csvRow[0] = "Cognate";
      cognateListModel.setColumnIdentifiers(csvRow);
      csvRow[0] = "Correspondence";
      correspondenceListModel.setColumnIdentifiers(csvRow);

      wordCells = new Object[csvRow.length + 1];

      while (scan.hasNextLine()) {
        csvRow = scan.nextLine().split(",");
        wordCells[0] = csvRow[0];
        for (int i = 1; i < csvRow.length; i ++)
          wordCells[i] = new MultiCell(csvRow[i], new ArrayList<String>(), CellType.WORD);
        wordlistModel.addRow(wordCells);
      }
      scan.close();
      return true;
    }
    catch (Exception e) {
      System.out.println(e);
      return false;
    }
  }

  public boolean writeCognateList(String csvPath) {

    try {
      FileWriter csvWriter = new FileWriter(csvPath);
      StringBuffer s = new StringBuffer();
      s.append(cognateListModel.getColumnName(0));
      for (int i = 1; i < cognateListModel.getColumnCount(); i ++)
        s.append("," + cognateListModel.getColumnName(i));
      s.append("\n");
      for (int i = 0; i < cognateListModel.getRowCount(); i ++) {
        s.append(cognateListModel.getMC(new Point(i, 0)).getPrimary());
        for (int j = 1; j < cognateListModel.getColumnCount(); j ++)
          s.append("," + cognateListModel.getS(new Point(i, j)));
        s.append("\n");
      }
      String text = new String(s);
      csvWriter.write(text);
      csvWriter.close();
      return true;
    }
    catch (Exception e) {
      System.out.println(e);
      return false;
    }
  }

  public boolean writeCorrespondenceList(String csvPath) {

    try {
      FileWriter csvWriter = new FileWriter(csvPath);
      StringBuffer s = new StringBuffer();
      s.append(correspondenceListModel.getColumnName(0));
      for (int i = 1; i < correspondenceListModel.getColumnCount(); i ++)
        s.append("," + correspondenceListModel.getColumnName(i));
      s.append("\n");
      for (int i = 0; i < correspondenceListModel.getRowCount(); i ++) {
        MultiCell labelCell = correspondenceListModel.getMC(new Point(i, 0));
        s.append(labelCell.getPrimary());
        for (int j = 1; j < correspondenceListModel.getColumnCount(); j ++)
          s.append("," + correspondenceListModel.getMC(new Point(i, j)).getPrimary());
        s.append("\n");
        for (int j = 0; j < labelCell.getSecondaries().size(); j ++) {
          s.append(labelCell.getSecondaries().get(j));
          for (int k = 1; k < correspondenceListModel.getColumnCount(); k ++) {
            s.append("," + correspondenceListModel.getMC(new Point(i, k)).getSecondaries().get(j));
          }
          s.append("\n");
        }
      }
      String text = new String(s);
      csvWriter.write(text);
      csvWriter.close();
      return true;
    }
    catch (Exception e) {
      System.out.println(e);
      e.printStackTrace();
      return false;
    }
  }

  public MultiCellTableModel getWordlist() {
    return wordlistModel;
  }

  public MultiCellTableModel getCognateList() {
    return cognateListModel;
  }

  public MultiCellTableModel getCorrespondenceList() {
    return correspondenceListModel;
  }









  private void editRelevantCorrespondence(Point cognatePoint, String s) {

    Point cognateLabelPoint = new Point(cognatePoint.x, 0);
    MultiCell cognateLabelCell = cognateListModel.getMC(cognateLabelPoint);
    List<String> correspondences = cognateLabelCell.getSecondaries();
    for (String correspondence : correspondences) {
      int correspondenceRow = correspondenceListModel.getIndexOfLabel(correspondence);
      if (correspondenceRow < 0) continue;
      MultiCell correspondenceCell = correspondenceListModel.getMC(new Point(correspondenceRow, cognatePoint.y));
      MultiCell correspondenceLabelCell = correspondenceListModel.getMC(new Point(correspondenceRow, 0));
      correspondenceCell.setSecondary(correspondenceLabelCell.getSecondaries().indexOf(cognateLabelCell.getPrimary()), s);
    }
  }

  private List<Point> getWordsWithCognate(String cognate) {

    List<Point> wordPoints = wordlistModel.getCellsWithSecWithRange(
      cognate,
      new Point(0, 1),
      new Point(wordlistModel.getRowCount(), wordlistModel.getColumnCount()));
    return wordPoints;
  }

  private List<Point> getCorrespondencesWithCognate(String cognate) {

    List<Point> correspondencePoints = correspondenceListModel.getCellsWithSecWithRange(
      cognate,
      new Point(0, 0),
      new Point(wordlistModel.getRowCount(), 1));
    return correspondencePoints;
  }









  public boolean assignWordToCognate(Point wordPoint, String cognate) {

    if (!wordlistModel.cellExists(wordPoint)) return false;
    int row = cognateListModel.getIndexOfLabel(cognate);
    if (row < 0) return false;
    MultiCell wordCell = wordlistModel.getMC(wordPoint);
    String word = wordCell.getPrimary();
    Point cognatePoint = new Point(row, wordPoint.y);
    if (wordCell.getSecondaries().contains(cognate)) return false;
    if (!cognateListModel.getS(cognatePoint).isEmpty()) return false;

    wordCell.addSecondary(cognate);
    cognateListModel.setValueAt(word, cognatePoint.x, cognatePoint.y);
    editRelevantCorrespondence(cognatePoint, word);

    return true;
  }

  public boolean createCognate(String cognate) {

    if (cognateListModel.getIndexOfLabel(cognate) >= 0) return false;

    Object[] cognateCells = new Object[cognateListModel.getColumnCount()];
    cognateCells[0] = new MultiCell(cognate, new ArrayList<String>(), CellType.COGNATE);
    for (int i = 1; i < cognateListModel.getColumnCount(); i ++) cognateCells[i] = "";
    cognateListModel.addRow(cognateCells);

    return true;
  }

  public boolean removeWordFromCognate1(Point wordPoint, String cognate) {

    int row = cognateListModel.getIndexOfLabel(cognate);
    if (row < 0) return false;
    MultiCell wordCell = wordlistModel.getMC(wordPoint);
    String word = wordCell.getPrimary();
    Point cognatePoint = new Point(row, wordPoint.y);
    if (!wordCell.getSecondaries().contains(cognate)) return false;
    if (!cognateListModel.getS(cognatePoint).equals(word)) return false;

    wordCell.removeSecondary(cognate);
    cognateListModel.setValueAt("", cognatePoint.x, cognatePoint.y);
    editRelevantCorrespondence(cognatePoint, "");

    return true;
  }









  public boolean renameCognate(int cognateRow, String newCognate) {

    if (cognateRow < 0 || cognateRow >= cognateListModel.getRowCount()) return false;
    if (newCognate.isEmpty() || (cognateListModel.getIndexOfLabel(newCognate) >= 0)) return false;

    String oldCognate = cognateListModel.getMC(new Point(cognateRow, 0)).getPrimary();
    cognateListModel.getMC(new Point(cognateRow, 0)).setPrimary(newCognate);
    for (Point p : getWordsWithCognate(oldCognate)) wordlistModel.getMC(p).replaceSecondary(oldCognate, newCognate);
    for (Point p : getCorrespondencesWithCognate(oldCognate)) correspondenceListModel.getMC(p).replaceSecondary(oldCognate, newCognate);

    return true;
  }

  public boolean removeWordFromCognate2(Point cognatePoint) {

    if (cognatePoint.x < 0 || cognatePoint.x >= cognateListModel.getRowCount()) return false;
    if (cognatePoint.y < 1 || cognatePoint.y >= cognateListModel.getColumnCount()) return false;
    String word = cognateListModel.getS(cognatePoint);
    if (word.isEmpty()) return false;

    String cognate = cognateListModel.getMC(new Point(cognatePoint.x, 0)).getPrimary();
    cognateListModel.setValueAt("", cognatePoint.x, cognatePoint.y);
    for (int row = 0; row < wordlistModel.getRowCount(); row ++) {
      MultiCell wordCell = wordlistModel.getMC(new Point(row, cognatePoint.y));
      if (!wordCell.getPrimary().equals(word)) continue;
      if (wordCell.getSecondaries().contains(cognate)) wordCell.removeSecondary(cognate);
    }
    editRelevantCorrespondence(cognatePoint, "");

    return true;
  }

  public boolean deleteCognate(String cognate) {

    int cognateRow = cognateListModel.getIndexOfLabel(cognate);
    if (cognateRow < 0) return false;

    for (int i = 1; i < cognateListModel.getColumnCount(); i ++) removeWordFromCognate2(new Point(cognateRow, i));
    cognateListModel.removeRow(cognateRow);
    for (Point p : getCorrespondencesWithCognate(cognate)) {
      int cIndex = correspondenceListModel.getMC(p).getSecondaries().indexOf(cognate);
      for (int i = 0; i < correspondenceListModel.getColumnCount(); i ++) {
        correspondenceListModel.getMC(new Point(p.x, i)).removeSecondary(cIndex);
      }
    }
    
    return true;
  }
  
  public boolean assignCognateToCorrespondence(Point cognatePoint, String correspondence) {

    cognatePoint = new Point(cognatePoint.x, 0);
    if (!cognateListModel.cellExists(cognatePoint)) return false;
    int row = correspondenceListModel.getIndexOfLabel(correspondence);
    if (row < 0) return false;
    MultiCell cognateCell = cognateListModel.getMC(cognatePoint);
    String cognate = cognateCell.getPrimary();
    MultiCell correspondenceLabel = correspondenceListModel.getMC(new Point(row, 0));
    if (cognateCell.getSecondaries().contains(correspondence)) return false;
    if (correspondenceLabel.getSecondaries().contains(cognate)) return false;

    cognateCell.addSecondary(correspondence);
    correspondenceLabel.addSecondary(cognate);
    for (int i = 1; i < correspondenceListModel.getColumnCount(); i ++)
      correspondenceListModel.getMC(new Point(row, i)).addSecondary(
        cognateListModel.getS(new Point(cognatePoint.x, i)));

    return true;
  }

  public boolean createCorrespondence(String correspondence) {

    if (correspondenceListModel.getIndexOfLabel(correspondence) >= 0) return false;

    Object[] correspondenceCells = new Object[correspondenceListModel.getColumnCount()];
    for (int i = 0; i < correspondenceListModel.getColumnCount(); i ++)
      correspondenceCells[i] = new MultiCell((i == 0) ? correspondence : "", new ArrayList<String>(), CellType.CORRESPONDENCE);
    correspondenceListModel.addRow(correspondenceCells);

    return true;
  }

  public boolean removeCognateFromCorrespondence1(Point cognatePoint, String correspondence) {

    int row = correspondenceListModel.getIndexOfLabel(correspondence);
    if (row < 0) return false;
    MultiCell cognateLabelCell = cognateListModel.getMC(new Point(cognatePoint.x, 0));
    String cognate = cognateLabelCell.getPrimary();
    MultiCell correspondenceLabelCell = correspondenceListModel.getMC(new Point(row, 0));
    if (!cognateLabelCell.getSecondaries().contains(correspondence)) return false;
    if (!correspondenceLabelCell.getSecondaries().contains(cognate)) return false;

    cognateLabelCell.removeSecondary(correspondence);
    int cognateI = correspondenceLabelCell.getSecondaries().indexOf(cognate);
    for (int col = 0; col < correspondenceListModel.getColumnCount(); col ++)
      correspondenceListModel.getMC(new Point(row, col)).removeSecondary(cognateI);

    return true;
  }









  public boolean renameCorrespondence(int correspondenceRow, String newCorrespondence) {

    if (correspondenceRow < 0 || correspondenceRow >= correspondenceListModel.getRowCount()) return false;
    if (newCorrespondence.isEmpty() || (correspondenceListModel.getIndexOfLabel(newCorrespondence) >= 0)) return false;

    MultiCell correspondenceCell = correspondenceListModel.getMC(new Point(correspondenceRow, 0));
    String oldCorrespondence = correspondenceCell.getPrimary();
    correspondenceCell.setPrimary(newCorrespondence);
    for (int i = 0; i < cognateListModel.getRowCount(); i ++) {
      MultiCell cognateCell = cognateListModel.getMC(new Point(i, 0));
      if (cognateCell.getSecondaries().contains(oldCorrespondence))
        cognateCell.replaceSecondary(oldCorrespondence, newCorrespondence);
    }

    return true;
  }

  public boolean editCorrespondence(Point correspondencePoint, String correspondence) {

    if (!correspondenceListModel.cellExists(correspondencePoint)) return false;
    if (correspondencePoint.y == 0) return false;

    MultiCell cell = correspondenceListModel.getMC(correspondencePoint);
    cell.setPrimary(correspondence);
    return true;
  }

  public boolean removeCognateFromCorrespondence2(int correspondenceRow, String cognateGroup) {

    if (correspondenceRow < 0 || correspondenceRow >= correspondenceListModel.getRowCount()) return false;
    MultiCell correspondenceGroup = correspondenceListModel.getMC(new Point(correspondenceRow, 0));
    if (!correspondenceGroup.getSecondaries().contains(cognateGroup)) return false;

    String correspondenceGroupName = correspondenceGroup.getPrimary();
    int relevantRow = correspondenceGroup.getSecondaries().indexOf(cognateGroup);
    for (int i = 0; i < correspondenceListModel.getColumnCount(); i ++)
      correspondenceListModel.getMC(new Point(correspondenceRow, i)).removeSecondary(relevantRow);
    for (int i = 0; i < cognateListModel.getRowCount(); i ++) {
      MultiCell cognateCell = cognateListModel.getMC(new Point(i, 0));
      if (cognateCell.getPrimary().equals(cognateGroup))
        cognateCell.removeSecondary(correspondenceGroupName);
    }

    return true;
  }

  public boolean deleteCorrespondence(String correspondence) {

    int corresondenceRow = correspondenceListModel.getIndexOfLabel(correspondence);
    if (corresondenceRow < 0) return false;

    MultiCell correspondenceCell = correspondenceListModel.getMC(new Point(corresondenceRow, 0));
    List<String> secondaries = correspondenceCell.getSecondaries();
    for (int i = 0; i < secondaries.size(); i ++)
      removeCognateFromCorrespondence2(corresondenceRow, secondaries.get(i));
    correspondenceListModel.removeRow(corresondenceRow);

    return true;
  }
  
}
