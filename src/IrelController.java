
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JMenuItem;

public class IrelController {
  
  private IrelModel model;
  private IrelView view;
  private Path currentPath = Paths.get("");
  private Path filesPath = Paths.get(currentPath.toString(), "..", "files");

  public IrelController(IrelModel model, IrelView view) {

    this.model = model;
    this.view = view;

    this.view.setTabListeners(
      new SetWordlistButtonListener(),
      new SetCognateListButtonListener(),
      new SetCorrespondenceListButtonListener()
    );

    this.view.setWordlistButtonListeners(
      new AutoPopulateCognatesFromWordsButtonListener(),
      new AssignWordToCognateButtonListener(),
      new RemoveWordFromCognate1ButtonListener()
    );

    this.view.setCognateListButtonListeners(
      new RenameCognateButtonListener(),
      new RemoveWordFromCognate2ButtonListener(),
      new DeleteCognateButtonListener(),
      new AssignCognateToCorrespondenceButtonListener(),
      new RemoveCognateFromCorrespondence1ButtonListener()
    );

    this.view.setCorrespondenceListButtonListeners(
      new RenameCorrespondenceButtonListener(),
      new EditCorrespondenceButtonListener(),
      new RemoveCognateFromCorrespondence2ButtonListener(),
      new DeleteCorrespondenceButtonListener()
    );

    this.view.setBottomListeners(
      new ExportCognateListButtonListener(),
      new ExportCorrespondenceListButtonListener()
    );
  }

  public void run() {

    String wordlistCSV = view.prompt("Enter name of Wordlist CSV file:");
    if (wordlistCSV == null
        || wordlistCSV.isEmpty()
        || wordlistCSV.isBlank()
        || !model.initWordlist(Paths.get(filesPath.toString(), wordlistCSV).toString())
      ) {
      view.message("Failed to open and initiatize from wordlist \""+ wordlistCSV +"\"");
    }
    view.setWordlist(model.getWordlist());
    view.setVisible(true);

    //testing();    
  }

  private void testing() {

    model.createCognate("test");
    model.assignWordToCognate(new Point(0, 1), "test");
    model.assignWordToCognate(new Point(0, 2), "test");
    model.assignWordToCognate(new Point(1, 1), "test");
    model.createCognate("sifr group");
    model.assignWordToCognate(new Point(3, 1), "sifr group");
    model.assignWordToCognate(new Point(3, 2), "sifr group");
    model.removeWordFromCognate1(new Point(3, 1), "sifr group");
    model.assignWordToCognate(new Point(3, 1), "sifr group");
    model.removeWordFromCognate2(new Point(1, 1));
    model.renameCognate(1, "nuli");
    model.deleteCognate("test");
  }

  public void commonConclude() {

    view.clearSelected();
    model.getWordlist().fireTableDataChanged();
    model.getCognateList().fireTableDataChanged();
    model.getCorrespondenceList().fireTableDataChanged();
    view.updateRowHeights();
  }
  








/**********************************************************************/
/**********************************************************************/
/**********************************************************************/









  private class SetWordlistButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      view.setWordlist(model.getWordlist());
      commonConclude();
    }
  }

  private class SetCognateListButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      view.setCognateList(model.getCognateList());
      commonConclude();
    }
  }

  private class SetCorrespondenceListButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      view.setCorrespondenceList(model.getCorrespondenceList());
      commonConclude();
    }
  }
  








/**********************************************************************/
/**********************************************************************/
/**********************************************************************/









  private void assignA2B(
      ActionEvent e,
      MultiCellTableModel a,
      MultiCellTableModel b,
      ActionListener listenerNew,
      ActionListener listenerNewAutoName,
      ActionListener listenerAssign
    ) {

    if (view.getDropdownOpen()) {
      view.closeDropdown();
      return;
    }
    List<Point> points = view.getSelected();
    if (points.isEmpty()) {
      view.message("No cells selected.");
      return;
    }
    Set<String> bSet = new HashSet<String>(b.getLabels());
    for (Point p : points) bSet.removeAll(a.getMC((b == model.getCognateList()) ? p : new Point(p.x, 0)).getSecondaries());
    List<String> bList = new ArrayList<String>(bSet);
    view.sendDropdown(
      bList,
      e,
      listenerNew,
      listenerNewAutoName,
      listenerAssign
    );
  }

  private void removeAfB(
      ActionEvent e,
      MultiCellTableModel a,
      MultiCellTableModel b,
      ActionListener listenerAssign
    ) {

    if (view.getDropdownOpen()) {
      view.closeDropdown();
      return;
    }
    List<Point> points = view.getSelected();
    if (points.isEmpty()) {
      view.message("No cells selected.");
      return;
    }
    Set<String> bSet = new HashSet<String>();
    for (Point p : points) bSet.addAll(a.getMC((b == model.getCognateList()) ? p : new Point(p.x, 0)).getSecondaries());
    List<String> bList = new ArrayList<String>(bSet);
    if (bList.isEmpty()) {
      view.message("No groups to remove selected cells from.");
      commonConclude();
      return;
    }
    if (bList.size() > 1)
      view.sendDropdown(
        bList,
        e,
        null,
        null,
        listenerAssign
      );
    else {
      if (b == model.getCognateList())
        removeWordFromCognateEnding(bList.get(0));
      else
        removeCognateFromCorrespondenceEnding(bList.get(0));
    }
  }
  








/**********************************************************************/
/**********************************************************************/
/**********************************************************************/









  private class AutoPopulateCognatesFromWordsButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      MultiCellTableModel wordlist = model.getWordlist();
      for (int i = 0; i < wordlist.getRowCount(); i ++) {
        String cognate = wordlist.getS(new Point(i, 0));
        model.createCognate(cognate);
        for (int j = 1; j < wordlist.getColumnCount(); j ++) {
          model.assignWordToCognate(new Point(i, j), cognate);
        }
      }
      commonConclude();
    }
  }

  private class AssignWordToCognateButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      assignA2B(
        e,
        model.getWordlist(),
        model.getCognateList(),
        new AssignWordToCognateDropdownAddNewAutoNameListener(),
        new AssignWordToCognateDropdownAddNewListener(),
        new AssignWordToCognateDropdownCommonListener()
      );
    }
  }

  private class AssignWordToCognateDropdownAddNewAutoNameListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      view.closeDropdown();
      List<Point> points = view.getSelected();
      int min = points.get(0).x;
      for (Point p : points)
        if (p.x < min)
          min = p.x;
      String cognate = (String) (model.getWordlist().getValueAt(min, 0));
      if (cognate == null || cognate.isEmpty() || (!(model.createCognate(cognate)))) {
        view.message("Failed to create new cognate group \"" + cognate + "\"");
        commonConclude();
      }
      assignWordToCognateAction(cognate);
    }
  }

  private class AssignWordToCognateDropdownAddNewListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      view.closeDropdown();
      String cognate = view.prompt("Enter name of new cognate group:");
      if (cognate == null || cognate.isEmpty() || (!(model.createCognate(cognate)))) {
        view.message("Failed to create new cognate group \"" + cognate + "\"");
        commonConclude();
      }
      assignWordToCognateAction(cognate);
    }
  }

  private class AssignWordToCognateDropdownCommonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      view.closeDropdown();
      JMenuItem i = (JMenuItem) (e.getSource());
      String cognate = i.getText();
      assignWordToCognateAction(cognate);
    }
  }

  private void assignWordToCognateAction(String cognate) {

    List<String> failedWords = new ArrayList<String>();
    List<Point> points = view.getSelected();
    for (Point p : points)
      if (!(model.assignWordToCognate(p, cognate)))
        failedWords.add(model.getWordlist().getMC(p).getPrimary());
    if (!(failedWords.isEmpty())) {
      StringBuffer msg = new StringBuffer();
      msg.append("Failed to add the following " + ((failedWords.size() > 1) ? "words" : "word") + " to cognate group \"" + cognate + "\":\n");
      for (String s : failedWords) msg.append("\"" + s + "\"\n");
      view.message(msg.toString());
    }
    commonConclude();
  }






  

  
  private class RemoveWordFromCognate1ButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      removeAfB(
        e,
        model.getWordlist(),
        model.getCognateList(),
        new RemoveWordFromCognateDropdownListener()
      );
    }
  }

  private class RemoveWordFromCognateDropdownListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      view.closeDropdown();
      JMenuItem i = (JMenuItem) (e.getSource());
      String cognate = i.getText();
      removeWordFromCognateEnding(cognate);
    }
  }

  private void removeWordFromCognateEnding(String cognate) {

    List<String> failedWords = new ArrayList<String>();
    List<Point> points = view.getSelected();
    for (Point p : points)
      if (!(model.removeWordFromCognate1(p, cognate)))
        failedWords.add(model.getWordlist().getMC(p).getPrimary());
    if (!(failedWords.isEmpty())) {
      StringBuffer msg = new StringBuffer();
      msg.append("Failed to remove the following " + ((failedWords.size() > 1) ? "words" : "word") + " from cognate group \"" + cognate + "\":\n");
      for (String s : failedWords) msg.append("\"" + s + "\"\n");
      view.message(msg.toString());
    }
    commonConclude();
  }
  








/**********************************************************************/
/**********************************************************************/
/**********************************************************************/









  private class RenameCognateButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {


      List<Point> points = view.getSelected();
      if (points.isEmpty()) {
        view.message("No cognate groups selected.");
        return;
      }
      int row = points.get(0).x;
      for (Point p : points) {
        if (p.x != row) {
          view.message("Multiple cognate groups selected.");
          commonConclude();
          return;
        }
      }
      String oldCognate = model.getCognateList().getMC(new Point(row, 0)).getPrimary();
      String newCognate = view.prompt("Rename cognate group \"" + oldCognate + "\":");
      if (newCognate == null || newCognate.isEmpty() || (!(model.renameCognate(row, newCognate)))) {
        view.message("Failed to rename cognate group from \"" + oldCognate + "\" to \"" + newCognate + "\".");
        commonConclude();
        return;
      }
      commonConclude();
    }
  }









  private class RemoveWordFromCognate2ButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      List<Point> points = view.getSelected();
      if (points.isEmpty()) {
        view.message("No words selected.");
        return;
      }
      List<String> failedWords = new ArrayList<String>();
      for (Point p : points) {
        if (!(model.removeWordFromCognate2(p))) {
          failedWords.add(model.getCognateList().getS(p));
        }
      }
      if (!(failedWords.isEmpty())) {
        StringBuffer msg = new StringBuffer();
        msg.append("Failed to remove the following " + ((failedWords.size() > 1) ? "words" : "word") + " from cognate " + ((failedWords.size() > 1) ? "groups" : "group") + ":\n");
        for (String s : failedWords) msg.append("\"" + s + "\"\n");
        view.message(msg.toString());
      }
      commonConclude();
    }
  }









  private class DeleteCognateButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      List<Point> points = view.getSelected();
      if (points.isEmpty()) {
        view.message("No words selected.");
        return;
      }
      Set<String> cognates = new HashSet<String>();
      for (Point p : points) cognates.add(model.getCognateList().getMC(new Point(p.x, 0)).getPrimary());
      List<String> failedRows = new ArrayList<String>();
      for (String c : cognates) if (!(model.deleteCognate(c))) failedRows.add(c);
      if (!(failedRows.isEmpty())) {
        StringBuffer msg = new StringBuffer();
        msg.append("Failed to remove the following cognate " + ((failedRows.size() > 1) ? "groups" : "group") + ":\n");
        for (String c : failedRows) msg.append("\"" + c + "\"\n");
      }
      commonConclude();
    }
  }
  








  private class AssignCognateToCorrespondenceButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      assignA2B(
        e,
        model.getCognateList(),
        model.getCorrespondenceList(),
        new AssignCognateToCorrespondenceDropdownAddNewAutoNameListener(),
        new AssignCognateToCorrespondenceDropdownAddNewListener(),
        new AssignCognateToCorrespondenceDropdownCommonListener()
      );
    }
  }
  
  private class AssignCognateToCorrespondenceDropdownAddNewAutoNameListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      view.closeDropdown();
      String correspondenceBase = "Correspondence";
      int counter = 1;
      String correspondence = correspondenceBase + " " + Integer.toString(counter ++);
      while (model.getCorrespondenceList().getIndexOfLabel(correspondence) >= 0)
        correspondence = correspondenceBase + " " + Integer.toString(counter ++);
      if (!model.createCorrespondence(correspondence)) {
        view.message("Failed to create new correspondence group \"" + correspondence + "\"");
        commonConclude();
      }
      assignCognateToCorrespondenceAction(correspondence);
    }
  }

  private class AssignCognateToCorrespondenceDropdownAddNewListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      view.closeDropdown();
      String correspondence = view.prompt("Enter name of new correspondence group:");
      if (correspondence == null || correspondence.isEmpty() || (!(model.createCorrespondence(correspondence)))) {
        view.message("Failed to create new correspondence group \"" + correspondence + "\"");
        commonConclude();
      }
      assignCognateToCorrespondenceAction(correspondence);
    }
  }

  private class AssignCognateToCorrespondenceDropdownCommonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      view.closeDropdown();
      JMenuItem i = (JMenuItem) (e.getSource());
      String correspondence = i.getText();
      assignCognateToCorrespondenceAction(correspondence);
    }
  }

  private void assignCognateToCorrespondenceAction(String correspondence) {

    List<String> failedCognates = new ArrayList<String>();
    List<Point> allPoints = view.getSelected();
    List<Point> points = new ArrayList<Point>();
    for (Point p : allPoints)
      if (!points.contains(new Point(p.x, 0)))
        points.add(new Point(p.x, 0));
    for (Point p : points)
      if (!(model.assignCognateToCorrespondence(p, correspondence)))
        failedCognates.add(model.getCognateList().getMC(p).getPrimary());
    if (!(failedCognates.isEmpty())) {
      StringBuffer msg = new StringBuffer();
      msg.append("Failed to add the following " + ((failedCognates.size() > 1) ? "cognates" : "cognate") + " to correspondence group \"" + correspondence + "\":\n");
      for (String s : failedCognates) msg.append("\"" + s + "\"\n");
      view.message(msg.toString());
    }
    commonConclude();
  }


  






  private class RemoveCognateFromCorrespondence1ButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      removeAfB(
        e,
        model.getCognateList(),
        model.getCorrespondenceList(),
        new RemoveCognateFromCorrespondenceDropdownListener()
      );
    }
  }

  private class RemoveCognateFromCorrespondenceDropdownListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      view.closeDropdown();
      JMenuItem i = (JMenuItem) (e.getSource());
      String correspondence = i.getText();
      removeCognateFromCorrespondenceEnding(correspondence);
    }
  }

  private void removeCognateFromCorrespondenceEnding(String correspondence) {

    List<String> failedCognates = new ArrayList<String>();
    List<Point> allPoints = view.getSelected();
    List<Point> points = new ArrayList<Point>();
    for (Point p : allPoints)
      if (!points.contains(new Point(p.x, 0)))
        points.add(new Point(p.x, 0));
    for (Point p : points)
      if (!(model.removeCognateFromCorrespondence1(p, correspondence)))
        failedCognates.add(model.getCognateList().getMC(p).getPrimary());
    if (!(failedCognates.isEmpty())) {
      StringBuffer msg = new StringBuffer();
      msg.append("Failed to remove the following " + ((failedCognates.size() > 1) ? "cognates" : "cognate") + " from correspondence group \"" + correspondence + "\":\n");
      for (String s : failedCognates) msg.append("\"" + s + "\"\n");
      view.message(msg.toString());
    }
    commonConclude();
  }
  








/**********************************************************************/
/**********************************************************************/
/**********************************************************************/









  private class RenameCorrespondenceButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      List<Point> points = view.getSelected();
      if (points.isEmpty()) {
        view.message("No correspondence group selected.");
        return;
      }
      Point point = new Point(points.get(0).x, 0);
      String oldCorrespondence = model.getCorrespondenceList().getMC(point).getPrimary();
      String newCorrespondence = view.prompt("Rename correspondence group \"" + oldCorrespondence + "\":");
      if (newCorrespondence == null || newCorrespondence.isEmpty() || (!(model.renameCorrespondence(point.x, newCorrespondence)))) {
        view.message("Failed to rename correspondence group from \"" + oldCorrespondence + "\" to \"" + newCorrespondence + "\".");
        commonConclude();
        return;
      }
      commonConclude();
    }
  }









  private class EditCorrespondenceButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      List<Point> points = view.getSelected();
      if (points.isEmpty()) {
        view.message("No correspondence cell selected.");
        return;
      }
      if (points.size() > 1) {
        view.message("Must choose one cell.");
        commonConclude();
        return;
      }
      Point point = points.get(0);
      if (point.y == 0) {
        view.message("Must choose a cell under a language column.");
        commonConclude();
        return;
      }
      String sound = view.prompt("Enter sound sequence:");
      if (sound == null || sound.isEmpty() || (!(model.editCorrespondence(point, sound)))) {
        view.message("Failed to set sound sequence \"" + sound + "\".");
        commonConclude();
        return;
      }
      commonConclude();
    }
  }









  private class RemoveCognateFromCorrespondence2ButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      if (view.getDropdownOpen()) {
        view.closeDropdown();
        return;
      }
      List<Point> points = view.getSelected();
      if (points.isEmpty()) {
        view.message("No correspondence group selected.");
        return;
      }
      MultiCell correspondenceLabel = model.getCorrespondenceList().getMC(new Point(points.get(0).x, 0));
      List<String> cognates = correspondenceLabel.getSecondaries();
      if (cognates.isEmpty()) {
        view.message("No cognate groups to remove.");
        commonConclude();
        return;
      }
      if (cognates.size() > 1)
        view.sendDropdown(
          cognates,
          e,
          null,
          null,
          new RemoveCognateFromCorrespondence2DropdownListener()
        );
      else {
        removeCognateFromCorrespondence2Ending(cognates.get(0));
      }
    }
  }

  private class RemoveCognateFromCorrespondence2DropdownListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      view.closeDropdown();
      JMenuItem i = (JMenuItem) (e.getSource());
      String cognate = i.getText();
      removeCognateFromCorrespondence2Ending(cognate);
    }
  }

  private void removeCognateFromCorrespondence2Ending(String cognate) {

    Point point = view.getSelected().get(0);
    if (!model.removeCognateFromCorrespondence2(point.x, cognate)) {
      view.message(
        "Failed to remove the cognate group \""
        + cognate
        + "\" from the correspondence group \""
        + model.getCorrespondenceList().getMC(new Point(point.x, 0)).getPrimary()
        + "\""
      );
    }
    commonConclude();
  }









  private class DeleteCorrespondenceButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      List<Point> points = view.getSelected();
      if (points.isEmpty()) {
        view.message("No correspondence selected.");
        return;
      }
      Point point = new Point(points.get(0).x, 0);
      String correspondence = model.getCorrespondenceList().getMC(point).getPrimary();
      if (!model.deleteCorrespondence(correspondence)) {
        view.message("Failed to delete correspondence group \"" + correspondence + "\".");
      }
      commonConclude();
    }
  }









  ////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////









  private class ExportCognateListButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      String csvPath = view.prompt("Enter name of Cognate List CSV file:");
      if (csvPath == null || csvPath.isEmpty() || csvPath.isBlank()) return;
      model.writeCognateList(Paths.get(filesPath.toString(), csvPath).toString());
    }
  }



  private class ExportCorrespondenceListButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      String csvPath = view.prompt("Enter name of Correspondence List CSV file:");
      if (csvPath == null || csvPath.isEmpty() || csvPath.isBlank()) return;
      model.writeCorrespondenceList(Paths.get(filesPath.toString(), csvPath).toString());
    }
  }
}
