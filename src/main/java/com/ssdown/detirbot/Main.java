package com.ssdown.detirbot;

public class Main {
	public static void main(String[] args) {
		try {
			DetirBot.detirBot = new DetirBot();
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.exit(Constants.EXIT_CODE_NORMAL);
		}

	}
}
