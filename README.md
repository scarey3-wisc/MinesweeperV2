Contains the Eclipse build tree for Stephen's Minesweeper AI (an AI that determines where bombs are, which spaces are safe, and guesses
to minimize risk). 

To run, open the command prompt in the root directory and type (without quotations) "java -cp bin Sweepin"

The AI will repeatedly restart the game until the user exits the program. Locations with a green plus are locations that the AI has
determined to be safe. The AI is also playing with a 100ms delay between moves to let the user see what the AI is doing.

Minimal modifications to the code could allow a user to play Minesweeper too, but the challenge of this project was more 
the AI than the user; the code enabling user play exists primarily for testing purposes.
