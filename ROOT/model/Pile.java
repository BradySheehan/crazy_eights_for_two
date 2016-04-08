
import java.util.ArrayList;
/**
 * Discard pile of cards.
 */
public class Pile {
  /** List of cards on the pile. */
  // For simplicity, 0 is considered the top card of the pile.
  private ArrayList<Card> list = new ArrayList<Card>();
  
  /** If an 8 is played, this is the announced suit preference. */
  private String announcedSuit;
  
  public Pile() { }
  
  /**
   * Return true if the given card can be legally played on the
   * current pile.
   */
  public boolean isValidToPlay(Card card) {
    boolean retVal = false;
    Card topCard = list.get(0);  // would be more efficient to
                                 // make last card the top
    if (card.getValue().equals("8")) {
	retVal = true;
    }
    else if (topCard.getValue().equals("8")) {
	retVal = (card.getSuit().equals(announcedSuit));
    }
    else if (card.getSuit().equals(topCard.getSuit())
               || 
             card.getValue().equals(topCard.getValue())) {
	retVal = true;
    }
    return retVal;
  }
  
  /**
   * Accept a card and make it the new top of the discard pile.
   */
  public void acceptACard(Card card) {
    list.add(0, card);
  }
  /**
   * Remember the suit preference announced when the most recent
   * 8 was played.
   */ 
  public void setAnnouncedSuit(String suit) {
    announcedSuit = suit;
  }

    public String getAnnouncedSuit() {
	return announcedSuit;
    }

  /**
   * Return the card that is on top of the pile.  The card is not removed.
   */
  public Card getTopCard() {
    return list.get(0);
  }

    /** 
     * Remove all cards but the top one from this list and return
     * the removed cards as a list. 
     */
    public ArrayList<Card> removeAllButTop() {
	ArrayList<Card> retList = list;
	list = new ArrayList<Card>();
	list.add(retList.remove(0));
	return retList;
    }
}
