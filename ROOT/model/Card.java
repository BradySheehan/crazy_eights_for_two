/**
 * A single playing card.
 */
public class Card {
  private String suit;
  private String value;
  public Card(String aSuit, String aValue)
  { suit = aSuit; value = aValue; }
  public String getSuit() { return suit; }
  public String getValue() { return value; }
  
  /**
   * Return a string representation of this card.
   * This is what will be printed if a Card is passed
   * as an argument to System.out.println().
   */
  public String toString() {
    return value + suit;
  }
}
