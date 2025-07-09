package trivia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

// REFACTOR ME
public class Game implements IGame {
   private static final int SIZE = 6;

   ArrayList<String> players = new ArrayList<String>();
   private final int[] places = new int[SIZE];
   private final int[] purses = new int[SIZE];
   private final boolean[] inPenaltyBox = new boolean[SIZE];

   Map<String, LinkedList<String>> questionMap = new HashMap<>();
   int currentPlayer = 0;
   boolean isGettingOutOfPenaltyBox;


   public Game() {
      String[] categories = { "Pop", "Science", "Sports", "Rock" };
      for (String category : categories) {
         LinkedList<String> questions = new LinkedList<>();
         for (int i = 0; i < 50; i++) {
            questions.addLast(category + " Question " + i);
         }
         questionMap.put(category, questions);
      }
   }

   public boolean isPlayable() {
      return (howManyPlayers() >= 2);
   }

   public boolean add(String playerName) {
      int playerIndex = howManyPlayers();

      places[playerIndex] = 1;
      purses[playerIndex] = 0;
      inPenaltyBox[playerIndex] = false;

      players.add(playerName);

      System.out.println(playerName + " was added");
      System.out.println("They are player number " + players.size());
      return true;
   }

   public int howManyPlayers() {
      return players.size();
   }

   public void handlePlayerMove(int roll) {
      places[currentPlayer] = (places[currentPlayer] + roll) % 12;
      if (places[currentPlayer] == 0) {
         places[currentPlayer] = 12;
      }

      System.out.println(players.get(currentPlayer) + "'s new location is " + places[currentPlayer]);
      System.out.println("The category is " + currentCategory());

      askQuestion();
   }


   private void handlePenaltyBoxRoll(int roll) {
      String player = players.get(currentPlayer);
      isGettingOutOfPenaltyBox = (roll % 2 != 0);

      String statusMessage = isGettingOutOfPenaltyBox
              ? player + " is getting out of the penalty box"
              : player + " is not getting out of the penalty box";

      System.out.println(statusMessage);

      if (isGettingOutOfPenaltyBox) {
         handlePlayerMove(roll);
      }
   }

   public void roll(int roll) {
      if (!isPlayable()) {
         System.out.println("Not enough players to play. Need at least 2 players.");
         return;
      }

      String player = players.get(currentPlayer);
      System.out.println(player + " is the current player");
      System.out.println("They have rolled a " + roll);

      if (inPenaltyBox[currentPlayer]) {
         handlePenaltyBoxRoll(roll);
      } else {
         handlePlayerMove(roll);
      }
   }

   private void askQuestion() {
      LinkedList<String> questions = questionMap.get(currentCategory());
      if (questions != null && !questions.isEmpty()) {
         System.out.println(questions.removeFirst());
      }
   }

   private String currentCategory() {
      int place = (places[currentPlayer] - 1) % 4;
       return switch (place) {
           case 0 -> "Pop";
           case 1 -> "Science";
           case 2 -> "Sports";
           default -> "Rock";
       };
   }

   public void handleReward(){
      purses[currentPlayer]++;
      System.out.println(players.get(currentPlayer)
              + " now has "
              + purses[currentPlayer]
              + " Gold Coins.");
   }

   public boolean checkWinner()  {
      boolean winner = didPlayerWin();
      moveToTheNextPlayer();
      return winner;
   }

   public void moveToTheNextPlayer() {
      currentPlayer = (currentPlayer + 1) % players.size();
   }

   public boolean handleCorrectAnswer() {
      if (inPenaltyBox[currentPlayer] && !isGettingOutOfPenaltyBox) {
         moveToTheNextPlayer();
         return true;
      }
      System.out.println("Answer was correct!!!!");
      handleReward();
      return checkWinner();
   }

   public boolean wrongAnswer() {
      System.out.println("Question was incorrectly answered");
      System.out.println(players.get(currentPlayer) + " was sent to the penalty box");
      inPenaltyBox[currentPlayer] = true;

      moveToTheNextPlayer();
      return true;
   }

   private boolean didPlayerWin() {
      return !(purses[currentPlayer] == SIZE);
   }
}
