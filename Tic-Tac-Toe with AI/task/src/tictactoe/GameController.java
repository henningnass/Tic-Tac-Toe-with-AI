package tictactoe;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class GameController {
    private Table table;
    // possible levels: easy, medium, hard
    String[] players = new String[2];

    // gameMode = 0: user vc computer    gameMode = 1: computer vs computer
    // gameMode = 2: user vs user        gameMode = 3: computer vs user
    // gameMode = 4: end of game
    private int gameMode;

    public GameController() {
        table = new Table();
        gameMode = 0;
        players[0] = "easy";
        players[1] = "easy";
    }


    public void play() {
        gameMode = readModeFromInput();
        while (gameMode != 4) {
            table.printGameGrid();
            while (table.isNotFinished()) {
                char p = table.getNextPlayer();

                //System.out.println("Next Player: " + table.getNextPlayer());
                switch (p) {
                    case 'X':
                        switch (gameMode) {
                            case 0:
                                moveHumanPlayer();
                                break;
                            case 1:
                                moveComputerPlayer();
                                break;
                            case 2:
                                moveHumanPlayer();
                                break;
                            case 3:
                                moveComputerPlayer();
                                break;
                        }
                        break;
                    case 'O':
                        switch (gameMode) {
                            case 0:
                                moveComputerPlayer();
                                break;
                            case 1:
                                moveComputerPlayer();
                                break;
                            case 2:
                                moveHumanPlayer();
                                break;
                            case 3:
                                moveHumanPlayer();
                                break;
                        }
                        break;
                }

                printStatus();

            }
            table = new Table();
            gameMode = readModeFromInput();
        }
    }

    public int readModeFromInput() {
        Scanner scanner = new Scanner(System.in);
        String regex1 = "(start|exit)";
        String regex2 = "(easy|medium|hard|user)";
        String line;
        int mode = 0;
        boolean validInput = false;
        String[] params;
        String command;

        do {
            // user input
            System.out.print("Input command: > ");
            line = scanner.nextLine();
            params = line.split("\\s+");

            if (params.length == 1) {
                command = params[0];
                if ("exit".equals(command)) {
                    mode = 4;
                    validInput = true;
                } else if ("start".equals(command)) {
                    System.out.println("Bad parameters!");
                } else {
                    System.out.println("Unknown command!");
                }
            } else if (params.length == 2) {
                command = params[0];
                if (command.matches(regex1)) {
                    System.out.println("Bad parameters");
                } else {
                    System.out.println("Unknown command");
                }
            } else if (params.length == 3) {
                if (params[0].matches(regex1)) {
                    command = params[0];
                    if ("start".equals(command)) {
                        if (params[1].matches(regex2) && params[1].matches(regex2)) {
                            String firstPlayer = params[1];
                            String secondPlayer = params[2];
                            mode = getMode(firstPlayer, secondPlayer);
                            players[0] = firstPlayer;
                            players[1] = secondPlayer;
                            validInput = true;
                        } else {
                            System.out.println("Bad parameters!");
                        }
                    } else {
                        System.out.println("Bad parameters");
                    }
                }
            } else {
                System.out.println("Unknown command");
            }

        } while (!validInput);

        return mode;
    }


    private int getMode(String firstPlayer, String secondPlayer) {

        return (firstPlayer.matches("(easy|medium|hard)") && secondPlayer.matches("(easy|medium|hard)")) ? 1 :
                (firstPlayer.matches("(easy|medium|hard)") && secondPlayer.matches("user")) ? 3 :
                        ("user".equals(firstPlayer) && secondPlayer.matches("(easy|medium|hard)")) ? 0 : 2;
    }

    public void moveHumanPlayer() {
        int row, col;
        Scanner scanner = new Scanner(System.in);
        boolean validInput = false;
        String line;
        String[] token;
        while (!validInput) {

            // User input first, X begins
            System.out.print("Enter the coordinates: > ");
            line = scanner.nextLine();
            token = line.split("\\s+");

            // try to read coordinates
            if (token.length == 2) {
                try {
                    row = Integer.parseInt(token[0]);
                    col = Integer.parseInt(token[1]);
                } catch (Exception e) {
                    System.out.println("You should enter numbers!");
                    continue;
                }

                if (table.isOccupied(row, col)) {
                    System.out.println("This cell is occupied! Choose another one!");
                } else if (!table.isLegalPosition(row, col)) {
                    System.out.println("Coordinates should be from 1 to 3!");
                } else {
                    table.makeCheck(row, col);
                    validInput = true;
                    if (!table.isNotFinished()) {
                        break;
                    }
                }
            } else {
                System.out.println("You should enter numbers!");
            }
        }
        table.printGameGrid();
    }

    public void moveComputerPlayer() {
        if (table.getNextPlayer() == 'X') {
            System.out.println("Making move level \"" + players[0] + "\"");
        } else {
            System.out.println("Making move level \"" + players[1] + "\"");
        }
        int index = table.getNextPlayer() == 'X' ? 0 : 1;

        switch (players[index]) {
            case "easy":
                makeRandomCheck();
                break;
            case "medium":
                makeMediumCheck();
                break;
            case "hard":
                makeHardCheck();
                break;
            default:
                break;
        }
        table.printGameGrid();
    }

    private void makeHardCheck() {
        int[] bestMove;
        bestMove = findBestMove(table.getGameGrid(), table.getNextPlayer());

        int row = bestMove[0];
        int col = bestMove[1];
        table.makeCheck(row, col);
    }

    private void makeMediumCheck() {
        int[] winningPosition;
        int[] riskyPosition;
        winningPosition = getWinningPosition();
        riskyPosition = getRiskyPosition();
        int row;
        int col;


        if (winningPosition != null) {
            row = winningPosition[0];
            col = winningPosition[1];
            //System.out.println("Winning positions: " + row + col);
            table.makeCheck(row, col);
        } else if (riskyPosition != null) {
            row = riskyPosition[0];
            col = riskyPosition[1];
            //System.out.println("Winning positions: " + row + col);
            table.makeCheck(row, col);
        } else {
            makeRandomCheck();
        }
    }



    private int[] getWinningPosition() {

        char nextPlayer = table.getNextPlayer();

        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 3; col++) {
                if (table.isLegalPosition(row, col) && !table.isOccupied(row, col)) {
                    table.makeCheck(row, col);
                    if (table.wins(nextPlayer)) {
                        int[] position = new int[2];
                        position[0] = row;
                        position[1] = col;
                        table.remakeCheck(row, col);
                        return position;
                    }
                    table.remakeCheck(row, col);
                }
            }
        }
        return null;
    }

    private int[] getRiskyPosition() {
        table.changePlayer();

        int[] position = getWinningPosition();
        table.changePlayer();
        return position;

    }

    private void makeRandomCheck() {
        int row, col;

        Date date = new Date();
        long millis;
        millis = date.getTime();
        Random random = new Random(millis);

        do {
            row = random.nextInt(3) + 1;
            col = random.nextInt(3) + 1;
        } while (!table.isLegalPosition(row, col) || table.isOccupied(row, col));
        table.makeCheck(row, col);
    }

    private void printStatus() {
        if (table.wins('X')) {
            System.out.println("X wins");
        }
        if (table.wins('O')) {
            System.out.println("O wins");
        }
        if (table.isDraw()) {
            System.out.println("Draw");
        }
    }

    public boolean isMovesLeft(char[][] board) {
        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 3; col++) {
                if (board[row - 1][col - 1] == '_') {
                    return true;
                }
            }
        }
        return false;
    }


    public int evaluate(char b[][], char player) {
        char opponent = player == 'X' ? 'O' : 'X';
        // Checking for Rows for X or O victory.
        for (int row = 1; row <= 3; row++) {
            if (b[row - 1][0] == b[row - 1][1] && b[row - 1][1] == b[row - 1][2]) {
                if (b[row - 1][0] == player)
                    return +10;
                else if (b[row - 1][0] == opponent)
                    return -10;
            }
        }

        // Checking for Columns for X or O victory.
        for (int col = 1; col <= 3; col++) {
            if (b[0][col - 1] == b[1][col - 1] && b[1][col - 1] == b[2][col - 1]) {
                if (b[0][col - 1] == player)
                    return +10;
                else if (b[0][col - 1] == opponent)
                    return -10;
            }
        }

        // Checking for Diagonals for X or O victory.
        if (b[0][0] == b[1][1] && b[1][1] == b[2][2]) {
            if (b[0][0] == player)
                return +10;
            else if (b[0][0] == opponent)
                return -10;
        }
        if (b[0][2] == b[1][1] && b[1][1] == b[2][0]) {
            if (b[0][2] == player)
                return +10;
            else if (b[0][2] == opponent)
                return -10;
        }

        // Else if none of them have won then return 0
        return 0;
    }



    // This is the minimax function. It considers all
    // the possible ways the game can go and returns
    // the value of the board
    public int minimax(char board[][], int depth, boolean isMax, char player)
    {
        char opponent = player == 'X' ? 'O' : 'X';
        int score = evaluate(board, player);

        // If Maximizer has won the game
        // return his/her evaluated score
        if (score == 10)
            return score;

        // If Minimizer has won the game
        // return his/her evaluated score
        if (score == -10)
            return score;

        // If there are no more moves and
        // no winner then it is a tie
        if (!isMovesLeft(board))
            return 0;

        // If this is maximizer's move
        if (isMax)
        {
            int best = -1000;

            // Traverse all cells
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    // Check if cell is empty
                    if (board[i][j] == '_') {
                        // Make the move
                        board[i][j] = player;

                        // Call minimax recursively and choose
                        // the maximum value
                        best = Math.max(best, minimax(board, depth + 1, !isMax, player));

                        // Undo the move
                        board[i][j] = '_';
                    }
                }
            }
            return best;
        }

        // If this minimizer's move
        else {
            int best = 1000;

            // Traverse all cells
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    // Check if cell is empty
                    if (board[i][j] == '_') {
                        // Make the move
                        board[i][j] = opponent;

                        // Call minimax recursively and choose
                        // the minimum value
                        best = Math.min(best, minimax(board, depth + 1, !isMax, player));

                        // Undo the move
                        board[i][j] = '_';
                    }
                }
            }
            return best;
        }
    }

    // This will return the best possible
    // move for the player
    public int[] findBestMove(char board[][], char player)
    {
        char opponent = player == 'X' ? 'O' : 'X';
        int bestVal = -1000;
        int[] bestMove = new int[2];
        bestMove[0] = -1;
        bestMove[1] = -1;

        // Traverse all cells, evaluate minimax function
        // for all empty cells. And return the cell
        // with optimal value.
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                // Check if cell is empty
                if (board[i][j] == '_') {
                    // Make the move
                    board[i][j] = player;

                    // compute evaluation function for this
                    // move.
                    int moveVal = minimax(board, 0, false, player);
                    //System.out.println(moveVal);

                    // Undo the move
                    board[i][j] = '_';

                    // If the value of the current move is
                    // more than the best value, then update
                    // best/
                    if (moveVal > bestVal)
                    {
                        bestMove[0] = i + 1;  // recalculate row from index
                        bestMove[1] = j + 1;  // recalculate col from index
                        bestVal = moveVal;
                    }
                }
            }
        }
        return bestMove;
    }

}

