import java.util.ArrayList;
import java.util.Iterator;
/**
 * Base class for players.  Initializes player's hand
 * and determines whether or not the player's hand is empty.
 */
public class Player {
  /**
   * This player's hand.
   */
  protected ArrayList<Card> list = new ArrayList<Card>();
  
  /**
   * Get seven cards from the deck and store them in this hand.
   */
  public Player(Deck deck) {
    for (int i=1; i<=7; i++) {
      list.add(deck.dealACard());
    }
  } 
  
  /**
   * Return true when this hand is empty.
   */
  public boolean isHandEmpty() {
    return list.isEmpty();
  }

  /**
   * Add the given Card object to this player's hand.
   */
  public void add(Card card) {
    list.add(card);
  }

  /**
   * Remove the card at the specified position (0-based) in
   * this player's hand.
   */
    public void remove(int i) {
	list.remove(i);
    }

    /** Remove the given card from the player's hand. */
    public void remove(Card card) {
	list.remove(card);
    }

    /**
     * Find card with given string representation in this hand.
     */
    public Card find(String cardString) {
    	int i = 0;
    	Card card = null;
    	while (i<this.list.size() && card == null) {
    	    if (this.list.get(i).toString().equals(cardString)) {
    		card = this.list.get(i);
    	    }
    	    i++;
    	}
	    return card;
    }

    public int getNCards() {
	return list.size();
    }
    public Iterator<Card> getCardIterator() {
	return list.iterator();
    }
}
  
  
