package com.ssdown.detirbot;

class ExtendClass {
	public String status;

	public void Print() {
		System.out.println("나는 " + this.status + "임");
	}
}

class BabyClass extends ExtendClass {
	String ace;
	public void Print() {
		System.out.println("나는 사실 " + this.status + "임");
	}
}

class DetirbotApplicationTests {
	public static void main(String[] args) {
		BabyClass babyClass = new BabyClass();

		babyClass.status = "천재";

		babyClass.Print();
	}
}
