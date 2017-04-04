package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
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
	@FXML
	private VBox theBox;
	@FXML
	private TextField searchBar;
	@FXML
	EventHandler<?super ScrollEvent> handle;
	@FXML
	EventHandler<?super KeyEvent> arrow;
	
	
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
	private Thread t1;
	private int count;
	
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
		
		this.searchBar.setOnKeyPressed(event->{
			try{
				if(event.getCode()==KeyCode.RIGHT){
					System.out.println("Right");
					i=(i+1)%3;
					move(-1,i);
					parallel.play();
				}
				if(event.getCode()==KeyCode.LEFT){
					System.out.println("Left");
					i=(i+2)%3;
					move(1,i);
					parallel.play();
				}
				if(event.getCode()==KeyCode.ENTER){
					play();
				}
				
			}catch(Exception e){}
		});
		
		image = new Image(getClass().getResource("/resources/Dont_Wanna_Know_Remix.jpg").toString());
		icon_1.setImage(image);
		image = new Image(getClass().getResource("/resources/Sugar.jpg").toString());
		icon_0.setImage(image);
		image = new Image(getClass().getResource("/resources/Honey.jpg").toString());
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
		
	    this.nameLabel.setText(player.getAllSongs().get(0).getName());
	    this.albumLabel.setText(player.getAllSongs().get(0).getAlbum());
	    this.singerLabel.setText(player.getAllSongs().get(0).getSinger());
		String in = getClass().getResource(player.getAllSongs().get(0).getLocation()).toString();
		player.media = new Media(in);
		player.mplayer = new MediaPlayer(player.media);

		setupPlayerMedia();

		setupTransitions();
		
		handle = theBox.getOnScroll();
		arrow = searchBar.getOnKeyPressed();
		count = 0;
		i = -1;
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
		Node node = (Node)mevent.getSource();
		resize[0].setNode(node);
		resize[0].setToX(node.getScaleX()*1.1);
		resize[0].setToY(node.getScaleY()*1.1);
		resize[0].play();
	}
	
	@FXML
	public void onHoverOut(MouseEvent mevent) throws Exception{
		resize[0] = new ScaleTransition();
		resize[0].setDuration(Duration.seconds(0.1));
		Node node = (Node)mevent.getSource();
		resize[0].setNode(node);
		resize[0].setToX(node.getScaleX()/1.1);
		resize[0].setToY(node.getScaleY()/1.1);
		resize[0].play();
	}
	
	@FXML
	public void onScrolled(ScrollEvent e) throws Exception{
		
		int delY;		
		if(e.getDeltaY()>0){
			i=(i+1)%3;
			delY = 1;
		}else{
			delY = -1;
		}
		System.out.println("dir: " + delY);
		
		System.out.println(icon[0] + " | " + icon[1] + " | " + icon[2] + " count = " + this.count);
		
		i=(i+1)%3;

		move(delY, i);

		System.out.println("Done");
//		theBox.setOnScroll(handle);
	}
	
	public void setThread(){
		t1 = new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("Started");
					Thread.sleep(250);
					theBox.setOnScroll(handle);
					searchBar.setOnKeyPressed(arrow);
				} catch (InterruptedException e) {}
			}
		});
	}
	
	public void move(int delY, int i){
		setupTransitions();
		if(delY>0){
			if(icon[i].getState()==0){
				count = (count + 1)%player.getAllSongs().size();
//				setupTransitions();
				smallToBig(delY, icon_img[i]);
				bigToSmall(delY, icon_img[(i+1)%3]);
				backToBack(delY, icon_img[(i+2)%3]);
				icon[i].setState(1);
				icon[(i+1)%3].setState(2);
				icon[(i+2)%3].setState(0);
			}
		}else{
			if(icon[i].getState()==0){
				count = (count + player.getAllSongs().size() -1)%player.getAllSongs().size();
//				setupTransitions();
				backToBack(delY, icon_img[i]);
				bigToSmall(delY, icon_img[(i+1)%3]);
				smallToBig(delY, icon_img[(i+2)%3]);
				icon[i].setState(2);
				icon[(i+1)%3].setState(0);
				icon[(i+2)%3].setState(1);
			}
		}
		theBox.setOnScroll(null);
		searchBar.setOnKeyPressed(null);
		parallel.play();
		setThread();
		t1.start();
		Song song = player.getAllSongs().get(count);
		this.nameLabel.setText(song.getName());
		this.albumLabel.setText(song.getAlbum());
		this.singerLabel.setText(song.getSinger());
		String in = getClass().getResource(song.getLocation()).toString();
		player.mplayer.stop();
		image = new Image(getClass().getResource("/resources/play.png").toString());
		this.playButton.setImage(this.image);
		this.player.setPlaying(false);
		
		player.media = new Media(in);
		player.mplayer = new MediaPlayer(player.media);
		setupPlayerMedia();

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
		System.out.println("x= " + mevent.getX());
		setupPlayerMedia();
	}

	@FXML
	public void onPlayButtonClicked(MouseEvent mevent) throws Exception{

		play();
		
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
		move[0].setDuration(Duration.seconds(0.25));
		move[0].setNode(img);
		move[0].setToX(img.getTranslateX()+dir*175);
		move[0].setToY(5);
		resize[0].setDuration(Duration.seconds(0.25));
		resize[0].setNode(img);
		resize[0].setToX(img.getScaleX()*0.5);
		resize[0].setToY(img.getScaleY()*0.5);
		move[0].setInterpolator(Interpolator.EASE_OUT);
	}
	
	public void smallToBig(int dir, ImageView img){
		move[1].setDuration(Duration.seconds(0.25));
		move[1].setNode(img);
		move[1].setToX(img.getTranslateX()+dir*175);
		resize[1].setDuration(Duration.seconds(0.25));
		resize[1].setNode(img);
		resize[1].setToX(img.getScaleX()*2);
		resize[1].setToY(img.getScaleY()*2);
		move[1].setInterpolator(Interpolator.EASE_OUT);
	}
	
	public void backToBack(int dir, ImageView img){
		goBack.setDuration(Duration.seconds(0.125));
		goBack.setNode(img);
		goBack.setToX(0);
		goBack.setToY(0);
		move[2].setNode(img);
		move[2].setDuration(Duration.seconds(0.0001));
		move[2].setToX(img.getTranslateX()-350*dir);
		comeFront.setDuration(Duration.seconds(0.125));
		comeFront.setNode(img);
		comeFront.setToX(img.getScaleX()*1);
		comeFront.setToY(img.getScaleY()*1);
	}

	public void play(){
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

	
	
}
