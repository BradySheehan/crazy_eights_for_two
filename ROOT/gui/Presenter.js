"use strict";

/**
 * Play the game Crazy Eights.
 * The computer player in this version always draws a card.
 * If the deck runs out, the program will throw an exception and terminate.
 */
function Presenter() {
  /**
   * Initialize game by creating and shuffling the deck,
   * dealing one card (other than an 8) to the discard pile, and
   * dealing 7 cards to each player.  Then create
   * View, set listeners, and display the initial situation.
   */

  this.pile = new Pile();
  // Create View, providing reference to this Presenter
  this.view = new View(this);
  this.player1 = new Player(); //
  this.player2 = new Player(); //i think we need to pass cards from XML here to this initialization
  // Ask View to associate event handlers with objects
  this.view.setDeckListener(this.pickCard);
  this.view.setCardListener(this.playCard);
  this.view.setSuitListener(this.setSuit);

  var request = new XMLHttpRequest();

  this.playerNumber = Number(window.location.search.split(/?[=&]/)[window.location.search.split(/[?=&]/).indexOf("player") + 1]);
  var presenter = this;
  request.addEventListener("load",
    function() { presenter.completeInitialization(request);} );
  request.open("POST", "/CrazyServlet", true);
  request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  request.send("type=poll"); //query string

    // Display initial situation
    // the intial situation won't be displayed until both players are joined?
  this.view.displayComputerHand(this.computer.getHandCopy());
  this.view.displayPileTopCard(this.pile.getTopCard());
  this.view.displayHumanHand(this.human.getHandCopy());
}

Presenter.prototype.completeInitialization = function(request) {
  if(request.status == 200) {
	  window.alert("In completeInitialization()");
      var doc = request.responseXML;
      var playerTurn = doc.getElementsByTagName("playerturn")[0].nodeValue;
      var pileSuit = doc.getElementsByTagName("pile").getAttribute("suit");
      var pileValue = doc.getElementsByTagName("pile").getAttribute("value");
      var pileASuit = doc.getElementsByTagName("pile").getAttribute("asuit");
      this.view.displayPileTopCard(new Card(pileSuit, pileValue));
      this.pile.setAnnouncedSuit(pileASuit);
      var cards = getElementsByTagName("cards");
      var cardList = new Array();
      var cardList2 = new Array();
      for(Element e : cards.childNodes) {
         cardList.push(new Card(e.getAttribute("suit"), e.getAttribute("value")));
         cardList2.push(new Card("b", "jok"));
         this.player1.add(new Card(e.getAttribute("suit"), e.getAttribute("value")));
      }
      this.view.displayHumanHand(cardList);
      this.view.displayComputerHand(cardList2);
      //extract data from XML and update model
      //tell view to display extracted data
      if(playerTurn != this.playerNum) { //not my turn
         this.view.blockPlay(); //check this later!
         var id = window.setInterval("pollHandler(request, id)", 1500);
      }
  }
}

/**
 * Event: User wants card from the deck.
 * Human hand is given a card from the deck and displayed,
 * then user's turn is completed (check for a win)
 * before the computer is given a turn.
 */
Presenter.prototype.pickCard = function() {
    drawCardHandler();
};

/**
 * Event: Card in user's hand has been selected.  Attempt to
 * play this card to discard pile and inform user if play is
 * illegal.  If card is played and is an 8, allow user to pick
 * a suit.
 */
Presenter.prototype.playCard = function(cardString) {

    // Alert user if illegal choice of card
    var card = this.player1.find(cardString);
    if (!this.pile.isValidToPlay(card)) {
      this.view.displayWrongCardMsg(cardString);
    }

    // Card is playable.  Remove from hand and add to discard pile.
    // Then, if it's an eight, show the suit-picker.
    // Either immediately (card played was not an 8) or after the player1
    // picks a suit (card was an 8), continue to completeUserPlay() to check
    // for win before turning play over to the computer.
    else {
      this.player1.remove(this.player1.indexOf(card));
      this.view.displayplayerHand(this.player1.getHandCopy());
      this.pile.acceptACard(card);
      this.view.displayPileTopCard(card);
      if (this.pile.getTopCard().getValue() == "8") {
        this.view.displaySuitPicker();
        // Execution continues at setSuit after user picks suit
      }

      else {
         playCardHandler(card);
        // this.completeUserPlay();
      }
    }
};

/**
 * Event: Suit has been picked.
 * Record the suit, undisplay the suit picker, and complete
 * human's turn.
 */
Presenter.prototype.setSuit = function(suit) {
  this.pile.setAnnouncedSuit(suit);
  this.view.undisplaySuitPicker();
  this.completeUserPlay();
};


/**
 * Complete user play (possibly after suit picked)
 * by checking for win.  Display message if win,
 * otherwise allow computer to take a turn.
 */
Presenter.prototype.completeUserPlay = function()
{
    // Are you done?
    if (this.player1.isHandEmpty()) {
      this.view.announceHumanWinner();
    }

    // If not, play my (computer) hand
    // else {
    //   this.playComputer();
    // }


};

Presenter.prototype.playCardHandler = function(Card c) {
      var request = new XMLHttpRequest();
      request.setRequestHeader("type", "play");
      request.send("/CrazyServlet/?player="+this.playerNum+"suit=" + c.getSuit() + "&value=" + c.getValue());
     if(this.player1.isHandEmpty()) {
         this.view.announceHumanWinner();
     } else {
         this.view.blockPlay();
     }
};

Presenter.prototype.pollHandler = function(request, intervalId) {
  request.send("type=poll");
  var doc = request.responseXML;
  //parse doc
  if(this.playerNum == doc.getElementsByTagName("playernum")[0].nodeValue) {
      window.clearInterval(intervalId);
      var playerTurn = doc.getElementsByTagName("playerturn")[0].nodeValue;
      var pileSuit = doc.getElementsByTagName("pile").getAttribute("suit");
      var pileValue = doc.getElementsByTagName("pile").getAttribute("value");
      var pileASuit = doc.getElementsByTagName("pile").getAttribute("asuit");
      this.view.displayPileTopCard(new Card(pileSuit, pileValue));
      this.pile.setAnnouncedSuit(pileASuit);
      var numOpponentCards = doc.getElementsByTagName("opponentcards")[0].nodeValue;
      if(numOpponentCards == 0) {
         this.view.announceComputerWinner();
      }  else {
         var cards = getElementsByTagName("cards");
         var cardList = new Array();
         var cardList2 = new Array();
         for(Element e : cards.childNodes) {
            cardList.push(new Card(e.getAttribute("suit"), e.getAttribute("value")));
         }
         for(var i = 0; i < numOpponentCards; i++) {
            cardList2.push(new Card("b", "jok"));
         }
         this.view.displayHumanHand(cardList);
         this.view.displayComputerHand(cardList2);
         this.view.unblockPlay();
      }
  }

};

Presenter.prototype.drawCardHandler = function(connection){
  connection.send("type=pick");
  var id = window.setInterval("drawCard(connection)", 1500);
  this.view.blockPlay();
};

Presenter.prototype.drawCard = function(connection){
  var doc = connection.responseXML;

  var card = new Card(doc.getElementsByTagName("card")[0].getAttribute("suit"),doc.getElementsByTagName("card")[0].getAttribute("value"));
  this.player1.add(card);
  this.view.displayHumanHand(this.player1.getHandCopy());
  this.view.blockPlay();
  var id = window.setInterval("pollHandler(connection,id)", 1500);
};
