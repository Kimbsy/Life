import java.awt.*;
import java.util.*;

public class Cell extends Sprite implements Living {

  // Class constants.
  public static final int NONE  = 0;
  public static final int UP    = 1;
  public static final int DOWN  = 2;
  public static final int LEFT  = 3;
  public static final int RIGHT = 4;

  // The size of the Cell.
  protected int size = 5;

  // List of moves.
  protected int[][] moveList;

  // Which move the Cell is on.
  protected int moveIndex = 0;

  // How far the Cell has travelled for this move.
  protected int distanceMoved = 0;

  // The amount of energy the Cell has.
  protected float energy = 512;

  // How quickly the Cell uses energy.
  protected float metabolicRate = 1.2f;

  // What fraction of the Cell's movement is stationary.
  protected float stationaryFactor = 1;

  /**
   * Constructs a Cell specifying coordinates.
   *
   * Uses the default shape, color and move list.
   *
   * @param  x  The X coordinate of the Cell.
   * @param  y  The Y coordinate of the Cell.
   */
  public Cell(int x, int y) {
    super(x, y);

    // Set defaults.
    setShape(getDefaultShape());
    setColor(getDefaultColor());
    setMoveList(getDefaultMoveList());
    updateStationaryFactor();

    energy = CellularSimulator.rand.nextInt(1024);
  }

  /**
   * Constructs a Cell specifying coordinates, shape and moveList.
   *
   * @param  x         The X coordinate of the Cell.
   * @param  y         The Y coordinate of the Cell.
   * @param  shape     The shape of the Cell.
   * @param  moveList  The move list fo the Cell.
   */
  public Cell(int x, int y, Shape shape, int[][] moveList) {
    super(x, y);

    setShape(shape);
    setColor(color);
    setMoveList(moveList);
    energy = CellularSimulator.rand.nextInt(1024);
  }

  /**
   * Gets the default shape for a Cell.
   *
   * @return  The default shape.
   */
  public static Polygon getDefaultShape() {
    int[] xPoints = {0, 5, 5, 0};
    int[] yPoints = {0, 0, 5, 5};
    Polygon poly  = new Polygon(xPoints, yPoints, 4);
    return poly;
  }

  /**
   * Gets the default color for a Cell.
   *
   * @return  The default color.
   */
  public static Color getDefaultColor() {
    Color color = Color.GREEN;
    return color;
  }

  /**
   * Gets the color of a Cell based on its energy level.
   *
   * @return  The color of the Cell.
   */
  public Color getColor() {
    float percentage = energy / 1024;
    int normalised   = (int) (percentage * 205);
    normalised = Math.min(normalised, 205);

    int r = normalised;
    int g = normalised + 50;
    int b = normalised;

    return new Color(r, g, b);
  }

  /**
   * Gets the default move list for a Cell.
   *
   * @return  The default move list.
   */
  public static int[][] getDefaultMoveList() {
    int[] directions = {UP, RIGHT, DOWN, LEFT};
    int[] distances  = {20, 20, 20, 20};
    int[][] moveList = {directions, distances};
    return moveList;
  }

  /**
   * Gets a random move list for a Cell.
   *
   * @return  The random move list.
   */
  public static int[][] getRandomMoveList() {
    int moveCount    = CellularSimulator.rand.nextInt(9) + 1;
    int[] directions = new int[moveCount];
    int[] distances  = new int[moveCount];

    for (int i = 0; i < moveCount; i++) {
      directions[i] = CellularSimulator.rand.nextInt(5);
      distances[i]  = CellularSimulator.rand.nextInt(10) + 1;
    }

    // int[][] list = {{0},{1}};
    // return list;

    int[][] moveList = {directions, distances};
    return moveList;
  }

  /**
   * Gets the list of moves.
   *
   * @return  The list of moves.
   */
  public int[][] getMoveList() {
    return moveList;
  }

  /**
   * Sets the list of moves.
   *
   * @param  moveList  The list of moves.
   */
  public void setMoveList(int[][] moveList) {
    this.moveList = moveList;
  }

  /**
   * Gets which move the Cell is on.
   *
   * @return  The move index.
   */
  public int getMoveIndex() {
    return moveIndex;
  }

  /**
   * Sets which move the Cell is on.
   *
   * @param  moveIndex  The move index
   */
  public void setMoveIndex(int moveIndex) {
    this.moveIndex = moveIndex;
  }

  /**
   * Increments the move index.
   *
   * Wraps around to the start of the list.
   *
   * @param  i  How much to increment by.
   */
  public void incMoveIndex(int i) {
    moveIndex = (moveIndex + i) % moveList[0].length;
  }

  /**
   * Gets how far the Cell has travelled for this move.
   *
   * @return  How far the Cell has travelled.
   */
  public int getDistanceMoved() {
    return distanceMoved;
  }

  /**
   * Sets how far the Cell has travelled for this move.
   *
   * @param  distanceMoved  How far the Cell has travelled for this move.
   */
  public void setDistanceMoved(int distanceMoved) {
    this.distanceMoved = distanceMoved;
  }

  /**
   * Increments how far the Cell has travelled for this move.
   *
   * @param  i  How much to increment by.
   */
  public void incDistanceMoved(int i) {
    distanceMoved += i;
  }

  public int getCurrentMove() {
    return moveList[0][moveIndex];
  }

  /**
   * Gets the amount of energy the Cell has.
   *
   * @return  The amount of energy.
   */
  public float getEnergy() {
    return energy;
  }

  /**
   * Sets the amount of energy the Cell has.
   *
   * @param  energy  The amount of energy.
   */
  public void setEnergy(float energy) {
    this.energy = energy;
  }

  /**
   * Gets the rate at which the Cell loses energy.
   *
   * @return  The metabolic rate.
   */
  public float getMetabolicRate() {
    return metabolicRate;
  }

  /**
   * Sets the rate at which the Cell loses energy.
   */
  public void setMetabolicRate(float metabolicRate) {
    this.metabolicRate = metabolicRate;
  }

  /**
   * Determines and updates what fraction of the time the Cell spends stationary.
   */
  public void updateStationaryFactor() {
    float total      = 0;
    float stationary = 0;

    for (int i = 0; i < moveList[0].length; i++) {
      total += moveList[1][i];
      if (moveList[0][i] == Cell.NONE) {
        stationary += moveList[1][i];
      }
    }

    stationaryFactor = stationary / total;
  }

  /**
   * Updates this Cell.
   *
   * @param  sim  The simulation.
   */
  public void update(CellularSimulator sim) {
    move();
    absorb(sim.foodMap);
    divide(sim.cells);
    metabolise(sim.cells);
  }

  /**
   * Moves the Cell based on its moveList, moveIndex and distanceMoved.
   */
  public void move() {
    int distance = moveList[1][moveIndex];

    if (distanceMoved >= distance) {
      setDistanceMoved(0);
      incMoveIndex(1);
    }

    switch (getCurrentMove()) {
      case NONE:
        break;
      case UP:
        incY(-1);
        break;
      case DOWN:
        incY(1);
        break;
      case LEFT:
        incX(-1);
        break;
      case RIGHT:
        incX(1);
        break;
    }

    incDistanceMoved(1);
  }

  /**
   * Absorbs energy from the surrounding environment.
   */
  public void absorb(FoodMap foodMap) {
    int minX = x;
    int maxX = x + size;
    int minY = y;
    int maxY = y + size;

    float absorbedEnergy = foodMap.absorbFromArea(minX, maxX, minY, maxY);

    if (getCurrentMove() == Cell.NONE) {
      double multiplier = 4 * Math.pow(stationaryFactor, 4);
      absorbedEnergy *= Math.max(multiplier, 1);
    }

    energy = Math.min((energy + absorbedEnergy), 1024);
  }

  /**
   * Creates a new Cell based on this one.
   *
   * @param  cells  The CellCollection.
   */
  public void divide(CellCollection cells) {
    if (energy >= 1024) {
      float newEnergy = energy / 2;
      setEnergy(newEnergy);

      Cell child = createChildCell();
      child.setEnergy(newEnergy);
      cells.add(child);
    }
  }

  public Cell createChildCell() {
    int x = this.x + CellularSimulator.rand.nextInt(size * 2) - size;
    int y = this.y + CellularSimulator.rand.nextInt(size * 2) - size;
    Cell child = new Cell(x, y, getShape(), getMoveList());
    return child;
  }

  /**
   * Reduces the energy level of the Cell.
   *
   * If the energy level drops to 0, the Cell is removed from the CellCollection.
   *
   * @param  cells  The CellCollection.
   */
  public void metabolise(CellCollection cells) {
    float reduction = metabolicRate;

    // Staying still require no energy.
    if (getCurrentMove() == Cell.NONE) {
      double multiplier = 50 * Math.pow(stationaryFactor, 4);
      reduction /= Math.max(multiplier, 1);
    }

    energy = Math.max((energy - reduction), 0);

    // Destroy Cells with no energy.
    if (energy == 0) {
      cells.remove(this);
    }
  }
}
