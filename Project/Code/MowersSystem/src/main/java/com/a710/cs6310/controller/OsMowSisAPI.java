package com.a710.cs6310.controller;

import com.a710.cs6310.common.GameState;
import com.a710.cs6310.common.SimpleLog;
import com.a710.cs6310.model.form.SystemStatus;
import com.a710.cs6310.service.AbstractSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class OsMowSisAPI {

    @Autowired
    AbstractSystemService ss;

    @PostMapping("/upload")
    public ResponseEntity<SystemStatus> uploadFile(@RequestParam("file") MultipartFile file) throws Exception{
        SimpleLog.getInstance().log("upload file: ");
        if (file == null) {
            throw new RuntimeException("You must select the a file for uploading");
        }

        InputStream inputStream = file.getInputStream();
        return new ResponseEntity<>(ss.uploadFile(inputStream), HttpStatus.OK);

    }

    @GetMapping("/load")
    public SystemStatus readFileAndInitial() {
//        SimpleLog.getInstance().log("load file: " + filename);
        return ss.readFile(null);
    }

    @GetMapping("/simulate/next")
    public SystemStatus simulateNext() {
        SimpleLog.getInstance().log("simulate next");
        return ss.getNextTurnStatus();
    }

    /*
    ** For local testing only
     */
    @GetMapping("/simulate/all")
    public SystemStatus simulateAll() {

//        for (int i = 0; i < 400; i ++) {
//            System.out.println("simulateAll:" + i);
            while (true) {
                if (ss.getNextTurnStatus().getGameState().contains(GameState.END.name())) {
                    break;
                }
            }
//        }
        return ss.getNextTurnStatus();
    }

    @GetMapping("/simulate/quit")
    public SystemStatus quitRequest() {
        SimpleLog.getInstance().log("quit simulation request");
        return ss.quitSimulation();
    }
}
