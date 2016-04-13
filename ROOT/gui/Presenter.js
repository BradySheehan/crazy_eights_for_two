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
  this.playerNumber;
  var request = new XMLHttpRequest();

  var params = window.location.search.split(/[?=&]/);
  for (var k = 1; k < params.length; k += 2) {
    if (params[k] == "player") {
       this.playerNumber = Number(params[k+1]);
       window.alert("playernumber = "+this.playerNumber);
    }
  }

  var presenter = this;
  request.open("POST", "/CrazyServlet", true);
  request.addEventListener("load",
    function() { presenter.completeInitialization(request);} );
  request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  request.send("type=poll&player="+this.playerNumber); //query string
}

Presenter.prototype.completeInitialization = function(request) {
  if(request.status == 200) {
	  window.alert("In completeInitialization()");
    var doc = request.responseXML;
    var playerTurn = doc.getElementsByTagName("playerturn")[0].childNodes[0].nodeValue;
    var pileSuit = doc.getElementsByTagName("pile")[0].getAttribute("suit");
    var pileValue = doc.getElementsByTagName("pile")[0].getAttribute("value");
    var pileASuit = doc.getElementsByTagName("pile")[0].getAttribute("asuit");

    if(playerTurn != 1) {
      window.alert("not player one");
    }

      window.alert("displaying card " + new Card(pileSuit, pileValue));
      this.pile.acceptACard(new Card(pileSuit, pileValue));
      this.view.displayPileTopCard(new Card(pileSuit, pileValue));
      this.pile.setAnnouncedSuit(pileASuit);


    var cards = doc.getElementsByTagName("cards")[0];
    var cardList = new Array();
    var cardList2 = new Array();
    for(var i = 0; i < cards.childNodes.length; i++) {
       cardList.push(new Card(cards.childNodes[i].getAttribute("suit"), cards.childNodes[i].getAttribute("value")));
       cardList2.push(new Card("b", "jok"));
       this.player1.add(new Card(cards.childNodes[i].getAttribute("suit"), cards.childNodes[i].getAttribute("value")));
    }
    this.view.displayHumanHand(cardList);
    this.view.displayComputerHand(cardList2);
    //extract data from XML and update model
    //tell view to display extracted data
    if(playerTurn != this.playerNumber) { //not my turn
       this.view.blockPlay(); //check this later!
       var id = window.setInterval(this.poll(request, id), 1500);
    }
  }
};

/**
 * Event: User wants card from the deck.
 * Human hand is given a card from the deck and displayed,
 * then user's turn is completed (check for a win)
 * before the computer is given a turn.
 */
Presenter.prototype.pickCard = function() {
    this.drawCard();
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
    }else {
      var request = new XMLHttpRequest();
      request.open("POST", "/CrazyServlet", true);
      request.setRequestHeader("type", "play");
      request.send("/CrazyServlet/?type=play&player="+this.playerNum+"&suit=" + card.getSuit() + "&value=" + card.getValue());
      this.player1.remove(this.player1.indexOf(card));
      this.view.displayHumanHand(this.player1.getHandCopy());
      this.pile.acceptACard(card);
      this.view.displayPileTopCard(card);
      if (this.pile.getTopCard().getValue() == "8") {
        this.view.displaySuitPicker();
        // Execution continues at setSuit after user picks suit
      }
       if(this.player1.isHandEmpty()) {
           this.view.announceHumanWinner();
       } else {
           this.view.blockPlay();
           var id = window.setInterval(this.poll(request,id), 1500);
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
};


//accepts a card c
Presenter.prototype.playCardHandler = function(c) {
      var request = new XMLHttpRequest();
      request.open("POST", "/CrazyServlet", true);
      request.setRequestHeader("type", "play");
      request.send("/CrazyServlet/?player="+this.playerNum+"suit=" + c.getSuit() + "&value=" + c.getValue());
     if(this.player1.isHandEmpty()) {
         this.view.announceHumanWinner();
     } else {
         this.view.blockPlay();
     }
};


Presenter.prototype.poll = function(request, intervalId) {
  var request = new XMLHttpRequest();
  request.open("POST", "/CrazyServlet", true);
  request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  var presenter = this;
  request.addEventListener("load",
    function() { presenter.pollHandler(request, intervalId);} );
  request.send("type=poll");
};


Presenter.prototype.pollHandler = function(request, intervalId) {
  var doc = request.responseXML;
  //parse doc
  if(this.playerNum != doc.getElementsByTagName("playerturn")[0].nodeValue) {
      window.clearInterval(intervalId);
      var playerTurn = doc.getElementsByTagName("playerturn")[0].nodeValue;
      var pileSuit = doc.getElementsByTagName("pile").getAttribute("suit");
      var pileValue = doc.getElementsByTagName("pile").getAttribute("value");
      var pileASuit = doc.getElementsByTagName("pile").getAttribute("asuit");
      this.pile.AcceptACard(new Card(pileSuit, pileValue));
      this.view.displayPileTopCard(new Card(pileSuit, pileValue));
      this.pile.setAnnouncedSuit(pileASuit);
      var numOpponentCards = doc.getElementsByTagName("opponentcards")[0].nodeValue;
      if(numOpponentCards == 0) {
         this.view.announceComputerWinner();
      }  else {
         var cards = getElementsByTagName("cards");
         var cardList = new Array();
         var cardList2 = new Array();
         for(var i = 0; i < cards[0].childNodes.length; i++) {
            cardList.push(new Card(cards[0].childNodes[i].getAttribute("suit"), cards[0].childNodes[i].getAttribute("value")));
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

Presenter.prototype.drawCardHandler = function(connection) {
  var doc = connection.responseXML;
  var card = new Card(doc.getElementsByTagName("card")[0].getAttribute("suit"),doc.getElementsByTagName("card")[0].getAttribute("value"));
  this.player1.add(card);
  this.view.displayHumanHand(this.player1.getHandCopy());
  this.view.blockPlay();
  var id = window.setInterval(this.poll(connection,id), 1500);
};

Presenter.prototype.drawCard = function() {
  var request = new XMLHttpRequest();
  request.open("POST", "/CrazyServlet", true);
  request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  var presenter = this;
  request.addEventListener("load",
    function() { presenter.drawCardHandler(request);} );
  request.send("type=pick");
};
