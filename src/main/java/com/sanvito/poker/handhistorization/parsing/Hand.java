package com.sanvito.poker.handhistorization.parsing;

public class Hand {
	private String holeCards;
	public String getHoleCards() {
		return holeCards;
	}

	public void setHoleCards(String holeCards) {
		this.holeCards = holeCards;
	}

	public String getFlopAction() {
		return flopAction;
	}

	public void setFlopAction(String flopAction) {
		this.flopAction = flopAction;
	}

	private String flopAction;

	public Hand(String holeCards, String flopAction) {
		this.flopAction = flopAction;
		this.flopAction = flopAction;
	}

}
