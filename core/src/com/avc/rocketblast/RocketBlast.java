package com.avc.rocketblast;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.*;

public class RocketBlast extends ApplicationAdapter
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
	long lastDropTime;
	int rockX;
	Texture gameOverImg;
	long preTime;
	boolean gameOver = false;
	Texture restart;
	Music sounds;
	int time;
	long lastGoldDropTime;
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

		timeTop = new BitmapFont();
		scoreTop = new BitmapFont();
		rockX = Gdx.graphics.getWidth()/2;
		fires = new Array<>();
		golds = new Array<>();
		preTime = TimeUtils.nanoTime();
	}

	@Override
	public void render()
	{
		camera = new OrthographicCamera();
		camera.setToOrtho(false,900,400);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		batch.draw(bg,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		if (gameOver){
			batch.draw(gameOverImg,(float)(Gdx.graphics.getWidth()/2)-300,(float)Gdx.graphics.getHeight()/3,600,600);
			timeTop.setColor(Color.WHITE);
			timeTop.getData().setScale(3,3);
			timeTop.draw(batch,"Time "+time,(float)(Gdx.graphics.getWidth()/2)+50,(float)(Gdx.graphics.getHeight()/2)-50);
			scoreTop.setColor(Color.WHITE);
			scoreTop.getData().setScale(3,3);
			scoreTop.draw(batch,"Score "+score,(float)(Gdx.graphics.getWidth()/2)-150,(float)(Gdx.graphics.getHeight()/2)-50);
			batch.draw(restart,(float)(Gdx.graphics.getWidth()/2),(float)(Gdx.graphics.getHeight()/3)-100,60,60);
		}
		batch.draw(rocket,rockX,(float)Gdx.graphics.getHeight()/3,100,100);
		timeTop.setColor(Color.PINK);
		timeTop.getData().setScale((float)2.2,(float)2.2);
		timeTop.draw(batch,"Time "+time,230,Gdx.graphics.getHeight() - 50);
		scoreTop.setColor(Color.PINK);
		scoreTop.getData().setScale((float)2.2,(float)2.2);
		scoreTop.draw(batch,"Score "+score,30,Gdx.graphics.getHeight() - 50);

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
			if(fire.x > rockX-50 && fire.x < rockX +50 && fire.y > (float)Gdx.graphics.getHeight()/3 -15 && fire.y < (float)Gdx.graphics.getHeight()/3 +15){
				iter.remove();
				gameOver = true;
				rocket = new Texture(Gdx.files.internal("blast.gif"));
			}
		}
		if(TimeUtils.nanoTime()-lastGoldDropTime > 1523030600 && !gameOver) addGold();
		for(Iterator<Rectangle> iter = golds.iterator(); iter.hasNext();){
			Rectangle fire = iter.next();
			if(!gameOver)fire.y -= 200 * Gdx.graphics.getDeltaTime();
			if(fire.y-12 <0) iter.remove();
			if(fire.x > rockX-50 && fire.x < rockX +50 && fire.y > (float)Gdx.graphics.getHeight()/3 -15 && fire.y < (float)Gdx.graphics.getHeight()/3 +15){
				iter.remove();
				score += 1;
			}
		}
		batch.end();
		if(Gdx.input.isTouched()){
			if(gameOver){
				if(Gdx.input.getY() > (Gdx.graphics.getHeight()/2)-500 && Gdx.input.getY() < (Gdx.graphics.getHeight()/2)+500){
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
		lastDropTime = TimeUtils.nanoTime();
	}
	public void addGold(){
		Rectangle rectangle = new Rectangle();
		rectangle.x = MathUtils.random(25, Gdx.graphics.getWidth()-25);
		rectangle.y = Gdx.graphics.getHeight()-25;
		rectangle.width = 50;
		rectangle.height = 50;

		golds.add(rectangle);
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
