
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class IrelView extends JFrame {

  private JPanel panel;

  private JPanel topPanel;
  private TabButton wordlistTab;
  private TabButton cognateListTab;
  private TabButton correspondenceListTab;

  private ScrollTable table;

  private JPanel buttonsPanel;

  private JPanel wordlistButtonsPanel;
  private IrelButton autoPopulateCognatesFromWordsButton;
  private IrelButton assignWordToCognateButton;
  private IrelButton removeWordFromCognate1Button;

  private JPanel cognateListButtonsPanel;
  private IrelButton renameCognateButton;
  private IrelButton removeWordFromCognate2Button;
  private IrelButton deleteCognateButton;
  private IrelButton assignCognateToCorrespondenceButton;
  private IrelButton removeCognateFromCorrespondence1Button;

  private JPanel correspondenceListButtonsPanel;
  private IrelButton renameCorrespondenceButton;
  private IrelButton editCorrespondenceButton;
  private IrelButton removeCognateFromCorrespondence2Button;
  private IrelButton deleteCorrespondenceButton;

  private JPanel bottomPanel;
  private IrelButton exportCognateListButton;
  private IrelButton exportCorrespondenceListButton;

  private IrelDropdown dropdown;
  private boolean dropdownOpen;

  private ViewSettings viewSettings;

  public IrelView(ViewSettings s) {

    this.viewSettings = s;

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1080, 720);
    setBackground(s.e0.bg);

    panel = new JPanel();
    panel.setBackground(s.e0.bg);
    panel.setLayout(new BorderLayout());
    
    topPanel = new JPanel();
    topPanel.setLayout(new FlowLayout());
    topPanel.setOpaque(false);
    topPanel.setLayout(new FlowLayout());
    wordlistTab = new TabButton("Wordlist", s);
    cognateListTab = new TabButton("Cognates", s);
    correspondenceListTab = new TabButton("Correspondences", s);
    topPanel.add(wordlistTab);
    topPanel.add(cognateListTab);
    topPanel.add(correspondenceListTab);
    panel.add(topPanel, BorderLayout.NORTH);

    table = new ScrollTable(s);
    panel.add(table, BorderLayout.CENTER);

    buttonsPanel = new JPanel();
    buttonsPanel.setOpaque(false);
    panel.add(buttonsPanel, BorderLayout.WEST);

    wordlistButtonsPanel = new JPanel();
    wordlistButtonsPanel.setLayout(new BoxLayout(wordlistButtonsPanel, BoxLayout.Y_AXIS));
    wordlistButtonsPanel.setOpaque(false);
    autoPopulateCognatesFromWordsButton = new IrelButton("Auto Populate Cognate List", s);
    assignWordToCognateButton = new IrelButton("Assign Word to Cognate Group", s);
    removeWordFromCognate1Button = new IrelButton("Remove Word from Cognate Group", s);
    wordlistButtonsPanel.add(autoPopulateCognatesFromWordsButton);
    wordlistButtonsPanel.add(assignWordToCognateButton);
    wordlistButtonsPanel.add(removeWordFromCognate1Button);

    cognateListButtonsPanel = new JPanel();
    cognateListButtonsPanel.setLayout(new BoxLayout(cognateListButtonsPanel, BoxLayout.Y_AXIS));
    cognateListButtonsPanel.setOpaque(false);
    renameCognateButton = new IrelButton("Rename Cognate Group", s);
    removeWordFromCognate2Button = new IrelButton("Remove Word from Cognate Group", s);
    deleteCognateButton = new IrelButton("Delete Cognate Group", s);
    assignCognateToCorrespondenceButton = new IrelButton("Assign Cognate to Correspondence Group", s);
    removeCognateFromCorrespondence1Button = new IrelButton("Remove Cognate from Correspondence Group", s);
    cognateListButtonsPanel.add(renameCognateButton);
    cognateListButtonsPanel.add(removeWordFromCognate2Button);
    cognateListButtonsPanel.add(deleteCognateButton);
    cognateListButtonsPanel.add(assignCognateToCorrespondenceButton);
    cognateListButtonsPanel.add(removeCognateFromCorrespondence1Button);

    correspondenceListButtonsPanel = new JPanel();
    correspondenceListButtonsPanel.setLayout(new BoxLayout(correspondenceListButtonsPanel, BoxLayout.Y_AXIS));
    correspondenceListButtonsPanel.setOpaque(false);
    renameCorrespondenceButton = new IrelButton("Rename Correspondence Group", s);
    editCorrespondenceButton = new IrelButton("Set Sound Sequence", s);
    removeCognateFromCorrespondence2Button = new IrelButton("Remove Cognate from Correspondence Group", s);
    deleteCorrespondenceButton = new IrelButton("Delete Correspondence Group", s);
    correspondenceListButtonsPanel.add(renameCorrespondenceButton);
    correspondenceListButtonsPanel.add(editCorrespondenceButton);
    correspondenceListButtonsPanel.add(removeCognateFromCorrespondence2Button);
    correspondenceListButtonsPanel.add(deleteCorrespondenceButton);

    bottomPanel = new JPanel();
    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
    bottomPanel.setOpaque(false);
    exportCognateListButton = new IrelButton("Export Cognate List", s);
    exportCorrespondenceListButton = new IrelButton("Export Correspondence List", s);
    bottomPanel.add(exportCognateListButton);
    bottomPanel.add(exportCorrespondenceListButton);
    panel.add(bottomPanel, BorderLayout.SOUTH);

    dropdown = new IrelDropdown(this, s);
    dropdownOpen = false;

    add(panel);
  }

  public List<Point> getSelected() {
    return table.getSelected();
  }

  public void clearSelected() {
    table.clearSelected();
  }

  public void updateRowHeights() {
    table.updateRowHeights();
  }

  private void switchListStandard(MultiCellTableModel m, int t) {

    table.setModel(m);
    if (t == 0) wordlistTab.setOn(); else wordlistTab.setOff();
    if (t == 1) cognateListTab.setOn(); else cognateListTab.setOff();
    if (t == 2) correspondenceListTab.setOn(); else correspondenceListTab.setOff();
    buttonsPanel.removeAll();
    buttonsPanel.add(
      (t == 0) ?
        wordlistButtonsPanel :
      (t == 1) ?
        cognateListButtonsPanel :
      (t == 2) ?
        correspondenceListButtonsPanel :
      null
    );
    revalidate();
    repaint();

  }

  public void setWordlist(MultiCellTableModel tableModel) {
    switchListStandard(tableModel, 0);
  }

  public void setCognateList(MultiCellTableModel tableModel) {
    switchListStandard(tableModel, 1);
  }

  public void setCorrespondenceList(MultiCellTableModel tableModel) {
    switchListStandard(tableModel, 2);
  }

  public void setTabListeners(ActionListener wordlistTabListener,
      ActionListener cognateListTabListener,
      ActionListener correspondenceListTabListener
    ) {

    wordlistTab.addActionListener(wordlistTabListener);
    cognateListTab.addActionListener(cognateListTabListener);
    correspondenceListTab.addActionListener(correspondenceListTabListener);
  }

  public void setWordlistButtonListeners(
      ActionListener autoPopulateCognatesFromWordsButtonListener,
      ActionListener assignWordToCognateButtonListener,
      ActionListener removeWordFromCognate1ButtonListener
    ) {

    autoPopulateCognatesFromWordsButton.addActionListener(autoPopulateCognatesFromWordsButtonListener);
    assignWordToCognateButton.addActionListener(assignWordToCognateButtonListener);
    removeWordFromCognate1Button.addActionListener(removeWordFromCognate1ButtonListener);
  }

  public void setCognateListButtonListeners(
      ActionListener renameCognateButtonListener,
      ActionListener removeWordFromCognate2ButtonListener,
      ActionListener deleteCognateButtonListener,
      ActionListener assignCognateToCorrespondenceButtonListener,
      ActionListener removeCognateFromCorrespondence1ButtonListener
    ) {

    renameCognateButton.addActionListener(renameCognateButtonListener);
    removeWordFromCognate2Button.addActionListener(removeWordFromCognate2ButtonListener);
    deleteCognateButton.addActionListener(deleteCognateButtonListener);
    assignCognateToCorrespondenceButton.addActionListener(assignCognateToCorrespondenceButtonListener);
    removeCognateFromCorrespondence1Button.addActionListener(removeCognateFromCorrespondence1ButtonListener);
  }

  public void setCorrespondenceListButtonListeners(
      ActionListener renameCorrespondenceButtonListener,
      ActionListener editCorrespondenceButtonListener,
      ActionListener removeCognateFromCorrespondence2ButtonListener,
      ActionListener deleteCorrespondenceButtonListener
    ) {

    renameCorrespondenceButton.addActionListener(renameCorrespondenceButtonListener);
    editCorrespondenceButton.addActionListener(editCorrespondenceButtonListener);
    removeCognateFromCorrespondence2Button.addActionListener(removeCognateFromCorrespondence2ButtonListener);
    deleteCorrespondenceButton.addActionListener(deleteCorrespondenceButtonListener);
  }

  public void setBottomListeners(
      ActionListener exportCognateListButtonListener,
      ActionListener exportCorrespondenceListButtonListener
    ) {

    exportCognateListButton.addActionListener(exportCognateListButtonListener);
    exportCorrespondenceListButton.addActionListener(exportCorrespondenceListButtonListener);
  }

  public void message(String msg) {
    JOptionPane.showMessageDialog(this, msg);
  }

  public String prompt(String msg) {
    return JOptionPane.showInputDialog(this, msg);
  }

  public void sendDropdown(
      Collection<String> c,
      ActionEvent e,
      ActionListener addNewAutoNameListener,
      ActionListener addNewListener,
      ActionListener commonListener
    ) {
    
    dropdownOpen = true;
    dropdown.display(
      c,
      (JButton) (e.getSource()),
      addNewAutoNameListener,
      addNewListener,
      commonListener
    );
  }

  public void closeDropdown() {

    dropdownOpen = false;
    dropdown.clear();
  }

  public boolean getDropdownOpen() {

    return dropdownOpen;
  }
}
