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

//added code for interacting with server//
  this.deck = new Deck();
  do {
    this.deck.shuffle();
  } while (this.deck.isTopCardAnEight());

  this.pile = new Pile();
  this.pile.acceptACard(this.deck.dealACard());
  this.human = new Player(this.deck);
  this.computer = new Player(this.deck);

  // Create View, providing reference to this Presenter
  this.view = new View(this);

  // Ask View to associate event handlers with objects
  this.view.setDeckListener(this.pickCard);
  this.view.setCardListener(this.playCard);
  this.view.setSuitListener(this.setSuit);

  // Display initial situation
  this.view.displayComputerHand(this.computer.getHandCopy());
  this.view.displayPileTopCard(this.pile.getTopCard());
  this.view.displayHumanHand(this.human.getHandCopy());
  this.playerNumber;
  var request = new XMLHttpRequest();
  var presenter = this;
  request.addEventListener("load",
    function() { presenter.completeInitialization(request);} );
  request.open("POST", "/CrazyServlet", true);
  request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  request.send("type=poll"); //query string
}

Presenter.prototype.completeInitialization = function(request) {
  if(request.status == 200) {
	window.alert("In completeInitialization()");
    var responseDocument = request.responseXML;
    var cards = responseDocument.getElementsByTagName("card")[0].getAttribute("suit");
	 window.alert(cards);
    var notMyTurn = true;
    //extract data from XML and update model
    //tell view to display extracted data
    if(notMyTurn) {
      view.blockPlay();
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
    this.human.list.push(this.deck.dealACard());
    this.view.displayHumanHand(this.human.getHandCopy());
    this.completeUserPlay();
};

/**
 * Event: Card in user's hand has been selected.  Attempt to
 * play this card to discard pile and inform user if play is
 * illegal.  If card is played and is an 8, allow user to pick
 * a suit.
 */
Presenter.prototype.playCard = function(cardString) {

    // Alert user if illegal choice of card
    var card = this.human.find(cardString);
    if (!this.pile.isValidToPlay(card)) {
      this.view.displayWrongCardMsg(cardString);
    }

    // Card is playable.  Remove from hand and add to discard pile.
    // Then, if it's an eight, show the suit-picker.
    // Either immediately (card played was not an 8) or after the human
    // picks a suit (card was an 8), continue to completeUserPlay() to check
    // for win before turning play over to the computer.
    else {
      this.human.remove(this.human.indexOf(card));
      this.view.displayHumanHand(this.human.getHandCopy());
      this.pile.acceptACard(card);
      this.view.displayPileTopCard(card);
      if (this.pile.getTopCard().getValue() == "8") {
        this.view.displaySuitPicker();
        // Execution continues at setSuit after user picks suit
      }
      else {
        this.completeUserPlay();
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
    if (this.human.isHandEmpty()) {
      this.view.announceHumanWinner();
    }

    // If not, play my (computer) hand
    else {
      this.playComputer();
    }
};

/**
 * Play for the computer.  In this version, the computer always plays
 * the first card in its hand that is playable.  If it plays an 8,
 * the suit implicitly announced is the suit on the card.
 */
Presenter.prototype.playComputer = function() {
  // Play the first playable card, or pick if none is playable.
  var i=0;
  var hand = this.computer.getHandCopy(); // copy of hand for convenience
  var card = hand[0];
  while (!this.pile.isValidToPlay(card) && i<hand.length-1) {
    i++;
    card = hand[i];
  }
  hand = null; // actual hand will change below, so don't continue to use copy
  if (this.pile.isValidToPlay(card)) {
    this.computer.remove(i);
    this.pile.acceptACard(card);
    this.view.displayPileTopCard(card);
    if (this.pile.getTopCard().getValue() == "8") {
      this.pile.setAnnouncedSuit(card.getSuit());
    }
    this.view.displayComputerHand(this.computer.getHandCopy());
    if (this.computer.isHandEmpty()) {
      this.view.announceComputerWinner();
    }
  }
  else {
    this.computer.add(this.deck.dealACard());
    this.view.displayComputerHand(this.computer.getHandCopy());
  }
};

Presenter.prototype.playCardHandler = function(connection) {
  //tell view to update player hand and pile
  //get suit, as needed (if played an 8)
  //send play data to servet (card and suit)
    //can send as a simple query string
    //no need to parse empty responde
  //if user has won, announc win
  //else block user and set up interval polling
};

Presenter.prototype.pollHandler = function(request, intervalId) {
  request.send("type=poll");
  var respondeDoc = request.responseXML;
  //parse doc
  if(played) {
    window.clearInterval(intervalId);
    this.computer.
    //update opponet hand and possibly pile (logic)
    //tell view to update display (graphics)
      //announce suit if necessary (displaysuitpicker)
    //announce win if opponenthas won (check for win)
    view.unblockPlay();
  }

};

Presenter.prototype.drawCardHandler = function(connection){
  connection.send("type=pick");
  var id = window.setInterval("drawCard(connection)", 1500);
  view.blockPlay();
};

Presenter.prototype.drawCard = function(connection){
  var responseDoc = connection.responseXML;
  parseXML(responseDoc);

  var card;
  var numPlayer;
  this.player.add(card);
  view.displayHumanHand(this.player.getHandCopy());
  view.blockPlay();
  var id = window.setInterval("pollHandler(connection,id)", 1500);
  //set up interval polling
};

Presenter.prototype.extractXMLData = function(){
  //parse XML document
  /*if (empty) {
    poll for data
  } else if (playedCard) {
    //play card then call play card handler
  } else if (drewCard) {
    //draw card then call draw card handler
  }*/
};