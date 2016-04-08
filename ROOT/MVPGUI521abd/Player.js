"use strict";

/**
 * Base class for players.  Initializes player's hand
 * and determines whether or not the player's hand is empty.
 */
function Player(deck) {
  /**
   * This player's hand.
   */
  this.list = new Array();
  
  /**
   * Get seven cards from the deck and store them in this hand.
   */
  for (var i=1; i<=7; i++) {
    this.list.push(deck.dealACard());
  } 
}

Player.prototype = {
  /**
   * Return true when this hand is empty.
   */
  isHandEmpty: function() {  
    return this.list.length == 0;
  },

  /**
   * Add the given Card object to this player's hand.
   */
  add: function(card) {
    this.list.push(card);
  },

  /**
   * Remove the card at the specified position (0-based) in
   * this player's hand.
   */
  remove: function(i) {
    this.list.splice(i,1);
  },

  /**
   * Given the string specification of a card,
   * return the card if it is in this player's hand
   * or return null.
   */
  find: function(cardString) {
    var i = 0;
    var card = null;
    while (i<this.list.length && !card) {
      if (this.list[i].toString() == cardString) {
        card = this.list[i];
      }
      i++;
    }
    return card;
  },

  /**
   * Return index of given Card object, or -1 if card not in hand.
   */
  indexOf: function(card) {
    return this.list.indexOf(card);
  },

  /**
   * Return copy of this player's hand (array of Card objects).
   * Changes to the returned array will not affect the Player's hand.
   */
  getHandCopy: function() {
    return this.list.slice(0);
  },

};  
