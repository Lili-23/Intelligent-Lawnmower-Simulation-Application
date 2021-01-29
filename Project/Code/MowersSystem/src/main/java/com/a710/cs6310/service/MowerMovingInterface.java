package com.a710.cs6310.service;



import com.a710.cs6310.common.MowerAction;
import com.a710.cs6310.model.Mower;
import com.a710.cs6310.model.SharedData;

import java.util.stream.Collectors;

public interface MowerMovingInterface {
    Mower getMower();

    SharedData getSharedData();

    void pollMowerForAction();

    default String getActionMessage() {
        Mower mower = getMower();
        String result = "";
        MowerAction action = mower.getCurrentAction();
        switch (action) {
            case STEER:
                result = "M" + mower.getId()  + ": " + mower.getCurrentAction().name();
                break;

            case CSCAN:
            case LSCAN:
                result = "M" + mower.getId() + "," + action.name() + "\n";
                result += String.join(",", mower.getScanResult().stream()
                        .map(item -> item.name())
                        .collect(Collectors.toList()));
                break;

            case PASS:
                result = "M" + mower.getId() + ",PASS";
                break;

            case MOVE:
                result = "M" + mower.getId() + ",MOVE";
                break;
        }

        return result.toLowerCase();
    }
}
