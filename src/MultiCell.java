
import java.util.ArrayList;
import java.util.List;

public class MultiCell {

  private String primary;
  private List<String> secondaries;
  private CellType type;

  public MultiCell() {
    primary = "";
    secondaries = new ArrayList<String>();
    type = CellType.NONE;
  }

  public MultiCell(String primary, List<String> secondaries, CellType type) {
    this.primary = primary;
    this.secondaries = secondaries;
    this.type = type;
  }

  public String getPrimary() {
    return primary;
  }

  public List<String> getSecondaries() {
    return secondaries;
  }

  public CellType getType() {
    return type;
  }

  public void setPrimary(String primary) {
    this.primary = primary;
  }

  public void setSecondaries(List<String> secondaries) {
    this.secondaries = secondaries;
  }

  public void setSecondary(int i, String secondary) {
    this.secondaries.set(i, secondary);
  }

  public void replaceSecondary(String oldSec, String newSec) {
    secondaries.set(secondaries.indexOf(oldSec), newSec);
  }

  public void addSecondary(String secondary) {
    secondaries.add(secondary);
  }

  public void addSecondary(int i, String secondary) {
    secondaries.add(i, secondary);
  }

  public void removeSecondary(String secondary) {
    secondaries.remove(secondary);
  }

  public void removeSecondary(int i) {
    secondaries.remove(i);
  }

  public void setType(CellType type) {
    this.type = type;
  }
}

enum CellType {
  NONE,
  WORD,
  COGNATE,
  CORRESPONDENCE,
}
