package test.java.intest;

import static org.junit.Assert.*;

import org.junit.Test;

import application.Player;

public class TestApp {

	private Player testPlayer;
	
	public TestApp(){
		testPlayer = new Player();
		testPlayer.dbconnect();
	}
	
	@Test
	public void testDatabaseConnection() {
		assertEquals(3, testPlayer.getAllSongs().size());
	}

	
}
