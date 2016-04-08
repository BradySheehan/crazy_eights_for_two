"use strict";

/**
 * Discard pile of cards.
 */
function Pile() {
  /** List of cards on the pile. */
  // For simplicity, 0 is considered the top card of the pile.
  this.list = new Array();
  
  /** If an 8 is played, this is the announced suit preference. */
  this.announcedSuit = "";
}
Pile.prototype = {
  /**
   * Return true if the given card can be legally played on the
   * current pile.
   */
  isValidToPlay: function (card) {
    var retVal = false;
    var topCard = this.getTopCard();

    if (card.getValue() == "8") {
      retVal = true;
    }
    else if (topCard.getValue() == "8") {
      retVal = (card.getSuit() == this.announcedSuit);
    }
    else if (card.getSuit() == topCard.getSuit()
               || 
             card.getValue() == topCard.getValue()) {
      retVal = true;
    }
    return retVal;
  },
  
  /**
   * Accept a card and make it the new top of the discard pile.
   */
  acceptACard: function (card) {
    this.list.splice(0, 0, card);
  },
  /**
   * Remember the suit preference announced when the most recent
   * 8 was played.
   */ 
  setAnnouncedSuit: function (suit) {
    this.announcedSuit = suit;
  },
  /**
   * Return the card that is on top of the pile.  The card is not removed.
   */
  getTopCard: function () {
    return this.list[0];
  }
};
