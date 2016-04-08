"use strict";

/**
 * The computer player's hand.
 */
function ComputerPlayer(deck) {
  // super(deck);
  Player.call(this, deck);
}
ComputerPlayer.prototype = Object.create(Player.prototype);
