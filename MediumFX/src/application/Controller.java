package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
	private TranslateTransition move;
	@FXML
	private ScaleTransition resize;
	@FXML
	private ParallelTransition parallel;
	
	
	private String tempTime;
	private boolean stopRequested;
	private boolean atEndOfMedia;
	private Duration duration;
	private Image image;
	private ObservableList<String> items;
	private Player player;
	private FXMLLoader loader;
	
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
		
		setupPlayerMedia();
	}
	
	public void setList(ArrayList<Song> songs){
		items = FXCollections.observableArrayList();
		for(int i = 0; i<songs.size(); i++)
			items.add(songs.get(i).getName());
		this.list.setItems(items);
	}	

	@FXML
	public void onHover(MouseEvent mevent) throws Exception{
		
	}
	
	@FXML
	public void onScrolled(ScrollEvent e) throws Exception{
		System.out.println(e.getDeltaY());
		int delY;
		if(e.getDeltaY()>0){
			delY = 1;
		}else{
			delY = -1;
		}
		move = new TranslateTransition();
		resize = new ScaleTransition();
		parallel = new ParallelTransition(move,resize);
		move.setDuration(Duration.seconds(0.25));
		move.setNode(this.icon_1);
		move.setToX(delY*160);
		move.setToY(5);
		resize.setDuration(Duration.seconds(0.25));
		resize.setNode(this.icon_1);
		resize.setToX(0.6);
		resize.setToY(0.6);
		move.setInterpolator(Interpolator.EASE_IN);
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
	
}
