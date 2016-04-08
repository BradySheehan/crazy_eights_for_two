
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import javax.imageio.ImageIO;

import java.awt.GraphicsEnvironment;

/** Generate playing card images */

public class CardGen {

    public static void main(String[] args) {
	Color borderColor = Color.black;
	Color bgColor = Color.WHITE;

	// Card size
	int width = 71; // pixels
	int height = 96;

	// Suits: clubs, diamonds, hearts, spades
	String[] suitNames = {"c", "d", "h", "s"};
	String[] suits = {"\u2663", "\u2666", "\u2665", "\u2660"};
	// Would use these if I could get Symbol font to work...
	//	String[] suits = {"\u00A7", "\u00A8", "\u00A9", "\u00AA"};
	Color[] colors = {Color.black, Color.red, Color.red, Color.black};
	// card names
	String[] names = {"2", "3", "4", "5", "6", "7", "8", "9",
			  "10", "J", "Q", "K", "A"};

	//	String font_file = "C:\\WINDOWS\\Fonts\\courbd.ttf";
	String charFontFile = "C:\\WINDOWS\\Fonts\\timesbd.ttf";
	String symFontFile = "C:\\WINDOWS\\Fonts\\symbol.ttf";
	float fontSizeSmaller = 14.0f;
	float fontSizeSmall = 16.0f;
	float fontSizeLarge = 20.0f;

	try {

	    /* Code for obtaining system names for fonts:
	      String[] fonts = 
	      GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	      for (int i=0; i<fonts.length; i++) {
	      System.out.println(fonts[i]);
	      }
	    */

	    /* Alternative way to make fonts:
	    Font fontChar = Font.createFont(Font.TRUETYPE_FONT,
					    new FileInputStream(charFontFile));
	    Font fontSmaller = fontChar.deriveFont(fontSizeSmaller);
	    Font fontSmall = fontChar.deriveFont(fontSizeSmall);
	    Font fontLarge = fontChar.deriveFont(fontSizeLarge);
	    */

	    Font fontSmaller = Font.decode("Courier New-BOLD-12");
	    Font fontSmall = Font.decode("Courier New-BOLD-16");
	    Font symSmall =  Font.decode("Courier New-BOLD-16");
	    Font symLarge = Font.decode("Courier New-BOLD-24");

	    BufferedImage buffer =
		new BufferedImage(width,
				  height,
				  BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = buffer.createGraphics();

	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			       RenderingHints.VALUE_ANTIALIAS_ON);

	    for (int name=0; name<names.length; name++) {

		String cardName = names[name];

		for (int suit=0; suit<suits.length; suit++) {

		    String cardSuit = suits[suit];

		    // Black background (will be border)
		    g.setBackground(borderColor);
		    g.clearRect(0,0,width,height);
		    // White card inside background
		    g.setColor(bgColor);
		    g.fillRect(1,1,width-2,height-2);
		    // Draw in suit color
		    g.setColor(colors[suit]);

		    AffineTransform saveTransform = null;

		    // Draw top of card, rotate card, and draw bottom
		    // (two passes, first for top, then for bottom)
		    for (int pass=1; pass<=2; pass++) {

			// Upper left corner
			if (cardName.equals("10")) {
			    g.setFont(fontSmaller);
			    g.drawString(cardName, 1, 12);
			}
			else {
			    g.setFont(fontSmall);
			    g.drawString(cardName, 2, 12);
			}
			g.setFont(symSmall);
			g.drawString(cardSuit, 2, 24);
			
			// Upper half of card
			g.setFont(symLarge);
			if (cardName.equals("2") ||
			    cardName.equals("3")) {
			    g.drawString(cardSuit, 28, 24);
			}
			else if (cardName.equals("4") ||
				 cardName.equals("5") ||
				 cardName.equals("6") ||
				 cardName.equals("7") ||
				 cardName.equals("8") ||
				 cardName.equals("9") ||
				 cardName.equals("10")) {

			    g.drawString(cardSuit, 14, 24);
			    g.drawString(cardSuit, 42, 24);

			    if (cardName.equals("8") ||
				cardName.equals("9") ||
				cardName.equals("10")) {
				g.drawString(cardSuit, 14, 44);
				g.drawString(cardSuit, 42, 44);
				if (cardName.equals("10")) {
				    g.drawString(cardSuit, 28, 34);
				}
			    }
			}

			// Do pass-specific processing
			if (pass == 1) {

			    // Some pips are only drawn one way
			    if (cardName.equals("A") ||
				cardName.equals("3") ||
				cardName.equals("5") ||
				cardName.equals("9")) {
				g.drawString(cardSuit, 28, 54);
			    }
			    else if (cardName.equals("6") ||
				     cardName.equals("7")) {
				g.drawString(cardSuit, 14, 54);
				g.drawString(cardSuit, 42, 54);
				if (cardName.equals("7")) {
				    g.drawString(cardSuit, 28, 39);
				}
			    }
			    else if (cardName.equals("J") ||
				     cardName.equals("Q") ||
				     cardName.equals("K")) {
				g.draw(new Rectangle(14, 14, 
						     width-28, height-28));
			    }

			    // Draw the lower half next time
			    saveTransform = g.getTransform();
			    g.rotate(Math.PI);
			    g.translate(-width, -height);
			}
			else {
			    // Restore to normal drawing
			    g.setTransform(saveTransform);
			}
		    }
		    
		    File imageFile = new File(names[name].toLowerCase() +
					      suitNames[suit] +
					      ".png");
		    ImageIO.write(buffer, "png", imageFile);
		}
	    }
	}
	catch (Exception e) {
	    e.printStackTrace();
	}

	return;
    }
}
