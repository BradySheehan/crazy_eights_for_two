"use strict";

/**
 * The human player's hand.
 */
function HumanPlayer(deck) {
  // super(deck);
  Player.call(this, deck);
}
HumanPlayer.prototype = Object.create(Player.prototype);
