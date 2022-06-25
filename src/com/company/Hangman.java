package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Hangman {
	private final JFrame frame = new JFrame("Hangman");
	private JPanel rootPanel;
	private JPanel primaryPanel;
	private JTextField guessField;
	private JLabel currentWordLabel;
	private JButton checkBtn;

	private JPanel Difficulty;
	private JRadioButton easyRadioButton;
	private JRadioButton mediumRadioButton;
	private JRadioButton hardRadioButton;
	private JButton changeDifficultyButton;
	private JLabel hintLabel;
	private JLabel hangManLabel;
	private ButtonGroup difficultyGroup;

	private String difficulty = null;
	private Word selectedWord;
	private String guessWord;
	private int randomIdx = -1;

	// This is set to 3 because we have 3+1 set of images for the Hangman itself
	final int MAX_WRONGS = 3;
	private int wrongs = 0;

	final int MAX_DIFFICULTIES = 3;
	private Difficulty[] difficulties = new Difficulty[MAX_DIFFICULTIES];

	public ArrayList<Word> getDifficultyWords(String name) {
		for (Difficulty diff: difficulties) {
			if (diff.getName().equals(name)) {
				return diff.getWords();
			}
		}
		return null;
	}

	void loadDifficulties() {
		difficulties[0] = new Difficulty("Easy");
		difficulties[0].addWord("apple", "fruit");
		difficulties[0].addWord("banana", "fruit");
		difficulties[0].addWord("orange", "fruit/colour");
		difficulties[0].addWord("chicago", "city");
		difficulties[0].addWord("london", "city");
		difficulties[0].addWord("mcdonalds", "restaurant");
		difficulties[0].addWord("pop", "music genre");

		difficulties[1] = new Difficulty("Medium");
		difficulties[1].addWord("shakespeare", "writer");
		difficulties[1].addWord("darwin", "scientist");
		difficulties[1].addWord("mississippi", "location");
		difficulties[1].addWord("eerie", "adjective");
		difficulties[1].addWord("elephant", "animals");

		difficulties[2] = new Difficulty("Hard");
		difficulties[2].addWord("jazz", "music genre");
		difficulties[2].addWord("fox",  "animal");
		difficulties[2].addWord("matchbox", "object");
		difficulties[2].addWord("bubbliest", "adjective");
		difficulties[2].addWord("mummifying", "process");

		System.out.println("---");
		for (Difficulty value : difficulties) {
			System.out.println("Difficulty level: " + value.getName() + " - Words: " + value.getWords().size());
		}
		System.out.println("---");
	}

	Hangman() {
		loadDifficulties();

		// Get the screen size, and perform some calculation for ideal size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Get Users Screen Size
		if (screenSize.getWidth() > 800 && screenSize.getHeight() > 600) {
			screenSize.setSize(screenSize.getWidth() / 1.5, screenSize.getHeight() / 1.5);
		}

		// Initialise our window
		frame.setMinimumSize(screenSize);
		frame.setContentPane(rootPanel);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

		hangManLabel.setVisible(false);
		hintLabel.setVisible(false);
		guessField.setVisible(false);

		// Action Listener for 'Check Button' and 'Guess Text Field',
		// User can either use the button, or press 'Enter' key
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String guess = guessField.getText();
				checkGuess(guess);
			}
		};

		// Check Button
		checkBtn.addActionListener(action);

		// Guess Text Field
		guessField.addActionListener(action);

		// Change Difficulty Button
		changeDifficultyButton.addActionListener(e -> {
			if (easyRadioButton.isSelected()) {
				difficulty = "Easy";
			} else if (mediumRadioButton.isSelected()) {
				difficulty = "Medium";
			} else if (hardRadioButton.isSelected()) {
				difficulty = "Hard";
			} else {
				JOptionPane.showMessageDialog(null, "Please select a difficulty level first!");
				return;
			}
			System.out.println("Refreshing word...");
			randomIdx = -1;
			selectWord();
			guessField.setVisible(true);
		});
	}

	// Recursion go brrr
	private int selectRandom() {
		int temp = new Random().nextInt(getDifficultyWords(difficulty).size());
		if (temp == randomIdx) {
			return selectRandom();
		}
		return temp;
	}

	void selectWord() {
		randomIdx = selectRandom();

		selectedWord = getDifficultyWords(difficulty).get(randomIdx);
		setGuessWord(getCensoredWord());

		checkGuess(String.valueOf(selectedWord.getWord().charAt(new Random().nextInt(selectedWord.getWord().length()))));
		System.out.println("Word: " + selectedWord.getWord());

		hangManLabel.setVisible(true);
		updateHangman();
	}

	void updateHangman() {
		hangManLabel.setIcon(new ImageIcon(System.getProperty("user.dir") + "\\hangman_" + wrongs + ".png"));
	}

	private void checkGuess(String guess) {
		String message = null;
		guess = guess.toLowerCase(Locale.ENGLISH);

		if (difficulty == null) {
			message = "Please select a difficulty level first.";
		} else if (guess.length() != 1) {
			message = "Please enter one character at a time.";
		} else if (!guess.matches("^[A-Za-z]*$")) {
			message = "Please enter alphabets (A-Z) only.";
		} else if (guessWord.contains(guess)) {
			message = "Already guessed or present as a hint";
		} else {
			boolean goodGuess = false;
			for (int i = 0; i < selectedWord.getWord().length(); i++) {
				if (guess.equals(Character.toString(selectedWord.getWord().charAt(i)))) {
					goodGuess = true;

					StringBuilder newWord = new StringBuilder(guessWord);
					newWord.setCharAt(i, selectedWord.getWord().charAt(i));
					setGuessWord(newWord.toString());
				}   
			}

			if (!goodGuess) {
				message = "Incorrect guess!";
				wrongs ++;
				updateHangman();

				if (wrongs == MAX_WRONGS) {
					promptAgain("lost");
					return;
				}
			}
		}

		guessField.setText("");
		if (message != null) {
			JOptionPane.showMessageDialog(null, message);
		} else if (guessWord.equals(selectedWord.getWord())) {
			promptAgain("won");
			wrongs = 0;
		}
	}

	public void promptAgain(String word) {
		if (JOptionPane.showConfirmDialog(null, "You " + word + "!  Do you want to continue and play more?", frame.getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			new Hangman();
		}
		frame.dispose();
	}

	private String getCensoredWord() {
		String word = selectedWord.getWord();
		return "" + "-".repeat(word.length());
	}

	private void setGuessWord(String word) {
		guessWord = word;
		currentWordLabel.setText(word);

		hintLabel.setVisible(!difficulty.equals("Hard"));
		hintLabel.setText("Hint: " + selectedWord.getHint());
	}
}
