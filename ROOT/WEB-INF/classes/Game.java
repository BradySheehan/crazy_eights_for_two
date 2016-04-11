/**
* Play the game Crazy Eights.
* The computer player in this version always draws a card.
* If the deck runs out, the program will throw an exception and terminate.
*/
public class Game {
   private Deck deck;
   private Pile pile;
   private Player[] player = new Player[2];
   private int nextPlayer = 0; // Second player, #0, goes first.

/**
* Initialize game by creating and shuffling the deck,
* dealing one card (other than an 8) to the discard pile,
* and dealing 7 cards to each player.
*/
public Game() {
   pile = new Pile();
   deck = new Deck(pile);
   do {
    deck.shuffle();
 } while (deck.isTopCardAnEight());
 pile.acceptACard(deck.dealACard());
 player[0] = new Player(deck);
 player[1] = new Player(deck);
}

public void playCard(int playerNum, String value, String suit, String announcedSuit) {
   Card playedCard = player[playerNum].find(value+suit);
   player[playerNum].remove(playedCard);
   pile.acceptACard(playedCard);
   if (value.equals("8")) {
    pile.setAnnouncedSuit(announcedSuit);
 }
}

public void addCard(int playerNum, Card card) {
   player[playerNum].add(card);
}

public void toggleTurn() {
   nextPlayer = (nextPlayer+1)%2;
}

public Deck getDeck() { return deck; }
public Pile getPile() { return pile; }
public Player getThisPlayer(int playerNum) {
   return player[playerNum];
}
public Player getOtherPlayer(int playerNum) {
   return player[(playerNum+1)%2];
}
public int getNextPlayer() { return nextPlayer; }
}