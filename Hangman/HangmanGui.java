import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

public class HangmanGui extends JFrame implements ActionListener {
    int guessCount = 7;
    String word;
    String initial;
    char[] incorrectGuesses = new char[7];
    JTextField guessBox;
    JButton guessButton;
    JLabel wrongInput;
    JLabel initialSet;
    String incorrectLetters;
    JLabel result;
    JLabel guessLabel;
    JLabel incorrect;
    JButton restartButton;
    char[] wordToArray;
    char[] initialToArray;

    HangmanGui() throws FileNotFoundException {
        super("Hangman");
        this.setSize(600,600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        Main.readList();

        //guess
        JLabel guess = new JLabel("Guess:");
        guessBox = new JTextField(1);
        g.fill = GridBagConstraints.BOTH;
        g.gridx = 0;
        g.gridy = 0;
        this.add(guess,g);
        g.gridx = 1;
        this.add(guessBox,g);

        //guess button
        guessButton = new JButton("Guess");
        g.fill = GridBagConstraints.BOTH;
        g.gridx = 2;
        g.gridy = 0;
        guessButton.addActionListener(this);
        this.add(guessButton,g);

        //words
        word = Main.getWord();
        initial = "_ ".repeat(word.length());
        wordToArray = word.toCharArray();
        initialToArray = initial.toCharArray();

        //initial set up
        initialSet = new JLabel(initial);
        g.fill = GridBagConstraints.BOTH;
        initialSet.setFont(new Font("Arial",Font.BOLD,10));
        g.gridx = 0;
        g.gridy = 1;
        this.add(initialSet,g);

        //guess counter
        guessLabel = new JLabel("Guesses Remaining: " + guessCount);
        g.fill = GridBagConstraints.BOTH;
        g.gridx = 0;
        g.gridy = 2;
        this.add(guessLabel,g);

        //incorrect guesses
        incorrect = new JLabel("Incorrect Guesses: ");
        g.fill = GridBagConstraints.BOTH;
        g.gridx = 0;
        g.gridy = 3;
        this.add(incorrect,g);

        //incorrect info entered statement
        wrongInput = new JLabel("Enter 1 letter at a time");
        g.fill = GridBagConstraints.BOTH;
        g.gridx = 0;
        g.gridy = 4;
        wrongInput.setPreferredSize(new Dimension(200,20));
        this.add(wrongInput,g);

        //restart button
        restartButton = new JButton("Restart");
        g.fill = GridBagConstraints.BOTH;
        g.gridx = 0;
        g.gridy = 5;
        restartButton.addActionListener(this);
        this.add(restartButton, g);

        //result
        result = new JLabel();
        g.fill = GridBagConstraints.BOTH;
        g.gridx = 0;
        g.gridy = 6;
        result.setPreferredSize(new Dimension(200,20));
        this.add(result,g);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == guessButton) {
            checkLetter();
            incorrectGuessFormat();
            gameResult();
        } else if (e.getSource() == restartButton) {
            restartGame();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new HangmanGui();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void checkLetter() {
        // Check if more than 1 letter or non-letter entered
        String guess = guessBox.getText().toLowerCase();
        if (guess.length() != 1 || !Character.isLetter(guess.charAt(0))) {
            wrongInput.setText("Please enter a single letter");
            return;
        }

        // Convert entered String to a character array
        char[] letterGuessed = guess.toCharArray();
        boolean correctLetter = false;

        // Check if the correct letter has been guessed before
        boolean alreadyGuessed = false;
        for (char c : initialToArray) {
            if (c == letterGuessed[0]) {
                alreadyGuessed = true;
                break;
            }
        }

        // Check if the letter entered is in the word
        for (int i = 0; i < wordToArray.length; i++) {
            if (Character.toLowerCase(wordToArray[i]) == letterGuessed[0]) {
                initialToArray[i * 2] = letterGuessed[0];
                correctLetter = true;
            }
        }

        // Conditionals for correct, incorrect and guessed letters
        if (!correctLetter) {
            for (char c : incorrectGuesses) {
                if (c == letterGuessed[0]) {
                    alreadyGuessed = true;
                    wrongInput.setText("Already guessed. Try again!");
                    break;
                }
            }

            if (!alreadyGuessed) {
                guessCount--;
                guessLabel.setText("Guesses Remaining: " + guessCount);
                for (int i = 0; i < incorrectGuesses.length; i++) {
                    if (incorrectGuesses[i] == '\u0000') {
                        incorrectGuesses[i] = letterGuessed[0];
                        wrongInput.setText("Incorrect letter. Try again!");
                        break;
                    }
                }
            }
        } else if (!alreadyGuessed) {
            initial = new String(initialToArray);
            initialSet.setText(initial);
            wrongInput.setText("Correct letter. Continue!!");
        } else {
            wrongInput.setText("Already guessed. Try again!");
        }
    }

    private void gameResult(){
        if(guessCount == 0){
            result.setText("YOU LOSE!!");
            wrongInput.setText("The word was: " + word);
            disableInput();
            return;
        }

        //check if '_' is in array to determine if all letters have been guessed
        boolean allLettersGuessed = true;
        for (char c : initialToArray) {
            if (c == '_') {
                allLettersGuessed = false;
                break;
            }
        }

        if (allLettersGuessed) {
            result.setText("YOU WIN!!");
            disableInput();
        }
    }

    private void incorrectGuessFormat(){
        StringBuilder s = new StringBuilder();

        for (char incorrectGuess : incorrectGuesses) {
            if (incorrectGuess != '\u0000') {
                s.append(incorrectGuess).append(", ");
            }
        }

        // Remove the trailing comma and space if there are any incorrect guesses
        if (!s.isEmpty()) {
            s.delete(s.length() - 2, s.length());
        }

        incorrectLetters = s.toString();
        incorrect.setText("Incorrect Guesses: " + incorrectLetters);
    }

    private void disableInput() {
        guessBox.setEnabled(false);
        guessButton.setEnabled(false);
    }

    private void restartGame() {
        guessCount = 7;
        word = Main.getWord();
        initial = "_ ".repeat(word.length());
        wordToArray = word.toCharArray();
        initialToArray = initial.toCharArray();
        incorrectGuesses = new char[7];
        initialSet.setText(initial);
        guessLabel.setText("Guesses Remaining: " + guessCount);
        incorrect.setText("Incorrect Guesses: ");
        result.setText("");
        wrongInput.setText("Enter 1 letter at a time");
        guessBox.setEnabled(true);
        guessButton.setEnabled(true);
    }
}