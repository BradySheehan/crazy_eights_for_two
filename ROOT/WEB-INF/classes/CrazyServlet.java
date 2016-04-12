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
        PrintWriter pw = null;
        if(numPlayers%2 != 0) {
            Game game1 = new Game();
            games.add(game1);
            session.setAttribute("game", games.size() - 1); //check this later
            response.sendRedirect("../../gui/Crazy8.html?player=0");
        } else {
            //the second player has shown up
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
      Card card1 = new Card(request.getParameter("suit"), request.getParameter("value")); // getCardFromXMLDoc();
      game1.getThisPlayer(Integer.parseInt(request.getParameter("player"))).remove(card1);
      game1.getPile().acceptACard(card1);
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
      Card card1 = game1.getDeck().dealACard();
      game1.addCard(Integer.parseInt(request.getParameter("player")), card1);
      game1.toggleTurn();
      pw = response.getWriter();
      doc = topCardFromDeckAsXMLDoc(game1.getDeck().dealACard());
      try {
        transformer.transform(new DOMSource(doc), new StreamResult(pw));
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if(request.getParameter("type").equals("poll")) {
      Game game1 = games.get((int)session.getAttribute("game"));
      pw = response.getWriter();
      doc = newPollXMLDoc(game1, request);
      try {
       transformer.transform(new DOMSource(doc), new StreamResult(pw));
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
    }

    public Document newPollXMLDoc(Game game1, HttpServletRequest request) {
         Pile pile = game1.getPile();
         int playerNum = game1.getNextPlayer();
         int currentPlayer = (int)Integer.valueOf(request.getParameter("player"));
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
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
        playerTurn.setTextContent(String.valueOf(playerNum));
        root.appendChild(playerTurn);

        Element opponentCards = doc.createElement("opponentCards");
        opponentCards.appendChild(doc.createTextNode(String.valueOf(game1.getOtherPlayer(currentPlayer).getNCards())));
        root.appendChild(opponentCards);

        Element pile1 = doc.createElement("pile");
        pile1.setAttribute("suit", pile.getTopCard().getSuit());
        pile1.setAttribute("value", pile.getTopCard().getValue());
        pile1.setAttribute("asuit", pile.getAnnouncedSuit());
        root.appendChild(pile1);

        Element cards = doc.createElement("cards");
        Iterator<Card> it = game1.getThisPlayer(currentPlayer).getCardIterator();
        while(it.hasNext()) { //not sure about this.
            Card c1 = it.next();
            Element card = doc.createElement("card");
            card.setAttribute("suit", c1.getSuit());
            card.setAttribute("value", c1.getValue());
            cards.appendChild(card);
        }
        root.appendChild(cards);

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
        root.setAttribute("suit", c.getSuit());
        root.setAttribute("value", c.getValue());
        root.appendChild(root);
    	 return doc;
    }

    public Document emptyDoc() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
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