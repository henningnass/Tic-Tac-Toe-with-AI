 package tictactoe;


public class Table {
    private char[][] gamegrid = new char[3][3];
    private int nrOccupiedCells = 0;
    private char nextPlayer = 'X';


    public char[][] getGameGrid() {
        return gamegrid.clone();
    }

    public void changePlayer() {
        if (nextPlayer == 'X') {
            nextPlayer = 'O';
        } else {
            nextPlayer = 'X';
        }
    }

    public Table(String initialSetting) {
        String regex = "(x|X|o|O|_){9}";
        int row, col;
        char c;
        if (initialSetting.length() == 9 && initialSetting.matches(regex)) {
            for (int count = 0; count < 9; count++) {
                c = initialSetting.charAt(count);
                row = count / 3 + 1;
                col = count % 3 + 1;
                makeCheck(row, col, c);
            }
            // wrong initial setting
        } else {
            clearAll();
        }
    }

    public Table() {
        clearAll();
    }

    public boolean isOccupied(int row, int col) {
        if (isLegalPosition(row, col)) {
            return gamegrid[row - 1][col - 1] == 'O' || gamegrid[row - 1][col - 1] == 'X';
        } else {
            return false;
        }
    }

    public boolean isLegalPosition(int row, int col) {
        return (1 <= row) && (row <= 3) && (1 <= col) && (col <= 3);
    }

    private boolean makeCheck(int row, int col, char c) {
        if (c == 'x') { c = 'X';}
        if (c == 'o') { c = 'O';}
        if (isLegalPosition(row, col) && !isOccupied(row, col) && (c == 'X' || c == 'O' || c == '_')) {
            gamegrid[row - 1][col - 1] = c;
            if (c == 'O' || c == 'X') {
                nrOccupiedCells++;
                changePlayer();
            }
            return true;
        }
        return false;
    }

    public void remakeCheck(int row, int col) {
        if (isLegalPosition(row, col)) {
            gamegrid[row - 1][col - 1] = '_';
            nrOccupiedCells--;
            changePlayer();
        }
    }

    public boolean makeCheck(int row, int col) {
        return makeCheck(row, col, nextPlayer);
    }


    private void clearAll() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gamegrid[i][j] = '_';
            }
        }
        nrOccupiedCells = 0;
    }

    private char getMark(int row, int col) {
        return gamegrid[row - 1][col - 1];
    }

    private char getOutput(int row, int col) {
        if (isLegalPosition(row, col)) {
            char c = getMark(row, col);
            if (c != '_') {
                return c;
            } else {
                return ' ';
            }
        }
        return ' ';
    }

    public void printGameGrid() {
        String upperRule = "---------";
        String lowerRule = upperRule;
        String line;
        System.out.println(upperRule);
        for (int row = 1; row <= 3; row++) {
            line = "| " + getOutput(row, 1) + " " + getOutput(row, 2) + " ";
            line = line +  getOutput(row, 3) +  " |";
            System.out.println(line);
        }
        System.out.println(lowerRule);
    }

    public boolean isNotFinished() {
        return !wins('X') && !wins('O') && (nrOccupiedCells <= 8);
    }

    public boolean isDraw() {
        return !wins('X') && !wins('O') && (nrOccupiedCells == 9);
    }

    public char getNextPlayer() {
        return nextPlayer;
    }

    public boolean wins(char c) {
        if (c == 'x') { c = 'X';}
        if (c == 'o') { c = 'O';}

        if (c == 'O' ||  c == 'X'){
            boolean firstRowComplete = c == gamegrid[0][0] && c == gamegrid[0][1] && c == gamegrid[0][2];
            boolean secondRowComplete = c == gamegrid[1][0] && c == gamegrid[1][1] && c == gamegrid[1][2];
            boolean thirdRowComplete = c == gamegrid[2][0] && c == gamegrid[2][1] && c == gamegrid[2][2];
            boolean firstColComplete = c == gamegrid[0][0] && c == gamegrid[1][0] && c == gamegrid[2][0];
            boolean secondColComplete = c == gamegrid[0][1] && c == gamegrid[1][1] && c == gamegrid[2][1];
            boolean thirdColComplete = c == gamegrid[0][2] && c == gamegrid[1][2] && c == gamegrid[2][2];
            boolean firstDiagComplete = c == gamegrid[0][0] && c == gamegrid[1][1] && c == gamegrid[2][2];
            boolean secondDiagComplete = c == gamegrid[0][2] && c == gamegrid[1][1] && c == gamegrid[2][0];

            boolean wins = firstRowComplete || secondRowComplete || thirdRowComplete;
            wins = wins || firstColComplete || secondColComplete || thirdColComplete;
            wins = wins || firstDiagComplete || secondDiagComplete;
            return wins;
        } else{
            return false;
        }
    }
}
