package com.company;

import java.util.ArrayList;

public class Difficulty {
	private String name;
	private ArrayList<Word> words = new ArrayList<>();

	Difficulty(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Word> getWords() {
		return words;
	}

	public boolean addWord(String word, String hint) {
		if (word == null || hint == null) {
			return false;
		}
		words.add(new Word(word, hint));
		return true;
	}

	public boolean removeWord(Word word) {
		if (word == null || words.contains(word)) {
			return false;
		}
		words.remove(word);
		return true;
	}
}
