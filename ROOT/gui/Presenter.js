"use strict";

/**
 * Play the game Crazy Eights. File that works with two player crazy eights
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
  this.intervalId;

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
    var doc = request.responseXML;
    var playerTurn = doc.getElementsByTagName("playerturn")[0].childNodes[0].textContent;
    var pileSuit = doc.getElementsByTagName("pile")[0].getAttribute("suit");
    var pileValue = doc.getElementsByTagName("pile")[0].getAttribute("value");
    var pileASuit = doc.getElementsByTagName("pile")[0].getAttribute("asuit");

    // window.alert("displaying card " + new Card(pileSuit, pileValue));
    this.pile.acceptACard(new Card(pileSuit, pileValue));
    this.view.displayPileTopCard(new Card(pileSuit, pileValue));

    var cards = doc.getElementsByTagName("cards")[0];
    var cardList = new Array();
    var cardList2 = new Array();
    for(var i = 0; i < cards.childNodes.length; i++) {
       cardList.push(new Card(cards.childNodes[i].getAttribute("suit"), cards.childNodes[i].getAttribute("value")));
       cardList2.push(new Card("b", "jok"));
       this.player1.add(new Card(cards.childNodes[i].getAttribute("suit"), cards.childNodes[i].getAttribute("value")));
    }
    // window.alert("calling display human hand");
    this.view.displayHumanHand(cardList);
    // window.alert("calling display computer hand");
    this.view.displayComputerHand(cardList2);
    //extract data from XML and update model
    //tell view to display extracted data
    if(playerTurn != this.playerNumber) { //not my turn
       this.view.blockPlay(); //check this later!
       // window.alert("attempting to set interval polling");
       var presenter = this;
       this.intervalId = window.setInterval(function() {presenter.poll();}, 3000);
    }
  }
};


Presenter.prototype.pickCardHandler = function(connection) {
  // window.alert("in pick card handler ");
  var doc = connection.responseXML;
  var card = new Card(doc.getElementsByTagName("card")[0].getAttribute("suit"),doc.getElementsByTagName("card")[0].getAttribute("value"));
  this.player1.add(card);
  this.view.displayHumanHand(this.player1.getHandCopy());
  this.view.blockPlay();
  var presenter = this;
  this.intervalId = window.setInterval(function() {presenter.poll();}, 3000);
};

/**
 * Event: User wants card from the deck.
 * Human hand is given a card from the deck and displayed,
 * then user's turn is completed (check for a win)
 * before the computer is given a turn.
 */
Presenter.prototype.pickCard = function() {
  var request = new XMLHttpRequest();
  request.open("POST", "/CrazyServlet", true);
  request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  var presenter = this;
  request.addEventListener("load", function() { presenter.pickCardHandler(request);} );
  request.send("type=pick&player="+this.playerNumber);
};


//accepts a card c
Presenter.prototype.playCardHandler = function() {
  // window.alert("in play card handler ");
     if(this.player1.isHandEmpty()) {
         this.view.announceHumanWinner();
     } else {
         this.view.blockPlay();
         var presenter = this;
         this.intervalId = window.setInterval( function() {presenter.poll();}, 3000);
     }
};


/**
 * Event: Card in user's hand has been selected.  Attempt to
 * play this card to discard pile and inform user if play is
 * illegal.  If card is played and is an 8, allow user to pick
 * a suit.
 */
Presenter.prototype.playCard = function(cardString) {
    var card = this.player1.find(cardString);
    if (!this.pile.isValidToPlay(card)) {
      this.view.displayWrongCardMsg(cardString);// Alert user if illegal choice of card
    }else {
      this.player1.remove(this.player1.indexOf(card));
      this.view.displayHumanHand(this.player1.getHandCopy());
      this.pile.acceptACard(card);
      this.view.displayPileTopCard(card);
      if (this.pile.getTopCard().getValue() == "8") {
        this.view.displaySuitPicker();  // Execution continues at setSuit after user picks suit
      } else {
        // window.alert("attempting to send play data to server");
        var request = new XMLHttpRequest();
        request.open("POST", "/CrazyServlet", true);
        request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        var presenter = this;
        request.addEventListener("load", function() { presenter.playCardHandler();} );
        request.send("type=play&player="+this.playerNumber+"&suit=" + card.getSuit() + "&value=" + card.getValue());
        // window.alert("play data sending in progress...");
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
  // window.alert("attempting to send play data to server");
  var request = new XMLHttpRequest();
  request.open("POST", "/CrazyServlet", true);
  request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  var presenter = this;
  request.addEventListener("load", function() { presenter.playCardHandler();} );
  request.send("type=play&player="+this.playerNumber+"&suit=" + suit + "&value=8");
  window.alert("play data sending in progress...");
};


Presenter.prototype.pollHandler = function(request) {
  // window.alert("pollHandler \n playernumber= "+ this.playerNumber);
  var doc = request.responseXML;
  if(this.playerNumber == doc.getElementsByTagName("playerturn")[0].textContent) { //its my turn
    // window.alert("inside poll handler: it is my turn");
    // window.alert("this.playernum = " + this.playerNumber + "\n player turn = " + doc.getElementsByTagName("playerturn")[0].textContent);
      window.clearInterval(this.intervalId);
      var playerTurn = doc.getElementsByTagName("playerturn")[0].textContent;
      var pileSuit = doc.getElementsByTagName("pile")[0].getAttribute("suit");
      var pileValue = doc.getElementsByTagName("pile")[0].getAttribute("value");
      var pileASuit = doc.getElementsByTagName("pile")[0].getAttribute("asuit");
      this.pile.acceptACard(new Card(pileSuit, pileValue));
      this.view.displayPileTopCard(new Card(pileSuit, pileValue));
      if(pileASuit) {
        this.pile.setAnnouncedSuit(pileASuit);
      }
      var numOpponentCards = doc.getElementsByTagName("opponentcards")[0].textContent;
      window.alert("opponent's number of cards is "+ numOpponentCards);
      if(numOpponentCards == 0) {
         this.view.announceComputerWinner();
      }  else {
         var cardList = new Array();
         for(var i = 0; i < numOpponentCards; i++) {
            cardList.push(new Card("b", "jok"));
         }
         this.view.displayComputerHand(cardList);
         this.view.unblockPlay();
      }
  }
};

Presenter.prototype.poll = function() {
  // window.alert("polling");
  var request = new XMLHttpRequest();
  request.open("POST", "/CrazyServlet", true);
  request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  var presenter = this;
  request.addEventListener("load",
    function() { presenter.pollHandler(request);}, false );
  request.send("type=poll&player="+this.playerNumber);
};
