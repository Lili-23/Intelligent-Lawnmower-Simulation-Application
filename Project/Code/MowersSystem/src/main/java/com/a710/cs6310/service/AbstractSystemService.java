package com.a710.cs6310.service;

import com.a710.cs6310.model.form.SystemStatus;

import java.io.InputStream;

public interface AbstractSystemService {
    SystemStatus readFile(String filename);

    SystemStatus getNextTurnStatus();

//    Not support auto run
//    SystemStatus getFinalTurnStatus();

    SystemStatus quitSimulation();

    SystemStatus uploadFile(InputStream fileStream);
}
