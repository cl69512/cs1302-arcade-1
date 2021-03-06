package cs1302.arcade.gameTetris;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.control.Button;
import java.util.Arrays;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.event.*;
import cs1302.arcade.gameTetris.shapes.*;
import cs1302.arcade.ArcadeApp;

/**
 * This class represents the Tetris game.           
 */
public class GameTetris{
    private ArcadeApp app;
    private Scene scene;
    private GridPane grid = new GridPane();
    private Text score = new Text();
    private Text level = new Text();
    private Timeline tl = new Timeline();
    private Shape currShape;
    private boolean gameOver = false;
    private int points = 0;

    /**
     * Creates the Tetris game scene.
     * @param a a reference to the original {@code ArcadeApp}
     * @return the scene for Tetris
     */
    public Scene getGameScene(ArcadeApp a) {
        app = a;
        updateScore();
        score.setFont(new Font(20));
        level.setFont(new Font(20));
        HBox scores = new HBox(score, level);
        scores.setSpacing(30);

        //Creates and sets behavior of buttons
        Button b = new Button("New Game") {
                public void requestFocus() { } //Prevents b from taking focus
            };
        b.setOnAction(e -> newGame());
        Button b2 = new Button("Back to Games List") {
                public void requestFocus() { } //Prevents b2 from taking focus
            };
        b2.setOnAction(e -> mainMenu());
        HBox buttons = new HBox(b, b2);

        VBox vbox = new VBox(scores, buttons, grid);
        scene = new Scene(vbox);
        grid.requestFocus();

        newGame();        
        grid.setOnKeyPressed(createKeyHandler());

        return scene;        
    }

    /**
     * Adds points based on the number of lines cleared and updates {@code score} 
     * and {@code level}.
     * @param lines the number of lines cleared
     */
    private void addPoints(int lines) {
         if(lines == 1)
         {
             points += 40;
         }
         else if(lines == 2)
         {
             points += 100;
         }
         else if(lines==3)
         {
             points += 300;
         }
         else if(lines == 4)
         {
             points +=1200;
         }
         updateScore();
         updateLevel();
    }

    /**
     * Updates the difficulty level of the game based on the value of {@code level}.
     */
    private void updateLevel() {
        if(points >= 200) {
            setTimeline(3);
            tl.play();
            level.setText("Level: 3");
        } else if (points >= 80) {
            setTimeline(2);
            tl.play();
            level.setText("Level: 2");
        }
    }


    /**
     * Updates the {@code score}.
     */
    private void updateScore() {
        String text = "Score: " + points;
        score.setText(text);
    }
    
    /**
     * Clears the board of all rectangles, resets {@code score} and {@code level} and 
     * restarts the game.
     */
    private void newGame(){
        for(int row = 0; row<20; row++)
        {
            for(int col = 0; col<10; col++)
            {
                for(int i = 0; i < 2; i++) { //Clears 2x for good measure
                    if(getFromGrid(col, row) != null)
                    {
                        grid.getChildren().remove(getFromGrid(col, row)); //removing existing shapes
                    }
                }
            }
        }
        newGrid();
        gameOver = false;
        points = 0;
        updateScore();
        level.setText("Level: 1");
        newShape();
        setTimeline(1);
        tl.play();
    }

    /**
     * Creates a new formatted grid.
     */
    private void newGrid(){
        grid.setPrefSize(300, 600);
        grid.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        
        //adds cols and rows to grid
        final int numCols = 10;
        final int numRows = 20;
        for (int i = 0; i < numCols; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / numCols);  
            grid.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / numRows);
            grid.getRowConstraints().add(rowConst);         
        }
        grid.setGridLinesVisible(true);
    }

    /**
     * Creates a new shape randomly.
     */
    private void newShape() {
        String[] shapes = {"Square", "L", "J", "S", "Z", "I", "T"};
        String shape = shapes[(int)(Math.random() * 7)];//randomnly picks shape
        switch(shape) {
        case "Square":
            currShape = new Square(grid);
            break;
        case "L":
            currShape = new LShape(grid);
            break;
        case "T":
            currShape = new TShape(grid);
            break;
        case "J":
            currShape = new JShape(grid);
            break;
        case "Z":
            currShape = new ZShape(grid);
            break;
        case "S":
            currShape = new SShape(grid);
            break;
        case "I":
            currShape = new IShape(grid);
            break;
        default:
            currShape = new Square(grid);
            break;
        }       
    }

    /**
     * Creates and returns a KeyEvent EventHandler that plays a timeline depending on what
     * arrow key the user presses.
     * @return an EventHandler that handles KeyEvents
     */
    private EventHandler<? super KeyEvent> createKeyHandler() {
        return e -> {
            if(gameOver == false){
                if (e.getCode() == KeyCode.RIGHT) {
                    currShape.moveRight();
                } else if (e.getCode() == KeyCode.LEFT) {
                    currShape.moveLeft();
                } else if (e.getCode() == KeyCode.UP) {
                    currShape.rotate();
                } else if (e.getCode() == KeyCode.DOWN) {
                    currShape.moveToBottom();
                } //if
            } //if
        }; //return
    } //createKeyHandler
    
    /**
     * Sets the correct timeline according to the specified level.
     * @param level the specified level of difficulty, can be 1, 2, or 3
     */
    private void setTimeline(int level) {
        tl.stop();
        EventHandler<ActionEvent> handler = e -> {
            if(currShape.moveDown() == false) {
                clearLines();
                checkGameOver();
                if(gameOver == false) {
                    newShape();
                } //if
            } //if
        };        
        KeyFrame k;
        switch(level) {
        case 1:
            k = new KeyFrame(Duration.millis(800), handler);
            break;
        case 2:
            k = new KeyFrame(Duration.millis(500), handler);
            break;
        case 3:
            k = new KeyFrame(Duration.millis(200), handler);
            break;
        default:
            k = new KeyFrame(Duration.millis(1000), handler);
        }
        tl.getKeyFrames().clear();
        tl.getKeyFrames().add(k);
        tl.setCycleCount(Timeline.INDEFINITE);
    } //setTimeline

    /**
     * Checks to see if a shape stopped on the top row. Ends the game and informs the
     * user if so.
     */
    private void checkGameOver() {
        for(int col = 0; col < 10; col++) {
            if(getFromGrid(col, 0) != null) {
                tl.stop();
                gameOver = true;
                Alert alert = new Alert(AlertType.INFORMATION,
                                        "GAME OVER. Press New Game to restart.\n"  +
                                        "Final score: " + points);
                Runnable r=() -> alert.showAndWait().filter(response->response==ButtonType.OK);
                Platform.runLater(r);
                return;
            }
        }
    }

    
    /**
     * Clears any full rows and brings down the rows above.
     */
    private void clearLines() {
        int rowsCleared = 0;
        for(int y = 0; y < 20; y++) {
            boolean isFull = true;
            for (int x = 0; x < 10; x++) {
                if(getFromGrid(x, y) == null) {
                    isFull = false;
                } //if
            } //for
            if(isFull) {
                rowsCleared++; //increases rows cleared
                for (int x = 0; x < 10; x++) {
                    Rectangle rect = getFromGrid(x, y);
                    grid.getChildren().remove(rect);
                    for (int k = y; k > 0; k--) {
                        Rectangle top = getFromGrid(x, k - 1);
                        if(top != null) {
                            GridPane.setRowIndex(top, k);
                        }
                    }
                }
            }
        }
        addPoints(rowsCleared);
    }
    
    /** Changes the scene to that of the main menu. */
    private void mainMenu() {
        app.stage.setScene(app.mainMenu());
    } //mainMenu

    /**
     * Returns the {@code Rectangle} object at the specified column and row.
     * @param col the specified column
     * @param row the specified row
     * @return the {@code Rectangle} at the specified column and row, or null if none exists
     */
    public Rectangle getFromGrid(int col, int row) {
        for (Node node : grid.getChildren()) {
            if(node != null && GridPane.getColumnIndex(node)!= null
               && GridPane.getRowIndex(node) != null) {
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                    return (Rectangle)node;
                }
            }
        }
        return null;
    }   
}
