# Wordle-Bot-V3
The third version of my wordle solving program

This third version contains 4 new helper methods
  which contribute to picking a smarter first
  guess, as well as to the new sub-case.

Added sub-case (in case 2) where 
  it will properly react when a guess 
  results in two of the same letters being 
  either yellow or green 
  (word must have 2+ of that letter case)

This version will also automatically end the
  game after the 6th guess, or when only
  1 or 0 possible guesses remain.

*Note: The 5-letter-words.txt file includes
possible answers for the actual wordle website,
while the wordle.txt file is any 5 letter word
and is more useful for knock-off versions
