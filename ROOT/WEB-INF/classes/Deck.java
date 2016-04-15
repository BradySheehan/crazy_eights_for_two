import java.util.ArrayList;

/**
 * Deck of playing cards.
 */
public class Deck {
  /**
   * Deck of cards.
   */
    private ArrayList<Card> list = new ArrayList<Card>();

    /**
     * Discard pile (take cards from this if deck runs out).
     */
    private Pile pile; 

  /**
   * Initialize deck to represent regular 52 playing cards.
   */
  public Deck(Pile pile) {
    String[] suit = {"c", "d", "h", "s"};
    String[] value = {"2", "3", "4", "5", "6", "7", "8", "9", "10",
      "j", "q", "k", "a"};
    for (String s : suit) {
      for (String v : value) {
        list.add(new Card(s,v));
      }
    }
    this.pile = pile;
  }
  /**
   * Shuffle the deck.
   */
  public void shuffle() {
    java.util.Collections.shuffle(list);
  }
  /**
   * Remove top card from the deck and return it.
   * If deck is empty, replenish deck from discard pile.
   */
  public Card dealACard() {
      if (list.size() == 0) {
    	  list = pile.removeAllButTop();
    	  shuffle();
      }
      return list.remove(0);
  }
  /**
   * Indicate whether or not top card of deck is an 8.
   * This method is intended to be used only during game
   * initialization to avoid starting the pile with an 8.
   */
  public boolean isTopCardAnEight() {
    return list.get(0).getValue().equals("8");
  }
}
