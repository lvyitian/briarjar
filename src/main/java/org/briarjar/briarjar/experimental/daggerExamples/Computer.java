package org.briarjar.briarjar.experimental.daggerExamples;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class Computer {
	private String cpu, hdd;
	private Keyboard keyboard;

	@Inject
	public Computer (@Named("CPU") String cpu,
					 @Named("HDD") String hdd,
					 Keyboard keyboard) {
		this.cpu = cpu;
		this.hdd = hdd;
		this.keyboard = keyboard;
	}

	@Override
	public String toString() {
		return "Computer (cpu: "+cpu+", hdd: "+hdd+", "+keyboard+")";
	}

	public void changeCpuToNone() {cpu = "none"; }

	public String getCpu() { return cpu; }
}
