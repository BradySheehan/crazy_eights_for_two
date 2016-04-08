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

    public void doGet (HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException
      {
      int gameNumber;
      response.setHeader("Cache-Control", "no-cache");
      response.setContentType("text/html; charset=\"UTF-8\"");
      PrintWriter servletOut = response.getWriter();
      HttpSession session = request.getSession();
      request.getQueryString(); //what is this for?
      if(session.isNew()) {}
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

}