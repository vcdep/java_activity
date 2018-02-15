package test.java.integration;

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
		assertEquals(2, testPlayer.getAllSongs().size());
	}

	
}
