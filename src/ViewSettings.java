import java.awt.Font;

public class ViewSettings {

  public ColorEnv e0;
  public ColorEnv e1;
  public ColorEnv e2;
  public ColorEnv e3;

  public Font f1;
  public Font f2;
  public Font f3;

  public ViewSettings(
      ColorEnv e0,
      ColorEnv e1,
      ColorEnv e2,
      ColorEnv e3,
      Font f1,
      Font f2,
      Font f3
    ) {

    this.e0 = e0;
    this.e1 = e1;
    this.e2 = e2;
    this.e3 = e3;

    this.f1 = f1;
    this.f2 = f2;
    this.f3 = f3;
  }
}
