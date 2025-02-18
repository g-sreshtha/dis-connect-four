package com.thg.accelerator23.connectn.ai.sreshtha;

import com.thehutgroup.accelerator.connectn.player.*;
import com.thg.accelerator23.connectn.ai.sreshtha.analysis.BoardAnalyser;
import com.thg.accelerator23.connectn.ai.sreshtha.analysis.GameState;


public class FourChan extends Player {

  public final int MAX_VAL= Integer.MAX_VALUE;
  public final int MIN_VAL= Integer.MIN_VALUE;
  public FourChan(Counter counter) {
    super(counter, FourChan.class.getName());
  }
  // alpha is constantly 16??
  @Override
  public int makeMove(Board board) {
//    int board_width = 10;
//    int board_height = 8;
//
//    int min = 0;
//    int max = board_width - 1;
//
//    int random = (int)Math.floor(Math.random() * (max - min + 1) + min);
//    //TODO: some crazy analysis
//    //TODO: make sure said analysis uses less than 2G of heap and returns within 10 seconds on whichever machine is running it
//    return random;


    int bestScore = MIN_VAL;
    int depth = 4;
    int bestMove= -1;

    for(int i=0; i < board.getConfig().getWidth(); i++){
        try {
            Board newBoard = new Board(board, i, getCounter());
            int score = minimax(newBoard, depth, MIN_VAL, MAX_VAL, false);
            if(score > bestScore){
              bestScore = score;
              bestMove = i;
            }
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    return bestMove;
  }

  // alpha beta pruning removes the depth of the minimax algorithm, this allows it to take up less tree memory therefore faster : )
  public int minimax(Board board, int depth, long alpha, long beta, boolean maximisingPlayer) throws InvalidMoveException {
    GameState gameState = new BoardAnalyser(board.getConfig()).calculateGameState(board);

    if(depth==0 || gameState.isWin() || gameState.isEnd()){
      return evaluate(board);
    }

    if(maximisingPlayer){
      int max = MIN_VAL;
      for(int i=0; i < board.getConfig().getWidth(); i++){
        Board b = new Board(board, i, maximisingPlayer ? this.getCounter() : this.getCounter().getOther());
        int new_score = minimax(b, depth - 1, alpha, beta, false);
        max = Math.max(max, new_score);
        alpha = Math.max(alpha, max);
        if (alpha >= beta){
        break;
        }
      } return max;

    }else{
      int min = MAX_VAL;
      for(int i=0; i< board.getConfig().getWidth(); i++){
        Board bo = new Board(board, i, maximisingPlayer ? this.getCounter() : this.getCounter().getOther());
        int new_score = minimax(bo, depth-1, alpha, beta, true);
        min = Math.min(min, new_score);
        beta = Math.min(beta, min);
        if(alpha >= beta){
          break;
        }
      }return min;
    }

  }

  public int calcScorePos(Board board, int row, int col, int incrementRow, int incrementCol) {
    int maxingPoints = 0, miningPoints = 0;
    for (int i = 0; i < 4; i++) {
      if (board.getCounterAtPosition(new Position(col, row)) == Counter.X) {
        maxingPoints++;
      } else if (board.getCounterAtPosition(new Position(col, row)) == Counter.O) {
        miningPoints++;
      }
      row += incrementRow;
      col += incrementCol;
    }
    if (maxingPoints == 4) {
      return MAX_VAL;
    } else if (miningPoints == 4) {
      return MIN_VAL;
    } else {
      return maxingPoints;
    }
  }


  public int evaluate(Board board) {
    int verticalPoints = 0, horizontalPoints = 0, decsDiagPoint = 0, ascDiagPoints = 0, totalPoints = 0;
    for (int row = 0; row < board.getConfig().getHeight() - 3; row++) {
      for (int col = 0; col < board.getConfig().getWidth(); col++) {
        int tempScore = calcScorePos(board, row, col, 1, 0);
        verticalPoints += tempScore;
        if (tempScore >= MAX_VAL || tempScore <= MIN_VAL) {
          return tempScore;
        }

      }
    }
    for (int row = 0; row < board.getConfig().getHeight(); row++) {
      for (int col = 0; col < board.getConfig().getWidth() - 3; col++) {
        int tempScore = calcScorePos(board, row, col, 0, 1);
        horizontalPoints += tempScore;
        if (tempScore >= MAX_VAL || tempScore <= MIN_VAL) {
          return tempScore;
        }

      }
    }

    for(int row=0; row< board.getConfig().getHeight() - 3; row++){
      for(int col=0; col < board.getConfig().getHeight() - 3; col++){
        int tempScore = calcScorePos(board, row, col, 1, 1);
        decsDiagPoint += tempScore;
        if(tempScore>=MAX_VAL || tempScore <= MIN_VAL){
          return tempScore;
        }
      }
    }
    for(int row=3; row< board.getConfig().getHeight(); row++){
      for(int col=0; col<board.getConfig().getHeight() - 4; col++){
        int tempScore = calcScorePos(board, row, col, -1, 1);
        ascDiagPoints += tempScore;
        if(tempScore >= MAX_VAL || tempScore <= MIN_VAL){
          return tempScore;
        }
      }
    }

    totalPoints = verticalPoints + horizontalPoints + decsDiagPoint + ascDiagPoints;
    return totalPoints;

  }

}
