package com.mycompany.gametest;

import java.util.HashMap;
import java.util.Map;

public class GameMap {
    private Map<String, Board> boards;
    private String currentBoardId;

    public GameMap() {
        boards = new HashMap<>();
    }

    public void addBoard(String id, Board board) {
        boards.put(id, board);
    }

    public Board getBoard(String id) {
        return boards.get(id);
    }

    public void setCurrentBoard(String id) {
        if (boards.containsKey(id)) {
            currentBoardId = id;
        }
    }

    public Board getCurrentBoard() {
        return boards.get(currentBoardId);
    }
}
