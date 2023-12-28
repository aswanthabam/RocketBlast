package com.abam.rocketBlast;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.*;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.scenes.scene2d.*;
//import android.util.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.assets.loaders.*;
import com.badlogic.gdx.audio.*;
//import android.view.*;
//import com.badlogic.gdx.utils.*;
//import android.util.*;

public class MyGdxGame implements ApplicationListener
{
	Texture rocket;
	Texture fire1;
	Texture fire2;
	BitmapFont scoreTop;
	BitmapFont timeTop;
	Texture gold;
	Texture bg;
	int score;
	SpriteBatch batch;
	Array<Rectangle> fires;
	Array<Rectangle> golds;
	Table table;
	Stage stage;
	long lastDropTime;
	int rockX;
	Texture gameOverImg;
	long preTime;
	boolean gameOver = false;
	Texture restart;
	//Animation<TextureRegion> animation;
    Skin skin;
	Music sounds;
	int time;
	long lastGoldDropTime;
	public static final int WORLD_WIDTH = 800;
    public static final int WORLD_HEIGHT = 480;
	OrthographicCamera camera;
	@Override
	public void create()
	{
		sounds = Gdx.audio.newMusic(Gdx.files.internal("aadhi.mp3"));
		sounds.setLooping(true);
		sounds.play();
		
		restart = new Texture(Gdx.files.internal("refreshing.png"));
		gameOverImg = new Texture(Gdx.files.internal("gameOver.png"));
		bg = new Texture(Gdx.files.internal("bg.jpg"));
		rocket = new Texture(Gdx.files.internal("rocket.png"));
		fire1 = new Texture(Gdx.files.internal("fire1.png"));
		fire2 = new Texture(Gdx.files.internal("fire2.png"));
		gold = new Texture(Gdx.files.internal("gold1.png"));
		batch = new SpriteBatch();
		score = 0;
		time = 0;
		//animation = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("throbber.gif").read());
		//skin = new Skin(Gdx.files.internal("default_skin/uiskin.json"));
		
		timeTop = new BitmapFont();
		scoreTop = new BitmapFont();
		rockX = Gdx.graphics.getWidth()/2;
		fires = new Array<Rectangle>();
		golds = new Array<Rectangle>();
		preTime = TimeUtils.nanoTime();
		/*
		table = new Table();
		table.setFillParent(true);
		table.setDebug(true);
		table.setFillParent(true);
		table.setDebug(true);

		table.add("hello").expand(true, false).center();
		table.row().pad(20, 0, 0, 10);
		table.add("hello everyone").expand(true, false);
		table.row().pad(10, 0, 0, 20);
		table.add("cent").expand(true, false);
		table.add("4").expand(true, false);
		table.row().pad(10, 0, 0, 20);
		table.add("5").expand(true, false);
		table.add("9").expand(true, false);
		table.row().pad(10, 0, 0, 20);
		table.add("0").expand(true, false);
		table.add("77").expand(true, false);
		table.setVisible(false);

		stage.addActor(table);*/
	}

	@Override
	public void render()
	{
		//sounds.play();
		camera = new OrthographicCamera();
		camera.setToOrtho(false,900,400);
	    Gdx.gl.glClearColor(1, 1, 1, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		
		batch.draw(bg,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		if (gameOver){
			batch.draw(gameOverImg,(Gdx.graphics.getWidth()/2)-300,Gdx.graphics.getHeight()/3,600,600);
			timeTop.setColor(Color.WHITE);
			timeTop.setScale(3,3);
			timeTop.draw(batch,"Time "+Integer.toString(time),(Gdx.graphics.getWidth()/2)+50,(Gdx.graphics.getHeight()/2)-100);
			scoreTop.setColor(Color.WHITE);
			scoreTop.setScale(3,3);
			scoreTop.draw(batch,"Score "+Integer.toString(score),(Gdx.graphics.getWidth()/2)-150,(Gdx.graphics.getHeight()/2)-100);
			batch.draw(restart,(Gdx.graphics.getWidth()/2)-30,(Gdx.graphics.getHeight()/3)-70,60,60);
			
			
		}
		batch.draw(rocket,rockX,Gdx.graphics.getHeight()/3,100,100);
		timeTop.setColor(Color.PINK);
		timeTop.setScale(2,2);
		timeTop.draw(batch,"Time "+Integer.toString(time),200,Gdx.graphics.getHeight());
		scoreTop.setColor(Color.PINK);
		scoreTop.setScale(2,2);
		scoreTop.draw(batch,"Score "+Integer.toString(score),0,Gdx.graphics.getHeight());
		
		for(Rectangle fire: fires){
			
			batch.draw(fire1,fire.x,fire.y,fire.width,fire.height);
		}
		for(Rectangle fire: golds){

			batch.draw(gold,fire.x,fire.y,fire.width,fire.height);
		}
		if(TimeUtils.nanoTime()-lastDropTime > 1000000000 && !gameOver) addFire();
		for(Iterator<Rectangle> iter = fires.iterator(); iter.hasNext();){
			Rectangle fire = iter.next();
			if(!gameOver)fire.y -= 200 * Gdx.graphics.getDeltaTime();
			if(fire.y-12 <0) iter.remove();
			if(fire.x > rockX-50 && fire.x < rockX +50 && fire.y > Gdx.graphics.getHeight()/3 -15 && fire.y < Gdx.graphics.getHeight()/3 +15){
				iter.remove();
				gameOver = true;
				rocket = new Texture(Gdx.files.internal("blast.gif"));
				/*stage.getBatch().end();
				stage.act();
				stage.draw();
				stage.getBatch().begin();
				*/
			}
		}
		if(TimeUtils.nanoTime()-lastGoldDropTime > 1523030600 && !gameOver) addGold();
		for(Iterator<Rectangle> iter = golds.iterator(); iter.hasNext();){
			Rectangle fire = iter.next();
			if(!gameOver)fire.y -= 200 * Gdx.graphics.getDeltaTime();
			if(fire.y-12 <0) iter.remove();
			if(fire.x > rockX-50 && fire.x < rockX +50 && fire.y > Gdx.graphics.getHeight()/3 -15 && fire.y < Gdx.graphics.getHeight()/3 +15){
				iter.remove();
				score += 1;
			}
		}
		batch.end();
		if(Gdx.input.isTouched()){
			if(gameOver){
				if(/*Gdx.input.getX() > (Gdx.graphics.getWidth()/2)-30 && Gdx.input.getX() < (Gdx.graphics.getWidth()/2)+30 && */Gdx.input.getY() > (Gdx.graphics.getHeight()/2)-500 && Gdx.input.getY() < (Gdx.graphics.getHeight()/2)+500){
					gameOver = false;
					score = 0;time=0;
					rocket = new Texture(Gdx.files.internal("rocket.png"));
				}
			}
			else if (rockX >= Gdx.graphics.getWidth()){
				rockX = 1;
			}
			else if(rockX < 1){
				rockX = Gdx.graphics.getWidth()-1;
			}
			else if(Gdx.input.getX() > Gdx.graphics.getWidth()/2){
				rockX += 3;
			}
			else if(Gdx.input.getX()<Gdx.graphics.getWidth()/2){
				rockX -= 3;
			}
		}
		if(TimeUtils.nanoTime()-preTime > 1000000000){
			if(!gameOver)time += 1;
			preTime = TimeUtils.nanoTime();
		}
		
		
	}
	
	public void addFire(){
		Rectangle rectangle = new Rectangle();
		rectangle.x = MathUtils.random(25, Gdx.graphics.getWidth()-25);
		rectangle.y = Gdx.graphics.getHeight()-25;
		rectangle.width = 50;
		rectangle.height = 70;
		
		fires.add(rectangle);
       // batch.draw(fire1, x,Gdx.graphics.getHeight()-32,50,70);
		lastDropTime = TimeUtils.nanoTime();
	}
	public void addGold(){
		Rectangle rectangle = new Rectangle();
		rectangle.x = MathUtils.random(25, Gdx.graphics.getWidth()-25);
		rectangle.y = Gdx.graphics.getHeight()-25;
		rectangle.width = 50;
		rectangle.height = 50;

		golds.add(rectangle);
		// batch.draw(fire1, x,Gdx.graphics.getHeight()-32,50,70);
		lastGoldDropTime = TimeUtils.nanoTime();
	}
	@Override
	public void dispose()
	{
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}
}
