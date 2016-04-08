import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.regex.*;
import java.util.Enumeration;
import java.util.HashMap;

/**
 *
 */
@WebServlet( urlPatterns={"/CrazyServlet"} )
public class CrazyServlet extends HttpServlet {
  private static final long serialVersionUID = 1;
/**
 * to do:
 *  -highlighting
 *  -url rewriting (how to we verify that it is correct??)
 */
    String[] highlight = {"#eee", "#eee", "#eee", "#eee", "#eee"};

    private void printEnd(PrintWriter servletOut)
    {
          servletOut.println(
            "  </body> \n" +
            "</html> ");
    }
    private void printStart(PrintWriter servletOut) {
      servletOut.println("<!DOCTYPE html>\n " +
        "<html>\n ");

    }

    private void printTable(PrintWriter servletOut, HttpServletResponse response) {
       String table = "<table>\n " +
          "<caption\n " +
          "style=\"border:dashed; border-color:blue; padding:5px; margin:5px;\">Choose\n " +
          "from any of the hands below to play Crazy Eights and possibly get your name\n " +
          "on the board!</caption>\n " +
          "<colgroup><col>\n " +
          "  <col>\n " +
          "  <col>\n " +
          "  <col>\n " +
          "  <col>\n " +
          "</colgroup>\n " +
          "<tbody>\n " +
            "<tr>\n " +
            "  <th>Hand</th>\n " +
            "  <th>Players</th>\n " +
            "  <th>% Players<br>\n " +
            "    winning</th>\n " +
             " <th>Fewest cards<br>\n " +
             "   played in win</th>\n " +
             " <th>Player playing<br>\n " +
            "    fewest cards</th>\n " +
            "</tr>\n " +
            "<tr>\n " +
            "  <td><a href=" + response.encodeURL("\"MVPGame/Crazy8_2.html?seed=0x6904acd2&game=1\"")+">1</a></td>\n " +
            "  <td>" + ConcurrentAccess.numPlayers[0] + "</td>\n " +
            "  <td>" + ConcurrentAccess.percentPlayersWinning[0] + "</td>\n " +
            "  <td>" + ConcurrentAccess.fewestCards[0] + "</td>\n " +
            "  <td bgcolor=" + highlight[0] + ">" + ConcurrentAccess.winner[0] + "</td>\n " +
            "</tr>\n " +
            "<tr>\n " +
             " <td><a href=" + response.encodeURL("\"MVPGame/Crazy8_2.html?seed=0xe03d8ca4&game=2\"")+">2</a></td>\n " +
             " <td>" + ConcurrentAccess.numPlayers[1] + "</td>\n " +
             " <td>" + ConcurrentAccess.percentPlayersWinning[1] + "</td>\n " +
              "<td>" + ConcurrentAccess.fewestCards[1]+"</td>\n " +
             " <td bgcolor=" + highlight[1]+">"+ ConcurrentAccess.winner[1] + "</td>\n " +
            "</tr>\n " +
           " <tr>\n " +
              "<td><a href=" + response.encodeURL("\"MVPGame/Crazy8_2.html?seed=0x500aee51&game=3\"")+">3</a></td>\n " +
              "<td>"+ ConcurrentAccess.numPlayers[2] + "</td>\n " +
              "<td>"+ ConcurrentAccess.percentPlayersWinning[2] + "</td>\n " +
              "<td>"+ ConcurrentAccess.fewestCards[2] + "</td>\n " +
              "<td bgcolor=" + highlight[2] + ">" + ConcurrentAccess.winner[2] + "</td>\n " +
            "</tr>\n " +
            "<tr>\n " +
              "<td><a href=" + response.encodeURL("\"MVPGame/Crazy8_2.html?seed=0x8752f900&game=4\"") + ">4</a></td>\n " +
              "<td>"+ ConcurrentAccess.numPlayers[3] + "</td>\n " +
             " <td>"+ ConcurrentAccess.percentPlayersWinning[3] + "</td>\n " +
            "  <td>"+ ConcurrentAccess.fewestCards[3] + "</td>\n " +
            "<td bgcolor=" + highlight[3]+">"+ ConcurrentAccess.winner[3] + "</td>\n " +
            "</tr>\n " +
            "<tr>\n " +
              "<td><a href=" + response.encodeURL("\"MVPGame/Crazy8_2.html?seed=0xbb905669&game=5\"") + ">5</a></td>\n " +
              "<td>"+ ConcurrentAccess.numPlayers[4] + "</td>\n " +
             " <td>"+ ConcurrentAccess.percentPlayersWinning[4]+"</td>\n " +
             " <td>"+ ConcurrentAccess.fewestCards[4]+"</td>\n " +
           "   <td bgcolor=" + highlight[4] + ">"+ ConcurrentAccess.winner[4] + "</td>\n " +
          " </tr>\n " +
         " </tbody>\n ";
         servletOut.println(table);

    }

    private void printHead(PrintWriter servletOut){
      String head =
      "<head>\n " +
         " <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n " +
         " <title>Select a Game</title>\n " +
         " </script>\n " +
         " <style type=\"text/css\">\n " +
         "   col { width:20%; }\n " +
         "   td,th  { text-align: center; }\n " +
         "   table, td, th { border: 1px solid gray }\n " +
         " </style>\n " +
         "   <link rel=\"stylesheet\" href=\"MVPGame/style2.css\" type=\"text/css\">\n " +
         " <meta name=\"generator\" content=\"Amaya, see http://www.w3.org/Amaya/\">\n " +
        "</head>\n " +
        "<body>\n ";
        servletOut.println(head);
    }

    private void printTitle(PrintWriter servletOut, String signIn) {
      String title=  "<h1><span id=\"title\">Crazy Eights</span></h1>\n " +
        "<h1>Welcome, " + WebTechUtil.escapeXML(signIn) + "!</h1>\n ";
        servletOut.println(title);
    }


    public void doGet (HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException
      {
      int gameNumber;
      response.setHeader("Cache-Control", "no-cache");
      response.setContentType("text/html; charset=\"UTF-8\"");
      PrintWriter servletOut = response.getWriter();
      HttpSession session = request.getSession();
      request.getQueryString(); //what is this for?
      if(session.isNew()) {
          servletOut.println(
            "<!DOCTYPE html> \n " +
             "<html xmlns='http://www.w3.org/1999/xhtml'> \n" +
             " <head> \n "+
               " <title> \n"+
                  "Crazy Eights Sign-in!\n"+
                "</title>\n"+
              "</head>\n"+
              "<body>\n"+
               " <form method='post'><div>\n"+
                 " <label>\n"+
                   " Please sign in: <input type='text' name='signIn' />\n"+
                  "</label>\n"+
                 "<br />\n"+
                "<input type='submit' name='doit' value='Sign In' />\n"+
               " </div></form>\n"+
              "</body>\n"+
            "</html>\n");
          servletOut.close();
      } else { //session is not new
          String signIn = WebTechUtil.escapeXML(session.getAttribute("signIn").toString());
          String welcome = "Welcome, " + signIn + "!"; //generate this string based on whether they won or lost
          if(request.getParameter("result")!=null) {
            String result = request.getParameter("result");
            gameNumber = Integer.parseInt(request.getParameter("game"));
            int cardsPlayed = Integer.parseInt(request.getParameter("cardsPlayed"));
            if(result.equals("won")) {
              highlight[gameNumber-1] = "pink";
              ConcurrentAccess.updateStats(gameNumber - 1,  signIn, true, cardsPlayed);
              welcome = "Congratulations, " + signIn + "! Play again?";
            } else {
              ConcurrentAccess.updateStats(gameNumber - 1,  signIn, false, cardsPlayed);
              welcome = "Sorry, " + signIn + ", better luck next time!";
            }
          }
          session.setAttribute("signIn", signIn);
          printStart(servletOut);
          printHead(servletOut);
          printTitle(servletOut, signIn);
          printTable(servletOut, response);
          printEnd(servletOut);
          servletOut.close();
          highlight[0] = "#eee"; //reset the highlighting for next time
          highlight[1] = "#eee";
          highlight[2] = "#eee";
          highlight[3] = "#eee"; 
          highlight[4] = "#eee"; 

      }
     }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
      request.getQueryString();
      String signIn = WebTechUtil.escapeXML(request.getParameter("signIn"));
      // String welcome = "Welcome, " + WsignIn + "!"; //generate this string based on whether they won or lost
      String result = request.getParameter("result");
      response.setHeader("Cache-Control", "no-cache");
      response.setContentType("text/html; charset=\"UTF-8\"");
      PrintWriter servletOut = response.getWriter();
      HttpSession session = request.getSession();
      if(signIn!=null) {
        session.setAttribute("signIn", signIn);
        printStart(servletOut);
        printHead(servletOut);
        printTitle(servletOut, signIn);
        printTable(servletOut, response);
        printEnd(servletOut);
        servletOut.close();

      }
    }
}

/**
 * Utilities to support examples in "Web Technologies" textbook
 */
class WebTechUtil {

    /**
     * Ampersand pattern used by escapeXML
     */
    static private Pattern pAmp = Pattern.compile("&");
    /**
     * Less-than pattern used by escapeXML
     */
    static private Pattern pLT =  Pattern.compile("<");
    /**
     * Greater-than pattern used by escapeXML
     */
    static private Pattern pGT =  Pattern.compile(">");
    /**
     * Double-quote pattern used by escapeQuotes
     */
    static private Pattern pDQ = Pattern.compile("\"");
    /**
     * Single-quote pattern used by escapeQuotes
     */
    static private Pattern pSQ = Pattern.compile("'");


    /**
     * Return input string with ampersands (&),
     * less-than signs (<), and greater-than signs (>)
     * replaced with character entity references.
     */
    static public String escapeXML(String inString)
    {
        Matcher matcher = pAmp.matcher(inString);
        String modified = matcher.replaceAll("&amp;");
        matcher = pLT.matcher(modified);
        modified = matcher.replaceAll("&lt;");
        matcher = pGT.matcher(modified);
        modified = matcher.replaceAll("&gt;");
        return modified;
    }

    /**
     * Return input string with all quotes replaced
     * with references.  Use character reference for single quotes
     * because IE6 does not support &apos; entity reference.
     */
    static public String escapeQuotes(String inString)
    {
	Matcher matcher = pDQ.matcher(inString);
	String modified = matcher.replaceAll("&quot;");
	matcher = pSQ.matcher(modified);
	modified = matcher.replaceAll("&#39;");
	return modified;
    }
}

class ConcurrentAccess {
  public static String[] winner = {"-","-","-","-","-"}; //entry 1 corresponds with winner of hand 1, etc.
  public static int[] fewestCards = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
  public static int[] numPlayers = {0,0,0,0,0};
  public static double[] percentPlayersWinning = {0,0,0,0,0};
  public static int[] numWinners = {0,0,0,0,0};
  public static HashMap<String, Player> players = new HashMap<String, Player>();

  public synchronized static void changeWinner(int gameNumber, String signIn) {
    winner[gameNumber] = signIn;
  }

  public synchronized static void changeFewestCards(int gameNumber, int cardsPlayed) {
    fewestCards[gameNumber] = cardsPlayed;
  }

  public synchronized static void changeNumPlayers(int gameNumber) { //+1
    numPlayers[gameNumber]++;
  }

  public synchronized static void updateStats(int gameNumber, String signIn, boolean win, int cardsPlayed) {
    addPlayer(signIn, gameNumber);
    if(win) {
      if(fewestCards[gameNumber] >= cardsPlayed) {
        changeWinner(gameNumber, signIn);
        changeFewestCards(gameNumber, cardsPlayed);
      }
      if(!hasPlayed(signIn, gameNumber)) {
          changeNumPlayers(gameNumber);
          changeNumWinners(gameNumber);
      }
    } else {
      if(!hasPlayed(signIn, gameNumber)) {
          changeNumPlayers(gameNumber);
      }
    }
    if(!hasPlayed(signIn, gameNumber)) {
      playGame(signIn, gameNumber);
    }
    updatePercentPlayersWinning(gameNumber);
  }


  public synchronized static void updatePercentPlayersWinning(int gameNumber) {
    percentPlayersWinning[gameNumber] = ((double)numWinners[gameNumber]/(double)numPlayers[gameNumber])*100; //not sure if this is right
  }

  public synchronized static void changeNumWinners(int gameNumber) { //+1
    numWinners[gameNumber]++;
  }

  public synchronized static void addPlayer(String signIn, int gameNumber) {
    if(!players.containsKey(signIn)) {
      Player p1 = new Player(signIn);
      players.put(signIn, p1);
    }
  }

  public synchronized static boolean hasPlayed(String signIn, int gameNumber) {
    Player p1 = players.get(signIn);
    return p1.hasPlayedGame(gameNumber);
  }

    public synchronized static void playGame(String signIn, int gameNumber) {
      Player p1 = players.get(signIn);
      p1.playGame(gameNumber);
    }
}

class Player {

  private String name = "";
  private boolean[] gamesPlayed = new boolean[5];

  public Player(String signIn) {
    name = signIn;
  }

  public void playGame(int gameNumber) {
    gamesPlayed[gameNumber] = true;
  }

  public boolean hasPlayedGame(int gameNumber) {
    return gamesPlayed[gameNumber];
  }

}