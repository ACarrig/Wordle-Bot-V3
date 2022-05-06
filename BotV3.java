import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class BotV3 {

  /**
   * Main method where the user will
   * interact/enter their guesses
   *
   * @param args - unused
   */
  public static void main(String[] args) {
    ArrayList<String> wordList = new ArrayList<>(16500);
    File file = new File("/Users/acarrig/CompSciStuff/GetWords/wordle.txt");
    Scanner scnr;

    try {

      // Add the words to an ArrayList
      scnr = new Scanner(file);
      while (scnr.hasNextLine()) {
        wordList.add(scnr.nextLine());
      }

      // Getting an inital guess
      Scanner userScnr = new Scanner(System.in);
      String guess = firstGuess(wordList);
      int guessNum = 1;
      System.out.println("Computer's first guess: " + guess);
      String input = "";

      // While loop where user will enter the results of each guess
      while (!input.equalsIgnoreCase("done")) {
        if(wordList.size() <= 1) break;

        System.out.println("Enter the results of the previous guess " + "" +
            "(eg gybbg would represent a green yellow black black green result) or \"done\"");
        input = userScnr.nextLine();
        input = input.toLowerCase(Locale.ROOT); // Have the users input be lower case letters

        // Break if user is done or all letters are green
        if (input.equals("done") || input.equals("ggggg")) break;

        // See if valid input
        if (input.length() != 5) System.out.println("Invalid input");

        // Go through input 1 char at a time
        for (int i = 0; i < 5; i++) {
          if (input.charAt(i) == 'g') { // When input[i] is 'g' ---------------- CASE 1 ------------

            // Loop through wordList and remove every word where the char at i
            // is not the same as the char at i in guess
            for (int j = wordList.size() - 1; j >= 0; j--) {
              String currWord = wordList.get(j);
              if (currWord.charAt(i) != guess.charAt(i)) {
                wordList.remove(j);
              }
            }
          } else if (input.charAt(i) == 'y') { // When input[i] is 'y' --------- CASE 2 ------------

            // Loop through wordList and make sure each word contains the letter at index i
            // but not at index i
            for (int j = wordList.size() - 1; j >= 0; j--) {
              String currWord = wordList.get(j);
              if (currWord.charAt(i) == guess.charAt(i)) { // Remove words with guess(i) at index i
                wordList.remove(j);
              }

              // Remove words that do not contain char
              if (i == 4) { // When we are examining the last slot in input
                if (!currWord.contains(guess.substring(i))) {
                  wordList.remove(j);
                }
              } else { // When we are examining any other spot in input
                if (!currWord.contains(guess.substring(i, i + 1))) {
                  wordList.remove(j);
                }
              }
            }

            // Need to check if char is elsewhere in word and if that other spot is 'g' or 'y'
            // if so, the word must contain 2 of this char
            for(int currInd = 0; currInd < guess.length(); currInd++) { // Search through guess
              if(currInd == i) continue; // Skip when same index
              if(guess.charAt(currInd) == guess.charAt(i)) { // If char is found somewhere else
                if(input.charAt(currInd) == 'g' || input.charAt(currInd) == 'y') {
                  for(int z = wordList.size() -1; z >= 0; z--) { // must be 2 or more of char
                    if(numOccur(guess.charAt(i), wordList.get(z)) < 2) {
                      wordList.remove(z);
                    }
                  }
                }
              }
            }

          } else { // When input[i] is 'b' ----------------------------------- CASE 3 -------------

            // Check if char occurs elsewhere in the word, if it does, but that spot is 'b'
            // normal case, but if that other spot is NOT 'b', treat like case 2
            boolean multipleOccur = false;
            for(int currInd = 0; currInd < 5; currInd++) { // Search through guess
              if(currInd == i) continue; // Skip when same index
              if(guess.charAt(currInd) == guess.charAt(i)) { // If char is found somewhere else
                if(input.charAt(currInd) != 'b') {
                  multipleOccur = true;
                  for(int wordInd = wordList.size() -1; wordInd >= 0; wordInd--) {
                    if(wordList.get(wordInd).charAt(i) == guess.charAt(i))
                      wordList.remove(wordInd);
                  }
                }
              }
            }

            // If char only appears once in word, remove all words containing it
            if(!multipleOccur) {
              if(i < 4) {
                String charToFind = guess.substring(i, i + 1);
                for (int wordInd = wordList.size() - 1; wordInd >= 0; wordInd--) {
                  if(wordList.get(wordInd).contains(charToFind))
                    wordList.remove(wordInd);
                }
              } else { // i == 4
                String charToFind = guess.substring(i);
                for (int wordInd = wordList.size() - 1; wordInd >= 0; wordInd--) {
                  if(wordList.get(wordInd).contains(charToFind))
                    wordList.remove(wordInd);
                }
              }
            }

          }
        }

        // Getting the next guess / ending the game if we used every allotted guess
        guess = provideGuess(wordList); // Update guess for future comparisons
        guessNum++;

        if(guessNum > 6) {
          System.out.println("All allotted guesses used, better luck next time");
          break;
        } else {
          System.out.println("Next guess: " + guess);
          System.out.println("List size: " + wordList.size());
        }
      } // end of while loop

      // Print wordList (will show remaining possible words when done)
      for (int index = 0; index < wordList.size(); index++) {
        System.out.println(wordList.get(index));
      }

    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    }
  }

  /**
   * Provide a guess to the user
   * Implementation:
   * The guess is a string from a random
   * index in the wordsList arrayList
   *
   * @param words - an arrayList of Strings containing possible words
   * @return The recommended guess as a String
   */
  public static String provideGuess(ArrayList<String> words) {
    if (words.size() == 0) return "Out of Luck";
    Random rand = new Random();
    return words.get(rand.nextInt(words.size()));
  }

  /**
   * Provide a first guess for the user
   * Conditions for a first guess:
   *    - Must contain 2 or more different vowels
   *    - Cannot have any repeat letters
   *    - Cannot have 'rare' letters; z, x, w, q, & j
   *
   * @param words - an arrayList of Strings containing possible words
   * @return The recommended first guess as a String
   */
  public static String firstGuess(ArrayList<String> words) {
    ArrayList<String> wordsCopy = new ArrayList<>(16000); // Copy that we will modify

    // Start by filtering out words that do not meet conditions
    for(int i = words.size() - 1; i >= 0; i--) {
      String curr = words.get(i);

      if(vowelCounter(curr) > 1 && !(curr.contains("z") || curr.contains("x") || curr.contains("w")
          || curr.contains("q") || curr.contains("j")) && noRepeats(curr)) {
        wordsCopy.add(curr); // If all the conditions (above) pass, add the word to copy
      }
    }

    // If valid words were added to copy, pick a random one of them to return as the first guess
    if(wordsCopy.size() != 0) {
      Random rand = new Random();
      return wordsCopy.get(rand.nextInt(wordsCopy.size()));
    }

    return "Something went wrong"; // This line should never be reached
  }

  /**
   * Counts the # of unique vowels in a word
   *    - 'y' is not treated as a vowel
   *
   * @param word - The current word we are counting vowels from
   * @return - The # of vowels in word as an int value
   */
  private static int vowelCounter(String word) {
    int numVowels = 0;
    for(int i = 0; i < word.length(); i++) {
      if(word.charAt(i) == 'a' || word.charAt(i) == 'i' || word.charAt(i) == 'u' ||
          word.charAt(i) == 'e' || word.charAt(i) == 'o') { // If current char is a vowel...

        if(i != word.length() -1) { // Checking char in the middle/start of word
          String c = word.substring(i, i+1);
          if(!word.substring(i+1).contains(c)) numVowels++; // Inc numVowels if i is unique vowel
        } else { // Checking char at the end of word
          numVowels++; // Will always be a unique vowel at this point
        }
      }
    }
    return numVowels;
  }

  /**
   * Checks if the word contains repeat letters
   *
   * @param word - Current word being checked
   * @return - true if no repeat letters in word, false otherwise
   */
  private static boolean noRepeats(String word) {
    boolean noRepeats = false;
    for(int i = 0; i < word.length(); i++) {
      if(i != word.length() -1) {
        String c = word.substring(i, i+1);
        if(!word.substring(i+1).contains(c)) noRepeats = true;
      } else {
        noRepeats = true;
      }
    }
    return noRepeats;
  }

  /**
   * Returns the number of times a char, c, occurs in String str
   *
   * @param c - char to check # of occurrences of
   * @param str - word to search for c in
   * @return - # of occurrences of c in str
   */
  private static int numOccur(char c, String str) {
    int times = 0;
    for(int i = 0; i < str.length(); i++) {
      if(str.charAt(i) == c) times++;
    }
    return times;
  }

}