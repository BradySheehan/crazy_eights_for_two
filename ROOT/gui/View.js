"use strict";

/**
 * Provide methods for interacting with the user.
 */
function View(presenter) {
  this.presenter = presenter;
  this.cardClickHandler = null;
}

/**
 * Set the click listener on the deck.
 */
View.prototype.setDeckListener = function(deckListener) {
  var presenter = this.presenter; // close over local copy of presenter ref
  var deckClickHandler = function(event) {
    deckListener.call(presenter);
  };
  var deckImg = document.getElementById("deck");
  deckImg.addEventListener("click", deckClickHandler, false);
}

/**
 * Remember the click listener for human's cards.
 */
View.prototype.setCardListener = function(cardListener) {
  var presenter = this.presenter; // close over local copy of presenter ref
  this.cardClickHandler = function(event) {
    // Card's string representation is stored as value of
    // alt attribute of the img element representing the card.
    var cardString = event.target.getAttribute("alt");
    cardListener.call(presenter, cardString);
  };
}

/**
 * Set the click listeners on the suit picker images.
 */
View.prototype.setSuitListener = function(suitListener) {
  var presenter = this.presenter; // close over local copy of presenter ref
  var suitClickHandler = function(event) {
    // First letter of id of clicked image is the selected suit
    var suit = event.target.getAttribute("id")[0];
    suitListener.call(presenter, suit);
  };
  var pickImg = document.getElementById("clubs");
  pickImg.addEventListener("click", suitClickHandler, false);
  pickImg = document.getElementById("diamonds");
  pickImg.addEventListener("click", suitClickHandler, false);
  pickImg = document.getElementById("hearts");
  pickImg.addEventListener("click", suitClickHandler, false);
  pickImg = document.getElementById("spades");
  pickImg.addEventListener("click", suitClickHandler, false);
}


/**
 * Display information about the computer's hand.
 * Hand is an array of Card's.
 */
View.prototype.displayComputerHand = function(hand) {
  var myDiv = document.getElementById("myHand");

  // Hand has more cards than are displayed, so add some to display
  if (hand.length > myDiv.childNodes.length) {
    for (var i=myDiv.childNodes.length; i<hand.length; i++) {
      this.addBackCardImage(hand[i],myDiv);
    }
  }
  // Hand has fewer than are displayed, so remove some from display.
  else {
    while (hand.length < myDiv.childNodes.length) {
      myDiv.removeChild(myDiv.childNodes[myDiv.childNodes.length-1]);
    }
  }
};


/**
 * Display the top card of the discard pile.
 */
View.prototype.displayPileTopCard = function(card) {

  var tableDiv = document.getElementById("table");
  var newImg = document.createElement("img");
  // Discard pile is second child of table div.
  tableDiv.replaceChild(newImg, tableDiv.childNodes[1]);
  newImg.setAttribute("src", card.getURL());
  newImg.setAttribute("alt", "Discard pile");
};

/**
 * Display a "wrong card" message.
 */
View.prototype.displayWrongCardMsg = function(cardString) {
  window.alert("Bad card choice '" + cardString + "'. Please try again.");
};

/**
 * Display the human hand.
 */
View.prototype.displayHumanHand = function(hand) {
  // This implementation removes all card images from the div
  // and recreates it using images of cards from the current hand.
  // Not very efficient, but simple and it looks fine to the user.
  var yourDiv = document.getElementById("yourHand");
  while (yourDiv.childNodes.length > 0) {
    // this.yourDiv.childNodes[0].style.display = "none";
    yourDiv.removeChild(yourDiv.childNodes[0]);
  }
  for (var i=0; i<hand.length; i++) {
    this.addCardImage(hand[i], yourDiv, this.cardClickHandler);
  }
};

/**
 * Display the suit picker.
 */
View.prototype.displaySuitPicker = function() {
   var suitPickerDiv = document.getElementById("suitPicker");
   suitPickerDiv.style.display = "block";
};

/**
 * Undisplay the suit picker.
 */
View.prototype.undisplaySuitPicker = function() {
  var suitPickerDiv = document.getElementById("suitPicker");
  suitPickerDiv.style.display = "none";
};

/**
 * Announce that human has won.
 */
View.prototype.announceHumanWinner = function() {
      window.alert("Like, congrats 'n' 'at...");
};

/**
 * Announce that I have won.
 */
View.prototype.announceComputerWinner = function() {
      window.alert("Oh yeah!!  I am a WINNER and you are a, well, non-winner.");};



/** Internal function for adding this card image to the given div (human's hand)
 *  along with a click listener.
 */
View.prototype.addCardImage = function(card, aDiv, listener) {
  this.addImageHelper(card, aDiv, card.getURL(), listener);
};

/** Internal function for adding back of card to the given
 *  div (computer's hand). */
View.prototype.addBackCardImage = function(card, aDiv) {
  this.addImageHelper(card, aDiv, card.getBackURL());
};

/**
 * Helper function for previous two functions.
 */
View.prototype.addImageHelper = function(card, aDiv, URL, listener) {

  var cardPos = aDiv.childNodes.length; // position of this card within div
  var newImg = document.createElement("img");
  aDiv.appendChild(newImg);
  newImg.setAttribute("src", URL);
  newImg.setAttribute("alt", card.toString());
  newImg.style.setProperty("position", "absolute", "");
  newImg.style.setProperty("left", (cardPos * card.pixelOffset) + "px", "");
  newImg.style.setProperty("top", "0", "");
  // each card is "on top of" the previous cards
  newImg.style.setProperty("z-index", String(cardPos+1), "");
  newImg.style.setProperty("width", card.width + "px", "");
  newImg.style.setProperty("height", card.height + "px", "");

  // If onclick listener supplied, add it to image and set cursor appropriately
  // to show that the card is clickable.
  if (listener) {
    newImg.style.setProperty("cursor", "pointer", "");
    newImg.addEventListener("click", listener, false);
  }
};

/**
 *
 * Add code for preventing clicking (stop propagation view listener on body stuff)
 * Change display when user is waiting for opponent to play.
 */
View.prototype.blockPlay = function() {
  var div = document.getElementById("blocking");
  div.style.display="block";
  var myHand = document.getElementById("myHand");
  myHand.style.setProperty("opacity", 0.5);
};

/**
 * Allow the user to play once their opponent has played.
 */
View.prototype.unblockPlay = function() {
  var div = document.getElementById("blocking");
  div.style.display="none";
  var myHand = document.getElementById("myHand");
  myHand.style.setProperty("opacity", 1);
};
