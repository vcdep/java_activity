package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller implements Initializable{
	@FXML
	private Button switchUser;
	@FXML
	private Label userLabel;
	@FXML
	private ListView<String> list;
	@FXML
	private Button searchButton;
	@FXML
	private Slider hslider;
	@FXML
	private Slider vslider;
	@FXML
	private Label bottomLabel;
	@FXML
	private Label currentTimeLabel;
	@FXML
	private Label durationLabel;
	@FXML
	private Label nameLabel;
	@FXML
	private Label albumLabel;
	@FXML
	private Label singerLabel;
	@FXML
	private ImageView playButton;
	@FXML
	private ImageView icon_1;
	@FXML
	private ImageView icon_0;
	@FXML
	private ImageView icon_2;
	@FXML
	private TranslateTransition move[];
	@FXML
	private ScaleTransition resize[];
	@FXML
	private ParallelTransition parallel;
	@FXML
	private ScaleTransition goBack, comeFront;
	@FXML 
	private SequentialTransition serial;
	@FXML
	private ImageView icon_img[];
	
	private String tempTime;
	private boolean stopRequested;
	private boolean atEndOfMedia;
	private Duration duration;
	private Image image;
	private ObservableList<String> items;
	private Player player;
	private FXMLLoader loader;
	private Icon icon[];
	private int i;
	
	public void setPlayer(Player newPlayer){
		System.out.println("Initializing the Player.");
		this.player = newPlayer;
		stopRequested = false;
		vslider.valueProperty().addListener(new InvalidationListener() {
		    public void invalidated(Observable ov) {
			       if (vslider.isValueChanging()) {
			           player.mplayer.setVolume(vslider.getValue() / 100.0);
			       }
			    }
			});
		
		this.setList(player.getCurrentUser().getAllSongs());
		
		image = new Image(getClass().getResource("/resources/Dont_Wanna_Know_Remix.jpg").toString());
		icon_1.setImage(image);
		image = new Image(getClass().getResource("/resources/Dont_Wanna_Know.jpg").toString());
		icon_0.setImage(image);
		image = new Image(getClass().getResource("/resources/Sugar.jpg").toString());
		icon_2.setImage(image);
		image = new Image(getClass().getResource("/resources/play.png").toString());
		playButton.setImage(image);
		
		
		icon_img = new ImageView[3];
		icon_img[0] = icon_0;
		icon_img[1] = icon_1;
		icon_img[2] = icon_2;
		
		icon = new Icon[3];
		for(int i = 0; i<3; i++){
			icon[i] = new Icon(i,i);
		}
		
		setupPlayerMedia();

		setupTransitions();
		
		
		i = 0;
	}
	
	public void setList(ArrayList<Song> songs){
		items = FXCollections.observableArrayList();
		for(int i = 0; i<songs.size(); i++)
			items.add(songs.get(i).getName());
		this.list.setItems(items);
	}	

	@FXML
	public void onHover(MouseEvent mevent) throws Exception{
		resize[0] = new ScaleTransition();
		resize[0].setDuration(Duration.seconds(0.1));
		resize[0].setNode((Node)mevent.getSource());
		resize[0].setToX(1.1);
		resize[0].setToY(1.1);
		resize[0].play();
	}
	
	@FXML
	public void onHoverOut(MouseEvent mevent) throws Exception{
		resize[0] = new ScaleTransition();
		resize[0].setDuration(Duration.seconds(0.1));
		resize[0].setNode((Node)mevent.getSource());
		resize[0].setToX(1.0);
		resize[0].setToY(1.0);
		resize[0].play();
	}
	
	@FXML
	public void onScrolled(ScrollEvent e) throws Exception{
		
		int delY;		
		if(e.getDeltaY()>0){
			delY = 1;
		}else{
			delY = -1;
		}
//		for(int i = 0; i<3; i++){
//			if(icon[i].getState() == 0){
//				if(delY>0){
//					smallToBig(delY, icon_img[i]);
//					icon[i].setState(1);
//					bigToSmall(delY, icon_img[(i+1)%3]);
//					icon[(i+1)%3].setState(2);
//					backToBack((-1)*delY, icon_img[(i+2)%3]);
//					icon[(i+2)%3].setState(0);
//				}else if(delY<0){
//					backToBack((-1)*delY, icon_img[i]);
//					icon[i].setState(2);
//					bigToSmall(delY, icon_img[(i+1)%3]);
//					icon[(i+1)%3].setState(0);
//					smallToBig(delY, icon_img[(i+2)%3]);
//					icon[(i+2)%3].setState(1);
//				}
//				break;
//			}
//		}
		
		if(i%2==0){
			if(i==0){
//				setupTransitions();
				smallToBig(delY, icon_img[0]);
					System.out.println(icon_img[0].getLayoutX() + " : ScaleX");
			}
			else if(i==2){
//				setupTransitions();
				bigToSmall(delY, icon_img[0]);
				System.out.println(icon_img[0].getLayoutX() + " : ScaleX");


			}
			else if(i==4){
//				setupTransitions();
				backToBack((-1)*delY, icon_img[0]);
				System.out.println(icon_img[0].getLayoutX() + " : ScaleX");
				i=-2;
			}
			System.out.println("i = " + i);
		}i++;

		


//		serial.play();
		parallel.play();
	}
	
	@FXML
	public void onSwitchUserClicked(ActionEvent event) throws Exception{
		this.loader = new FXMLLoader();
		Pane root = loader.load(getClass().getResource("/resources/login.fxml").openStream());
		LoginController loginCont = (LoginController) loader.getController();
		loginCont.setPlayer(player);
		Stage window = (Stage) ((Control)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root, 800, 600);		
		window.setScene(scene);
		window.show();
	}
	@FXML
	public void onSearchButtonClicked(ActionEvent event) throws Exception{
//		this.setList(player.getCurrentUser().getAllSongs());
		System.out.println("Searching...");

	}
	@FXML
	public void onIconOneClicked(MouseEvent mevent) throws Exception{
		this.player.setMedia(this.player.media);
		System.out.println("Song Selected...");
		setupPlayerMedia();
	}
	
	@FXML
	public void onPlayButtonClicked(MouseEvent mevent) throws Exception{

		if(!player.getPlaying()){
			image = new Image(getClass().getResource("/resources/pause.png").toString());
			this.playButton.setImage(image);
			this.player.mplayer.play();;
			this.player.setPlaying(true);
			System.out.println("Playing...");
			stopRequested = false;
		} else{
		
			this.player.mplayer.pause();;
			image = new Image(getClass().getResource("/resources/play.png").toString());
			this.playButton.setImage(this.image);
			this.player.setPlaying(false);
			System.out.println("Paused");
			stopRequested = true;
		}
		
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public void setUserLabelText(String userName){
		this.userLabel.setText(userName);
	}

	protected void setupTransitions(){
		move = new TranslateTransition[3];
		for(int i = 0; i<3; i++){
			move[i] = new TranslateTransition();
		}
		resize = new ScaleTransition[3];
		for(int i = 0; i<3; i++){
			resize[i] = new ScaleTransition();
		}
				
		goBack = new ScaleTransition();
		comeFront = new ScaleTransition();
		
		serial = new SequentialTransition(goBack, move[2], comeFront);
		parallel = new ParallelTransition(move[0],resize[0], move[1], resize[1],serial);
	}
	
	protected void setupPlayerMedia(){
		player.mplayer.currentTimeProperty().addListener(new InvalidationListener() 
	      {
	          public void invalidated(Observable ov) {
	              updateValues();
	          }
	      });
		
		hslider.valueProperty().addListener(new InvalidationListener() {
		    public void invalidated(Observable ov) {
			       if (hslider.isValueChanging()) {
			    	   
			           player.mplayer.seek(duration.multiply(hslider.getValue() / 100.0));
			       }
			    }
			});

//	    player.mplayer.setOnPlaying(new Runnable() {           
//	
//				public void run() {
//					System.out.println("Running");
//				 if (stopRequested) {
//					 image = new Image("resources/pause.png");
//					 playButton.setImage(image);
//					 player.mplayer.pause();
//					 stopRequested = false;
//					 System.out.println("Playing...");
//	            } else {
//	              image = new Image(getClass().getResource("/resources/Dont_Wanna_Know_Remix.jpg").toString());
//	    			playButton.setImage(image);
//	    			player.setPlaying(false);
//	    			player.mplayer.play();
//	    			stopRequested = true;
//	    			System.out.println("Paused");
//	            }
//	        }
//	    });
//	
//	    player.mplayer.setOnPaused(new Runnable() {
//	        public void run() {
//	        	image = new Image("resources/play.png");
//				playButton.setImage(image);
//				player.setPlaying(false);
//	        }
//	    });
//	
	    player.mplayer.setOnReady(new Runnable() {
	        public void run() {
	            duration = player.mplayer.getMedia().getDuration();
	            updateValues();
	        }
	    });
	
	    player.mplayer.setOnEndOfMedia(new Runnable() {
	        public void run() {
	      	  image = new Image(getClass().getResource("/resources/play.png").toString());
				playButton.setImage(image);
	            stopRequested = true;
	            player.mplayer.seek(player.mplayer.getStartTime());
	            player.mplayer.pause();
	            player.setPlaying(false);
	        }
	   });
	}
	protected void updateValues() {
		  if (currentTimeLabel != null && hslider != null && vslider != null) {
		     Platform.runLater(new Runnable() {
		        public void run() {
		        	Duration currentTime = player.mplayer.getCurrentTime();
		        	tempTime = String.valueOf(currentTime);
		        	tempTime = tempTime.substring(0,tempTime.length()-3);
		        	int time = ((int)Double.parseDouble(tempTime))/1000; 
		        	tempTime = String.format("%02d", time/60)+":"+String.format("%02d", time%60);
		        	currentTimeLabel.setText(tempTime);

		        	hslider.setDisable(duration.isUnknown());
		        	if (!hslider.isDisabled() 
		        			&& duration.greaterThan(Duration.ZERO) 
		        			&& !hslider.isValueChanging()) {
		        				hslider.setValue(currentTime.divide(duration).toMillis()
		        						* 100.0);
		        	}
		        	if (!vslider.isValueChanging()) {
		        		vslider.setValue((int)Math.round(player.mplayer.getVolume() 
		        				* 100));
		        	}
		        }
		     });
		  }
		}
	
	public void bigToSmall(int dir, ImageView img){
		setupTransitions();
		move[0].setDuration(Duration.seconds(0.25));
		move[0].setNode(img);
		move[0].setToX(dir*175);
		move[0].setToY(5);
		resize[0].setDuration(Duration.seconds(0.25));
		resize[0].setNode(img);
		resize[0].setToX(0.6);
		resize[0].setToY(0.6);
		move[0].setInterpolator(Interpolator.EASE_IN);
	}
	
	public void smallToBig(int dir, ImageView img){
		setupTransitions();
		move[1].setDuration(Duration.seconds(0.25));
		move[1].setNode(img);
		move[1].setToX(dir*175);
		resize[1].setDuration(Duration.seconds(0.25));
		resize[1].setNode(img);
		resize[1].setToX(2);
		resize[1].setToY(2);
		move[1].setInterpolator(Interpolator.EASE_IN);
	}
	
	public void backToBack(int dir, ImageView img){
		setupTransitions();
		goBack.setDuration(Duration.seconds(0.125));
		goBack.setNode(img);
		goBack.setToX(0);
		goBack.setToY(0);
		move[2].setNode(img);
		move[2].setDuration(Duration.seconds(0.0001));
		move[2].setToX(360*dir);
		comeFront.setDuration(Duration.seconds(0.125));
		comeFront.setNode(img);
		comeFront.setToX(1);
		comeFront.setToY(1);
	}
	
	
}
