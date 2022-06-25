package com.company;

public class Word {
	private String word;
	private String hint;

	Word(String word, String hint) {
		this.word = word;
		this.hint = hint;
	}

	public String getWord() {
		return word;
	}

	public String getHint() {
		return hint;
	}
}