package application;

public class Icon {
	private int ID;
	private int state;
	
	public Icon(int id, int state){
		ID = id;
		this.state = state;		
	}
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
}
