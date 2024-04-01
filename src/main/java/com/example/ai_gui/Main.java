package com.example.ai_gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

public class Main extends Application {
    static final char EMPTY = ' ', BLACK = '\u25A1', WHITE = '\u25A0';
    static final int MAX_DEPTH = 7;
    static final int SHALLOW_DEPTH = 2;

    static char[][] board = new char[8][8];
    static char aiPlayer = WHITE, humanPlayer = BLACK;
    private final Button[][] buttons = new Button[8][8];

    char player;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the board with initial colors
        initializeBoard();
        player = BLACK;
        welcomePage();

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setStyle("-fx-background-color: #6b7996;");

        // Create buttons and add them to the grid pane
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Button button = createButton(row, col);

                if (col != 0 && col != 7)
                    button.setDisable(true);

                button.setStyle("-fx-base: grey;");
                gridPane.add(button, col, row);
                buttons[row][col] = button;
            }
        }

        Scene scene = new Scene(gridPane);
        primaryStage.setTitle("Board Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        if (aiPlayer == BLACK)
            computerTurn();
    }

    private Button createButton(int row, int col) {
        Button button = new Button();
        button.setPrefSize(50, 50);

        button.setOnAction(event -> {
            if (board[row][col] == EMPTY) {
                makeMove(player, row, col);
                updateButtonColor(button, humanPlayer);

                // enable next buttons
                if (col + 1 < 8)
                    buttons[row][col + 1].setDisable(false);
                if (col - 1 > 0)
                    buttons[row][col - 1].setDisable(false);

                if (checkWinner(row, col, humanPlayer, true)) {
                    System.out.println("The Winner is: " + player + " Congratulations!!");
                    button.setStyle(button.getStyle() + "-fx-border-color: green;");
                    button.setDisable(false);
                } else if (gameTie()) {
                    System.out.println("Game Over, its a tie");
                }
                player = switchColor(player);
                computerTurn();
            }
        });

        return button;
    }

    private void updateButtonColor(Button button, char color) {
        if (color == BLACK) {
            button.setStyle("-fx-base: black;");
        } else {
            button.setStyle("-fx-base: white;");
        }
    }

    // main menu
    public void welcomePage() {
        Stage stage = new Stage();
        BorderPane pane = new BorderPane();
        VBox box = new VBox(20);

        Label select = new Label("Select Your Color:");

        RadioButton option_1 = new RadioButton("1. Black, (NPC is white)");
        RadioButton option_2 = new RadioButton("2. White, (NPC is black)");
        ToggleGroup group = new ToggleGroup();

        Button button = new Button("Start Game!");
        button.setFont(new Font(17));

        option_1.setToggleGroup(group);
        option_2.setToggleGroup(group);

        select.setFont((new Font(15)));
        option_1.setFont(new Font(18));
        option_2.setFont(new Font(18));
        // Add a listener to the selectedProperty of the radio buttons
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RadioButton selectedRadioButton = (RadioButton) newValue;
                if (selectedRadioButton == option_1) {
                    System.out.println("First radio button selected");
                    aiPlayer = WHITE;
                    humanPlayer = BLACK;
                } else if (selectedRadioButton == option_2) {
                    System.out.println("Second radio button selected");
                    aiPlayer = BLACK;
                    humanPlayer = WHITE;
                }
            }
        });

        button.setOnAction(e -> stage.close());

        box.getChildren().addAll(select, option_1, option_2);
        pane.setCenter(box);
        pane.setBottom(button);
        BorderPane.setAlignment(button, Pos.CENTER);

        Scene scene = new Scene(pane, 555, 150);
        stage.setTitle("Menu!!");
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void colorWinChain(ArrayList<int[]> chain) {
        for (int i = 0; i < 8; i++) {
            for (Button button : buttons[i])
                button.setDisable(true);
        }
        for (int[] pair : chain) {
            buttons[pair[0]][pair[1]].setDisable(false);
            buttons[pair[0]][pair[1]].setStyle(buttons[pair[0]][pair[1]].getStyle() + "-fx-border-color: green;");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void initializeBoard() {
        for (char[] chars : board)
            Arrays.fill(chars, EMPTY);
    }

    public static boolean validMove(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7)
            return false;

        if (board[row][col] != EMPTY)
            return false;

        if (col != 7 && col != 0)
            return board[row][col + 1] != EMPTY || board[row][col - 1] != EMPTY;

        return true;
    }

    public static void makeMove(char player, int row, int col) {
        if (board[row][col] == EMPTY)
            board[row][col] = player;
    }

    public static void undoMove(int row, int col) {
        board[row][col] = EMPTY;
    }

    public boolean checkWinner(int row, int col, char player, boolean currentState) {
        int count = 0;
        ArrayList<int[]> winningChain = new ArrayList<>();

        // check horizontal.
        for (int i = -1; i >= -4; i--) {
            if (col + i < 8 && col + i >= 0) {
                if (board[row][col + i] == player) {
                    count++;
                    winningChain.add(new int[]{row, (col + i)});
                } else
                    break;
            }
        }
        for (int i = 1; i <= 4; i++) {
            if (col + i < 8 && col + i >= 0) {
                if (board[row][col + i] == player) {
                    count++;
                    winningChain.add(new int[] {row, col + i});
                } else
                    break;
            }
        }
        if (count >= 4) {
            if (currentState)
                colorWinChain(winningChain);
            return true;
        }
        winningChain.clear();

        // check vertically
        count = 0;
        for (int i = -1; i >= -4; i--) {
            if (row + i < 8 && row + i >= 0) {
                if (board[row + i][col] == player) {
                    count++;
                    winningChain.add(new int[] {row + i, col});
                } else
                    break;
            }
        }
        for (int i = 1; i <= 4; i++) {
            if (row + i < 8 && row + i >= 0) {
                if (board[row + i][col] == player) {
                    count++;
                    winningChain.add(new int[] {row + i, col});
                } else
                    break;
            }
        }
        if (count >= 4) {
            if (currentState)
                colorWinChain(winningChain);
            return true;
        }
        winningChain.clear();

        // check diagonal
        count = 0;
        for (int i = -1; i >= -4; i--) {
            if (row + i < 8 && row + i >= 0 && col + i < 8 && col + i >= 0)
                if (board[row + i][col + i] == player) {
                    count++;
                    winningChain.add(new int[] {row + i, col + i});
                } else
                    break;
        }
        for (int i = 1; i <= 4; i++) {
            if (row + i < 8 && row + i >= 0 && col + i < 8 && col + i >= 0)
                if (board[row + i][col + i] == player) {
                    count++;
                    winningChain.add(new int[] {row + i, col + i});
                } else
                    break;
        }
        if (count >= 4) {
            if (currentState)
                colorWinChain(winningChain);
            return true;
        }
        winningChain.clear();

        // check 2nd diagonal
        count = 0;
        for (int i = -1; i >= -4; i--) {
            if (row + i < 8 && row + i >= 0 && col - i < 8 && col - i >= 0)
                if (board[row + i][col - i] == player) {
                    count++;
                    winningChain.add(new int[] {row + i, col - i});
                } else
                    break;
        }
        for (int i = 1; i <= 4; i++) {
            if (row + i < 8 && row + i >= 0 && col - i < 8 && col - i >= 0)
                if (board[row + i][col - i] == player) {
                    count++;
                    winningChain.add(new int[] {row + i, col - i});
                } else
                    break;
        }
        if (count >= 4) {
            if (currentState)
                colorWinChain(winningChain);
            return true;
        }
        return false;
    }

    public static boolean gameTie() {
        for (char[] row : board) {
            for (char c : row)
                if (c == EMPTY)
                    return false;
        }
        return true;
    }

    public static int evaluate() {
        int score = 0, count = 0, stackBonus = 0;
        int enemyCount = 0, enemyStackBonus = 0;
        int emptyCount = 0;

        // check horizontal.
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == aiPlayer) {
                    score++;
                    stackBonus += count * count;
                    count++;

                    if (enemyCount != 0)
                        emptyCount = 0;

                    // reset counters and stack bonus for opponent.
                    enemyStackBonus = 0;
                    enemyCount = 0;
                } else if (board[i][j] == humanPlayer) {
                    score--;
                    enemyStackBonus -= enemyCount * enemyCount;
                    enemyCount++;

                    if (count != 0)
                        emptyCount = 0;

                    // reset the count and stack bonus for player.
                    count = 0;
                    stackBonus = 0;
                } else if (board[i][j] == EMPTY) {
                    emptyCount++;

                    if (emptyCount + count >= 5) {
                        score += stackBonus;
                        stackBonus = 0;
                    } else if (emptyCount + enemyCount >= 5) {
                        score += enemyStackBonus;
                        enemyStackBonus = 0;
                    }
                }
            }
            count = 0;
            enemyCount = 0;
            stackBonus = 0;
            enemyStackBonus = 0;
            emptyCount = 0;
        }
        // check vertically.
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[j][i] == aiPlayer) {
                    score++;
                    stackBonus += count * count;
                    count++;

                    if (enemyCount != 0)
                        emptyCount = 0;

                    // reset counters and stack bonus for opponent.
                    enemyStackBonus = 0;
                    enemyCount = 0;
                } else if (board[j][i] == humanPlayer) {
                    score--;
                    enemyStackBonus -= enemyCount * enemyCount;
                    enemyCount++;

                    if (count != 0)
                        emptyCount = 0;

                    // reset the count and stack bonus for player.
                    count = 0;
                    stackBonus = 0;
                } else if (board[j][i] == EMPTY) {
                    emptyCount++;

                    if (emptyCount + count >= 5) {
                        score += stackBonus;
                        stackBonus = 0;
                    } else if (emptyCount + enemyCount >= 5) {
                        score += enemyStackBonus;
                        enemyStackBonus = 0;
                    }
                }
            }
            count = 0;
            enemyCount = 0;
            stackBonus = 0;
            enemyStackBonus = 0;
            emptyCount = 0;
        }
        // check diagonal (top-left to bottom-right).
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8 - i; j++) {
                if (board[i + j][j] == aiPlayer) {
                    score++;
                    stackBonus += count * count;
                    count++;

                    if (enemyCount != 0)
                        emptyCount = 0;

                    // reset counters and stack bonus for opponent.
                    enemyStackBonus = 0;
                    enemyCount = 0;
                } else if (board[i + j][j] == humanPlayer) {
                    score--;
                    enemyStackBonus -= enemyCount * enemyCount;
                    enemyCount++;

                    if (count != 0)
                        emptyCount = 0;

                    // reset the count and stack bonus for player.
                    count = 0;
                    stackBonus = 0;
                } else if (board[i + j][j] == EMPTY) {
                    emptyCount++;

                    if (emptyCount + count >= 5) {
                        score += stackBonus;
                        stackBonus = 0;
                    } else if (emptyCount + enemyCount >= 5) {
                        score += enemyStackBonus;
                        enemyStackBonus = 0;
                    }
                }
            }
            count = 0;
            enemyCount = 0;
            stackBonus = 0;
            enemyStackBonus = 0;
            emptyCount = 0;
        }
        // check diagonal (top-right to bottom-left).
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j <= i; j++) {
                if (board[i - j][j] == aiPlayer) {
                    score++;
                    stackBonus += count * count;
                    count++;

                    if (enemyCount != 0)
                        emptyCount = 0;

                    // reset counters and stack bonus for opponent.
                    enemyStackBonus = 0;
                    enemyCount = 0;
                } else if (board[i - j][j] == humanPlayer) {
                    score--;
                    enemyStackBonus -= enemyCount * enemyCount;
                    enemyCount++;

                    if (count != 0)
                        emptyCount = 0;

                    // reset the count and stack bonus for player.
                    count = 0;
                    stackBonus = 0;
                } else if (board[i - j][j] == EMPTY) {
                    emptyCount++;

                    if (emptyCount + count >= 5) {
                        score += stackBonus;
                        stackBonus = 0;
                    } else if (emptyCount + enemyCount >= 5) {
                        score += enemyStackBonus;
                        enemyStackBonus = 0;
                    }
                }
            }
            count = 0;
            enemyCount = 0;
            stackBonus = 0;
            enemyStackBonus = 0;
            emptyCount = 0;
        }

        return score;
    }

    public void computerTurn() {
        int row, col;
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
        int[] shallowBestMove = new int[2];
        int[] deepBestMove = new int[2];

        double start = System.currentTimeMillis();
        // search at a shallow depth
        int shallowScore = minimax(
                true, true,
                0,
                aiPlayer,
                0, 0,
                alpha, beta,
                shallowBestMove
        );
        System.out.println("minimax: " + shallowScore);

        // search at a higher depth.
        int deepScore = minimax(false, true,
                0,
                aiPlayer,
                0, 0,
                alpha, beta,
                deepBestMove
        );
        System.out.println("minimax: " + deepScore);

        double end = System.currentTimeMillis();

        if (shallowScore >= deepScore) {
            System.out.println("bestScore: " + shallowScore);
            row = shallowBestMove[0];
            col = shallowBestMove[1];
            System.out.println(row + " " + col);
        } else {
            System.out.println("bestScore: " + deepScore);
            row = deepBestMove[0];
            col = deepBestMove[1];
            System.out.println(row + " " + col);
        }

        makeMove(aiPlayer, row, col);
        updateButtonColor(buttons[row][col], aiPlayer);
        System.out.println("time: " + (end - start) + "ms");

        // enable next buttons
        if (col + 1 < 8)
            buttons[row][col + 1].setDisable(false);
        if (col - 1 > 0)
            buttons[row][col - 1].setDisable(false);

        if (checkWinner(row, col, aiPlayer, true)) {
            System.out.println("The Winner is: " + player + " Congratulations!!");
            buttons[row][col].setStyle(buttons[row][col].getStyle() + "-fx-border-color: green;");
            buttons[row][col].setDisable(false);

        } else if (gameTie()) {
            System.out.println("Game Over, its a tie");
        }
        this.player = switchColor(this.player);
    }

    public int minimax(boolean shallowSearch, boolean isMaxPlayer, int depth,
                       char player, int row, int col, int alpha, int beta, int[] bestMove)
    {
        if (depth != 0 && checkWinner(row, col, player, false)) {
            if (player == aiPlayer)
                return 1000;
            else if (player == humanPlayer)
                return -1000;
        }
        if (gameTie())
            return 0;

        if (depth == MAX_DEPTH || (shallowSearch && depth == SHALLOW_DEPTH))
            return evaluate();

        // if max player, then the children are minimizing.
        if (isMaxPlayer) {
            int max = Integer.MIN_VALUE;
            player = aiPlayer;

            for (int i = 8; i >= 0; i--) {
                boolean cutoffOccurred = false;

                for (int j = 0; j < 8; j++) {
                    if (validMove(i, j)) {
                        makeMove(player, i, j);
                        int score = minimax(shallowSearch, false, depth+1, player, i, j, alpha, beta, bestMove);
                        undoMove(i, j);

                        if (score > max) {
                            max = score;
                            if (depth == 0) {
                                bestMove[0] = i; // Update the best move row
                                bestMove[1] = j; // Update the best move column
                            }
                        }
                        alpha = Math.max(alpha, score);

                        if (beta <= alpha || score == 1000) {
                            cutoffOccurred = true;
                            break; // Beta cutoff
                        }
                    }
                }
                if (cutoffOccurred)
                    break;
            }
            return max;
        } else { // if the player is minimizing, then children are maximizing.
            int min = Integer.MAX_VALUE;
            player = humanPlayer;

            for (int i = 0; i < 8; i++) {
                boolean cutoffOccurred = false;

                for (int j = 0; j < 8; j++) {
                    if (validMove(i, j)) {
                        makeMove(player, i, j);
                        int score = minimax(shallowSearch, true, depth+1, player, i, j, alpha, beta, bestMove);
                        undoMove(i, j);

                        min = Math.min(min, score);
                        beta = Math.min(beta, score);

                        if (beta <= alpha || score == -1000) {
                            cutoffOccurred = true;
                            break; // Alpha cutoff
                        }
                    }
                }
                if (cutoffOccurred)
                    break;
            }
            return min;
        }
    }

    public static char switchColor(char player) {
        return (player == BLACK)? WHITE : BLACK;
    }

} // end class