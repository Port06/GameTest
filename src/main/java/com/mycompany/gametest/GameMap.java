package com.mycompany.gametest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Board getBoardByPortalLinkId(int entryPortalId, int exitPortalId) {
        return boards.values().stream().filter(board -> {
            for (int x = 0; x < board.getWidth(); x++) {
                for (int y = 0; y < board.getHeight(); y++) {
                    Cell cell = board.getCell(x, y, 3);
                    if (cell != null && cell.getEntryPortalId() == entryPortalId && cell.getExitPortalId() == exitPortalId) {
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

    public String getBoardContainingPlayer(Player player) {
        for (String boardName : getBoardNames()) {
            if (isPlayerOnBoard(player, boardName)) {
                return boardName;
            }
        }
        return null; // Or handle this case appropriately
    }
    
     private boolean isPlayerOnBoard(Player player, String boardName) {
        Board board = getBoard(boardName);
        if (board == null) {
            return false;
        }
        return board.containsPlayer(player);
    }
    
    // Method to retrieve the map of boards
    public Map<String, Board> getBoards() {
        return boards;
    }

    // Method to retrieve the names of all boards
    public List<String> getBoardNames() {
        return boards.keySet().stream().collect(Collectors.toList());
    }
}