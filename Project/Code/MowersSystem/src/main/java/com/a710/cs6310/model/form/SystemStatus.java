package com.a710.cs6310.model.form;

public class SystemStatus {
    private int _currentTurn;
    private int _turnLimit;
    private int _totalGrass;
    private int _remainGrass;
    private int _lawnWidth;
    private int _lawnHeight;

    // Defined in GameState
    private String _gameState;

    // Can be the current action's description
    private String _message;
    private int _mowerNumber;
    private int _gopherNumber;

    // Defined in MowerState
    // And the current energy level. The order of the array will be aline with mower's index.
    // e.g.
    /*    ACTIVE        20     NORTH        //mower 1
          CRASH         15     NORTHWEST    //mower 2
     */
    private String[][] _mowerInfo;

    private int _moverMaxEnergy;

    // Follow is one example for the lawn info. For mower, it will always come with direction.
    // We need to use those string to generate name for pictures
    // TODO we can think about performance, e.g. after load, every time font end only need the changed cells
    //  information not the whole lawn
    /*
    GRASS        GRASS  GOPHER_GRASS  EMPTY  GOPHER_GRASS
  MOWER_NORTH    GRASS  EMPTY         EMPTY  GRASS
    EMPTY        EMPTY  GRASS         GRASS  MOWER_NORTHEAST
     */
    private String[][] _currentLawn;

    public int getCurrentTurn() {
        return _currentTurn;
    }

    public SystemStatus setCurrentTurn(int currentTurn) {
        this._currentTurn = currentTurn;
        return this;
    }

    public int getTurnLimit() {
        return _turnLimit;
    }

    public SystemStatus setTurnLimit(int turnLimit) {
        this._turnLimit = turnLimit;
        return this;
    }

    public int getTotalGrass() {
        return _totalGrass;
    }

    public SystemStatus setTotalGrass(int totalGrass) {
        this._totalGrass = totalGrass;
        return this;
    }

    public int getRemainGrass() {
        return _remainGrass;
    }

    public SystemStatus setRemainGrass(int remainGrass) {
        this._remainGrass = remainGrass;
        return this;
    }

    public int getLawnWidth() {
        return _lawnWidth;
    }

    public SystemStatus setLawnWidth(int width) {
        _lawnWidth = width;
        return this;
    }

    public int getLawnHeight() {
        return _lawnHeight;
    }

    public SystemStatus setLawnHeight(int height) {
        _lawnHeight = height;
        return this;
    }

    public String getMessage() {
        return _message;
    }

    public SystemStatus setMessage(String message) {
        this._message = message;
        return this;
    }

    public int getMowerNumber() {
        return _mowerNumber;
    }

    public SystemStatus setMowerNumber(int mowerNumber) {
        this._mowerNumber = mowerNumber;
        return this;
    }

    public String[][] getMowerStateInfo() {
        return _mowerInfo;
    }

    public SystemStatus setMowerInfo(String[][] mowerStateEnergy) {
        this._mowerInfo = mowerStateEnergy;
        return this;
    }

    public int getMoverMaxEnergy() {
        return _moverMaxEnergy;
    }

    public SystemStatus setMoverMaxEnergy(int moverMaxEnergy) {
        this._moverMaxEnergy = moverMaxEnergy;
        return this;
    }

    public String[][] getCurrentLawn() {
        return _currentLawn;
    }

    public SystemStatus setCurrentLawn(String[][] currentLawn) {
        this._currentLawn = currentLawn;
        return this;
    }

    public String getGameState() {
        return _gameState;
    }

    public SystemStatus setGameState(String gameState) {
        this._gameState = gameState;
        return this;
    }

    public int getGopherNumber() {
        return _gopherNumber;
    }

    public SystemStatus setGopherNumber(int gopherNumber) {
        this._gopherNumber = gopherNumber;
        return this;
    }
}
