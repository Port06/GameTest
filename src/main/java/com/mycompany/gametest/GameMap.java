package com.mycompany.gametest;

import javax.swing.JPanel;
import java.util.HashMap;
import java.util.Map;

public class GameMap {
    private Map<String, Board> boards;
    private Board currentBoard;

    public GameMap() {
        boards = new HashMap<>();
    }

    public void addBoard(String name, Board board) {
        boards.put(name, board);
    }

    public Board getBoard(String name) {
        return boards.get(name);
    }

    public Board getBoardByPortalLinkId(int portalLinkId) {
        return boards.values().stream().filter(board -> {
            for (int x = 0; x < board.getWidth(); x++) {
                for (int y = 0; y < board.getHeight(); y++) {
                    Cell cell = board.getCell(x, y, true);
                    if (cell != null && cell.getPortalLinkId() == portalLinkId) {
                        return true;
                    }
                }
            }
            return false;
        }).findFirst().orElse(null);
    }

    public void setCurrentBoard(String name) {
        currentBoard = boards.get(name);
    }

    public Board getCurrentBoard() {
        return currentBoard;
    }

    public void addPortalLink(int portalLinkId, String targetBoardName) {
        Board current = getCurrentBoard();
        Board target = getBoard(targetBoardName);

        if (current != null && target != null) {
            current.setPortalLink(portalLinkId, target);
        } else {
            System.out.println("Error: Board or target board not found.");
        }
    }
}