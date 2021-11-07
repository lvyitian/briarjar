package org.briarjar.briarjar.experimental.daggerExamples;

public class Main {
	public static void main(String[] args) {

		ItComponent itc = DaggerItComponent.create();
		Computer c1 = itc.getComputer();


		/* @Singleton vs not @Singleton example, when altering properties */

		System.out.println("\n\nComputer IS @Singleton:\n");
		System.out.println("itc.getCpu():\t"+itc.getCpu());
		System.out.println("c1.getCpu():\t"+c1.getCpu());
		System.out.println("c1.changeCpuToNone()...");
		c1.changeCpuToNone();
		System.out.println("itc.getComputer().getCpu(): "+itc.getComputer().getCpu()+
				"  <-- there's only 1 instantiated COMPUTER");
		System.out.println("itc.getCpu():\t"+itc.getCpu());
		System.out.println("c1.getCpu():\t"+c1.getCpu());


		System.out.println("\n\nKeyboard is NOT @Singleton:\n");
		System.out.println("itc:\t\t"+itc.getKeyboard());
		Keyboard kb = itc.getKeyboard();
		System.out.println("kb.changeNumPadToFalse()...");
		kb.changeNumPadToFalse();
		System.out.println("itc:\t\t"+itc.getKeyboard());
		System.out.println("kb :\t\t"+kb);
	}
}
