import java.awt.Color;
import java.awt.Font;

public class IrelMain {

  private static ViewSettings CONTRAST_LIGHT = new ViewSettings(
    new ColorEnv(
      Color.decode("#ffffff"),
      Color.decode("#000000"),
      Color.decode("#000000")
    ),
    new ColorEnv(
      Color.decode("#ffffff"),
      Color.decode("#ffffff"),
      Color.decode("#000000")
    ),
    new ColorEnv(
      Color.decode("#ffffff"),
      Color.decode("#000000"),
      Color.decode("#0000ff")
    ),
    new ColorEnv(
      Color.decode("#0000ff"),
      Color.decode("#ffffff"),
      Color.decode("#0000ff")
    ),
    new Font(
      "SanSerif",
      Font.PLAIN,
      24
    ),
    new Font(
      "SanSerif",
      Font.PLAIN,
      16
    ),
    new Font(
      "SanSerif",
      Font.PLAIN,
      12
    )
  );

  private static ViewSettings CONTRAST_DARK = new ViewSettings(
    new ColorEnv(
      Color.decode("#000000"),
      Color.decode("#ffffff"),
      Color.decode("#ffffff")
    ),
    new ColorEnv(
      Color.decode("#000000"),
      Color.decode("#000000"),
      Color.decode("#ffffff")
    ),
    new ColorEnv(
      Color.decode("#000000"),
      Color.decode("#ffffff"),
      Color.decode("#00ffff")
    ),
    new ColorEnv(
      Color.decode("#00ffff"),
      Color.decode("#000000"),
      Color.decode("#00ffff")
    ),
    new Font(
      "SanSerif",
      Font.PLAIN,
      24
    ),
    new Font(
      "SanSerif",
      Font.PLAIN,
      16
    ),
    new Font(
      "SanSerif",
      Font.PLAIN,
      12
    )
  );

  public static void main(
      String[] args
    ) {

    IrelModel model = new IrelModel();
    IrelView view = new IrelView(CONTRAST_DARK);
    IrelController controller = new IrelController(
      model, view
    );
    controller.run();
  }
}
