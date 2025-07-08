package trivia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

// REFACTOR ME
public class Game implements IGame {
   ArrayList<String> players = new ArrayList<String>();
   int[] places = new int[6];
   int[] purses = new int[6];
   boolean[] inPenaltyBox = new boolean[6];

   LinkedList<String> popQuestions = new LinkedList<String>();
   LinkedList<String> scienceQuestions = new LinkedList<String>();
   LinkedList<String> sportsQuestions = new LinkedList<String>();
   LinkedList<String> rockQuestions = new LinkedList<String>();
   Map<String, LinkedList<String>> questionMap = new HashMap<>();

   int currentPlayer = 0;
   boolean isGettingOutOfPenaltyBox;

   public Game() {
      for (int i = 0; i < 50; i++) {
         popQuestions.addLast("Pop Question " + i);
         scienceQuestions.addLast(("Science Question " + i));
         sportsQuestions.addLast(("Sports Question " + i));
         rockQuestions.addLast(createRockQuestion(i));
      }
      questionMap.put("Pop", popQuestions);
      questionMap.put("Science", scienceQuestions);
      questionMap.put("Sports", sportsQuestions);
      questionMap.put("Rock", rockQuestions);
   }

   public String createRockQuestion(int index) {
      return "Rock Question " + index;
   }

   public boolean isPlayable() {
      return (howManyPlayers() >= 2);
   }

   public boolean add(String playerName) {
      places[howManyPlayers()] = 1;
      purses[howManyPlayers()] = 0;
      inPenaltyBox[howManyPlayers()] = false;
      players.add(playerName);

      System.out.println(playerName + " was added");
      System.out.println("They are player number " + players.size());
      return true;
   }

   public int howManyPlayers() {
      return players.size();
   }

   public void handlePlayerMove(int roll){
      places[currentPlayer] += roll;
      if (places[currentPlayer] > 12) places[currentPlayer] -= 12;

      System.out.println(players.get(currentPlayer)
              + "'s new location is "
              + places[currentPlayer]);
      System.out.println("The category is " + currentCategory());
      askQuestion();
   }

   public void roll(int roll) {
      System.out.println(players.get(currentPlayer) + " is the current player");
      System.out.println("They have rolled a " + roll);

      if (inPenaltyBox[currentPlayer]) {
         if (roll % 2 != 0) {
            isGettingOutOfPenaltyBox = true;
            System.out.println(players.get(currentPlayer) + " is getting out of the penalty box");
            handlePlayerMove(roll);
         } else {
            System.out.println(players.get(currentPlayer) + " is not getting out of the penalty box");
            isGettingOutOfPenaltyBox = false;
         }
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
      currentPlayer++;
      if (currentPlayer == players.size()) currentPlayer = 0;
   }

   public boolean handleCorrectAnswer() {
      if (inPenaltyBox[currentPlayer]) {
         if (isGettingOutOfPenaltyBox) {
            System.out.println("Answer was correct!!!!");
            handleReward();
            return checkWinner();
         } else {
            moveToTheNextPlayer();
            return true;
         }
      } else {
         System.out.println("Answer was corrent!!!!");
         handleReward();
         return checkWinner();
      }
   }

   public boolean wrongAnswer() {
      System.out.println("Question was incorrectly answered");
      System.out.println(players.get(currentPlayer) + " was sent to the penalty box");
      inPenaltyBox[currentPlayer] = true;

      currentPlayer++;
      if (currentPlayer == players.size()) currentPlayer = 0;
      return true;
   }


   private boolean didPlayerWin() {
      return !(purses[currentPlayer] == 6);
   }
}
