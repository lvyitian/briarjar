package org.briarjar.briarjar.daggerExperiments;

import javax.inject.Inject;

public class Keyboard {
	private boolean numPad;
	private String layout;

	@Inject
	public Keyboard(boolean numPad, String layout) {
		this.numPad = numPad;
		this.layout = layout;
	}

	public String toString() {
		return "Keyboard (numPad: "+numPad+", layout: "+layout+")";
	}

	public void changeNumPadToFalse() {
		numPad = false;
	}
}
