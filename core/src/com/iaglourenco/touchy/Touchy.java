package com.iaglourenco.touchy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

@SuppressWarnings("unused")
class Touchy extends ApplicationAdapter {

    private SpriteBatch batch;
    private final Texture[] birds= new Texture[4];
    private Texture background;
    private Texture canoCima,canoBaixo,gameOver;
    private BitmapFont fonte,message;
    private Rectangle canoCimaCollision,canoBaixoCollision;
    private Circle birdCollision;
    private float variacao=0;
    private float velocidadeQueda=0;
    private float deviceHeight;
    private float deviceWidth;
    private int pontuacao=0;

    private int xBird=0,yBird=0;
    private int xCanoHigh=0,yCanoHigh=0;
    private int xCanoLow=0,yCanoLow=0;
    private int espacoEntreCanos;
    private Random random;
    private int gameState=0;
    private float randSpace;
    private boolean marcou=false;
    private OrthographicCamera camera;
    private Viewport viewport;


    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }

    @Override
	public void create () {
	    Gdx.app.log("Info","Inicializado o jogo");
        batch = new SpriteBatch();
        float VIRTUAL_WIDTH = 768;
        float VIRTUAL_HEIGHT = 1024;
        deviceWidth = VIRTUAL_WIDTH;
        deviceHeight = VIRTUAL_HEIGHT;
        Music music = Gdx.audio.newMusic(Gdx.files.internal("adventure_song.mp3"));
        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        message = new BitmapFont();
        message.setColor(Color.WHITE);
        message.getData().setScale(3);

        background = new Texture("fundo.png");
        canoCima = new Texture("cano_topo_maior.png");
        canoBaixo = new Texture("cano_baixo_maior.png");
        birds[0]=new Texture("frame-1.png");
        birds[1]=new Texture("frame-2.png");
        birds[2]=new Texture("frame-3.png");
        birds[3]=new Texture("frame-4.png");


        gameOver = new Texture("game_over.png");

        birdCollision = new Circle();
        canoCimaCollision = canoBaixoCollision = new Rectangle();
        ShapeRenderer shapeRenderer = new ShapeRenderer();

        espacoEntreCanos=250;
        random = new Random();

        xBird=120;
        yBird = (int) (deviceHeight/2);

        xCanoHigh= (int) deviceWidth;
        yCanoHigh=0;

        xCanoLow = (int) deviceWidth;
        yCanoLow =0;

        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH /2, VIRTUAL_HEIGHT /2,0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT,camera);
        music.play();

    }
	@Override
	public void render () {

        camera.update();
        Gdx.app.log("FPS","FPS: "+Gdx.graphics.getFramesPerSecond());
        Gdx.gl.glClear(GL20.GL_DEPTH_BITS | GL20.GL_COLOR_BUFFER_BIT);

        Gdx.app.log("Info", "Game State= "+ gameState);
        if(gameState == 0){
            if(Gdx.input.justTouched()){
                gameState = 1;
           }
       }else{

            if (yBird > 0 || velocidadeQueda < 0) yBird -= velocidadeQueda++;
            if(gameState == 1){
                float deltaTime = Gdx.graphics.getDeltaTime() * 300;

                if (Gdx.input.justTouched()) {
                    if (yBird < deviceHeight) {
                        velocidadeQueda = -15;
                    }
                }
                float accelerationX = 10;
                if(Gdx.input.isTouched(1)){
                    xBird+= accelerationX /2;
                    velocidadeQueda=2;
                }else{

                    if(yBird<=birds[0].getHeight()){
                        xBird-= deltaTime;
                    }
                    if(xBird>120)
                        xBird-= accelerationX /2;
                }
                if(xBird<=0) gameState=-1;
                if (xCanoHigh < -canoCima.getWidth() && xCanoLow < -canoBaixo.getWidth()) {
                    xCanoLow = xCanoHigh = (int) deviceWidth;
                    randSpace = random.nextInt(400) - 200;
                    marcou=false;
                }
                xCanoHigh -= deltaTime;
                xCanoLow -= deltaTime;

                if(xCanoHigh < xBird && !marcou){
                    pontuacao++;
                    marcou=true;
                }

            }else{
                //game over
                if (Gdx.input.justTouched()) {
                    pontuacao=0;
                        gameState=0;
                        velocidadeQueda=0;
                        yCanoHigh=yCanoLow=0;
                        xBird=120;
                        yBird= (int) (deviceHeight/2);
                        xCanoHigh =xCanoLow = (int) deviceWidth;
                    }

            }


       }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(background, 0, 0, deviceWidth, deviceHeight);
        variacao += Gdx.graphics.getDeltaTime() * 5;
        if (variacao > birds.length) variacao = 0;
        batch.draw(canoBaixo, xCanoLow, (deviceHeight/2) - canoBaixo.getHeight()- (espacoEntreCanos / 2)  + randSpace + yCanoLow);
        batch.draw(canoCima, xCanoHigh, (deviceHeight/2) + (espacoEntreCanos / 2)+ randSpace + yCanoHigh);
        batch.draw(birds[(int) variacao], xBird, yBird);
        fonte.draw(batch,String.valueOf(pontuacao),deviceWidth/2,deviceHeight-50);

        if(gameState == -1){
            message.draw(batch,"Toque para reiniciar!",deviceWidth/2-230,deviceHeight/2 -gameOver.getHeight()/2);
            batch.draw(gameOver,deviceWidth/2 - gameOver.getWidth()/2,deviceHeight/2);
        }

        batch.end();

        birdCollision=new Circle(xBird + birds[0].getWidth()/2,yBird+birds[0].getHeight()/2,birds[0].getWidth()/2);
        canoCimaCollision = new Rectangle(xCanoHigh,(deviceHeight/2) + (espacoEntreCanos / 2)+ randSpace,canoCima.getWidth(),canoCima.getHeight()+yCanoHigh);
        canoBaixoCollision= new Rectangle(xCanoLow,(deviceHeight/2) - canoBaixo.getHeight()- (espacoEntreCanos / 2)  + randSpace,canoBaixo.getWidth(),canoBaixo.getHeight()+yCanoLow);

/*
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(birdCollision.x,birdCollision.y,birdCollision.radius);
        shapeRenderer.rect(canoBaixoCollision.x,canoBaixoCollision.y,canoBaixoCollision.width,canoBaixoCollision.height);
        shapeRenderer.rect(canoCimaCollision.x,canoCimaCollision.y,canoCimaCollision.width,canoCimaCollision.height);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.end();
*/
        if(Intersector.overlaps(birdCollision,canoBaixoCollision) || Intersector.overlaps(birdCollision,canoCimaCollision))
        {
            if(gameState != -1 )
                Gdx.input.vibrate(100);
            Gdx.app.log("Info","Colisao detectada");
            gameState = -1;
        }
    }
	

}
