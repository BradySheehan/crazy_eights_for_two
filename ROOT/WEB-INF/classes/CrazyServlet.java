import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.regex.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

/**
*
*/
@WebServlet( urlPatterns={"/CrazyServlet"} )
public class CrazyServlet extends HttpServlet {
    private static final long serialVersionUID = 1;
    int numPlayers = 0;
    ArrayList<Game> games = new ArrayList<Game>();

    public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        numPlayers++;
        if(numPlayers%2 != 0) {
            Game game1 = new Game();
            games.add(game1);
            session.setAttribute("game", games.size() - 1); //check this later
            response.sendRedirect("../../gui/Crazy8.html?player=0");
        } else {
            //the second player has showed up
            session.setAttribute("game", games.size() - 1);
            response.sendRedirect("../../gui/Crazy8.html?player=1");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
		PrintWriter pw = null;
		Document doc = null;
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tFactory.newTransformer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        response.setContentType("application/xml; charset=\"UTF-8\"");

        if(session.isNew()) {
            doGet(request, response);
        } else if(request.getParameter("type").equals("play")) {
			  int gameIndex = (int)session.getAttribute("game");
            Game game1 = games.get(gameIndex);
            //assume we have XML doc with card played? 
            //remove card from hand, add card to pile, toggle turn, return empty doc
            Card card1 = null; // getCardFromXMLDoc();
            // game1.player[(int)(session.getAttribute("player"))].remove(card1);
            // game1.pile.acceptACard(card1);
            game1.toggleTurn();
            pw = response.getWriter();
			  doc = emptyDoc();
			  try {
				  transformer.transform(new DOMSource(doc), new StreamResult(pw));
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
        } else if(request.getParameter("type").equals("pick")) {
            Game game1 = games.get((int)session.getAttribute("game"));
            // Card card1 = game1.deck.dealACard();
            // game1.addCard(Integer.parseInt(request.getParameter("player")), card1);
            game1.toggleTurn();
            pw = response.getWriter();
			  // doc = topCardFromDeckAsXMLDoc(game1.deck.dealACard());
            try {
				  transformer.transform(new DOMSource(doc), new StreamResult(pw));
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
        } else if(request.getParameter("type").equals("poll")) {
            Game game1 = games.get((int)session.getAttribute("game"));
            pw = response.getWriter();
			  // doc = newPollXMLDoc(game1.player[game1.getNextPlayer()].list, game1.pile, game1.getNextPlayer());
			  try {
					transformer.transform(new DOMSource(doc), new StreamResult(pw));
			  } catch (Exception e) {
				   e.printStackTrace();
			  }
        }
    }

    public Document newPollXMLDoc(ArrayList<Card> hand, Pile pile, int playerNum) {
        DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
  	     DocumentBuilder dBuilder = null;
		  try {
			  dBuilder = dbFactory.newDocumentBuilder();
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
        Document doc = dBuilder.newDocument();

        Element root = doc.createElement("game");
        doc.appendChild(root);

        Element playerTurn = doc.createElement("playerturn");
        playerTurn.appendChild(doc.createTextNode(String.valueOf(playerNum)));

        Element pile1 = doc.createElement("pile");
        // pile1.setAttribute("suit", pile.getTopCard().suit);
        // pile1.setAttribute("value", pile.getTopCard().value);
        pile1.setAttribute("asuit", pile.getAnnouncedSuit());

        Element cards = doc.createElement("cards");
		 
        for(Card c1:hand) {
            Element card = doc.createElement("card");
            // card.setAttribute("suit", c1.suit);
            // card.setAttribute("value", c1.value);
            cards.appendChild(card);
        }
        return doc;
    }

    public Document topCardFromDeckAsXMLDoc(Card c) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
		 try {
			  dBuilder = dbFactory.newDocumentBuilder();
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
        Document doc = dBuilder.newDocument();

        Element root = doc.createElement("card");
        // root.setAttribute("suit", c.suit);
        // root.setAttribute("value", c.value);
        root.appendChild(root);
    	 return doc;
    }

    public Document emptyDoc() {
        DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
		  try {
			  dBuilder = dbFactory.newDocumentBuilder();
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
        Document doc = dBuilder.newDocument();
        return doc;
    }
}

/**
 * Base class for players.  Initializes player's hand
 * and determines whether or not the player's hand is empty.
 */
 class Player {
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
  
  
/**
 * Discard pile of cards.
 */
 class Pile {
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

/**
* Play the game Crazy Eights.
* The computer player in this version always draws a card.
* If the deck runs out, the program will throw an exception and terminate.
*/
 class Game {
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

/**
 * Deck of playing cards.
 */
 class Deck {
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

/**
 * A single playing card.
 */
 class Card {
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

// class User {
// //sessionId
// //gameNumber

//   public User(int sessionId, int gameNumber) {


//   }
// }