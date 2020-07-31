package com.friends.game;


import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Joguin extends ApplicationAdapter {

	private Stage stage;
	SpriteBatch batchEstatico;
	OrthographicCamera camera;
	SpriteBatch batch;
	Sprite lagartoAnimado;
	Animation <TextureRegion> walkAnimation;

	TextureRegion backgroundTexture;
	TextureRegion lagartoTexture;
	TextureRegion backgroundAbertura;
	TextureRegion canceladoTexture;



	float time;
	Rectangle manPosition;
	Vector2 manVelocity;
	Vector2 tamanhoSprite;
	List<Rectangle> lagartoPositions;
	Sound collisionSound;
	Vector3 touchPoint=new Vector3();

	Animation <TextureRegion> lagartoAnimation;

	static final String INICIO_JOGO = "INICIO";
	static final String DURANTE_JOGO = "DURANTE";
	static final String GANHOU_JOGO = "GANHOU";
	static final String PERDEU_JOGO = "PERDEU";

	String estadoJogo;
	int personagemAtual;

	BitmapFont font;
	TextureRegion [] texturasPersonagens;

	String [] personagens = {"arthur", "danilo", "joao", "mateus", "matheus", "renato", "ricardo", "sara", "wercton", "weslley"};


	@Override
	public void create () {

	    //Carrega o arquivo de fonte bitmap ( diferente da fonte normal, ele fica associado a uma imagem)
		font = new BitmapFont(Gdx.files.internal("font/fonte.fnt"));

		//Definida a variável responsável por guardar o estado do jogo, sinalizando o inicio
		estadoJogo = INICIO_JOGO;


		// Carrega a o fundo principal do jogo como textura e entao usa a textura para criar uma @TextureRegion
		Texture texture = new Texture(Gdx.files.internal("cenariouft2.png"));
		backgroundTexture = new TextureRegion(texture, 0, 0,2048, 563);

		//Carrega a imagem do camaleao que e o obstaculo principal desse joguinho merda
		Texture texture2 = new Texture(Gdx.files.internal("camaleao.png"));
		lagartoTexture = new TextureRegion(texture2,1081,421);

		//Carrega o fundo da primeira tela com umas plantinhas
		Texture texture3 = new Texture(Gdx.files.internal("fundomato.jpg"));
		backgroundAbertura = new TextureRegion(texture3,300,-90,1668,1251);

		//Carrega a imagem exibida quando o jogador perde o jogo
		Texture texture4 = new Texture(Gdx.files.internal("cancelado.jpg"));
		canceladoTexture = new TextureRegion(texture4, 0, 0,800, 600);

		texturasPersonagens = new TextureRegion[10];

		Texture texture5 = buscaTexturaPersonagem(personagens[0]);
		texturasPersonagens [0] = new TextureRegion(texture5,0,0,380,507);

		Texture texture6 = buscaTexturaPersonagem(personagens[1]);
		texturasPersonagens [1] = new TextureRegion(texture6, 300,400);

		Texture texture7 = buscaTexturaPersonagem(personagens[2]);
		texturasPersonagens [2] = new TextureRegion(texture7,500,556);

		Texture texture8 = buscaTexturaPersonagem(personagens[3]);
		texturasPersonagens [3] = new TextureRegion(texture8, 587,707);

		Texture texture9 = buscaTexturaPersonagem(personagens[4]);
		texturasPersonagens [4] = new TextureRegion(texture9, 500,564);

		Texture texture10 = buscaTexturaPersonagem(personagens[5]);
		texturasPersonagens [5] = new TextureRegion(texture10, 560,480);

		Texture texture11 = buscaTexturaPersonagem(personagens[6]);
		texturasPersonagens [6] = new TextureRegion(texture11, 270,340);

		Texture texture12 = buscaTexturaPersonagem(personagens[7]);
		texturasPersonagens [7] = new TextureRegion(texture12, 376,520);

		Texture texture13 = buscaTexturaPersonagem(personagens[8]);
		texturasPersonagens [8] = new TextureRegion(texture13, 700,641);

		Texture texture14 =  buscaTexturaPersonagem(personagens[9]);
		texturasPersonagens [9] = new TextureRegion(texture14, 1200,1293);



		//Cria o a lista onde serao posicionados todos os lagartos do jogo
		lagartoPositions = new  ArrayList<>();

		//Distancia a partir da qual o lagarto comecara a aparecer
		int x = 1800;

		//Loop que salva em objetos Rectangle as definicoes para aparicao do lagarto,
        // utilizando o x como parametro de posicao, e o x sendo incrementado aleatoriamente
		for (int i = 0; i < 60; i++) {
			lagartoPositions.add(new Rectangle(x,0,216,84));
			x += 600 + new Random().nextInt(600);
		}



		//Carrega a textura do sprite do lagarto puramente ilustrativo da tela inicial
		Texture walksheet2 = new Texture("walksheetlagarto.png");
		int FRAME_COLS2 = 5;
		int FRAME_ROWS2 = 4;

		//Recorta o sprite, guarda em uma matriz, depois em um vetor
		TextureRegion [][] tmp2= TextureRegion.split(walksheet2,walksheet2.getWidth() / FRAME_COLS2, walksheet2.getHeight() / FRAME_ROWS2);
		TextureRegion [] walkFrames2 = new TextureRegion[FRAME_COLS2 * FRAME_ROWS2];


		int index2 = 0;

		for (int i = 0; i < FRAME_ROWS2; i++) {
			for (int j = 0; j < FRAME_COLS2; j++) {
				walkFrames2[index2++] = tmp2[i][j];
			}
		}

		//Inicializa uma Animation com os frames do lagarto
		lagartoAnimation = new Animation<TextureRegion>(0.15f,walkFrames2);

		//Carrega o som utilizado quando o jogador bate em um lagarto
		collisionSound = Gdx.audio.newSound(Gdx.files.internal("videoweslley.wav"));

		//Inicializa e configura a camera
		camera = new OrthographicCamera();
		configureCamera();

		//Inicializa o batch, responsavel por desenhar as texturas na tela
		batch = new SpriteBatch();
		batchEstatico = new SpriteBatch();

		//Inicializa os objetos que guardam a posicao e velocidade do jogador e sao atuaizadas com a tela
		manVelocity = new Vector2(500,0);
		manPosition = new Rectangle(0,0,250,200);

		personagemAtual = 0;
	}

	@Override
	public void render () {


		if (estadoJogo.equals(INICIO_JOGO)){

		    //Tudo que esta na tela e limpo e substituido pelo branco
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			//A camera e atualizada para a nova situacao
			camera.update();


			//O batch e inicializado e depois utilizado para desenhar o fundo e o lagarto da tela inicial
			batch.begin();
			batch.draw(backgroundAbertura,0,0,1668,2000);

			//batch.draw(lagartoAnimation.getKeyFrame(time, true), manPosition.x, manPosition.y, 1000, 1000);

			if(Gdx.app.getType() == Application.ApplicationType.Android) {
				batch.draw(texturasPersonagens[0], 0,800,300,300);
				batch.draw(texturasPersonagens[1], 250,800,300,300);
				batch.draw(texturasPersonagens[2], 500,800,300,300);
				batch.draw(texturasPersonagens[3], 750,800,300,300);
				batch.draw(texturasPersonagens[4], 0,500,300,300);
				batch.draw(texturasPersonagens[5], 250,500,300,300);
				batch.draw(texturasPersonagens[6], 500,500,300,300);
				batch.draw(texturasPersonagens[7], 750,500,300,300);
				batch.draw(texturasPersonagens[8], 0,200,300,300);
				batch.draw(texturasPersonagens[9], 250,200,300,300);
				//A fonte e configurada e depois exibida
				font.setColor(Color.GREEN);
				font.getData().setScale(1.5f,1.5f);


				font.getData().setScale(2,2);
				font.draw(batch,"NAO PISE NO \nLAGARTO OU VOCE \nSERA CANCELADO \nPELOS DEFENSORES \nDOS ANIMAIS",30,1800);
				font.draw(batch,"ESCOLHA UM \nPERSONAGEM",20, 1300);

			}
			else {
				batch.draw(texturasPersonagens[0], 0, 250, 50, 50);
				font.draw(batch,"1",50,280);
				batch.draw(texturasPersonagens[1], 100, 250, 50, 50);
				font.draw(batch,"2",150,280);
				batch.draw(texturasPersonagens[2], 200, 250, 50,50);
				font.draw(batch,"3",250,280);
				batch.draw(texturasPersonagens[3], 300, 250, 50,50);
				font.draw(batch,"4",350,280);
				batch.draw(texturasPersonagens[4], 0, 200, 50,50);
				font.draw(batch,"5",50,230);
				batch.draw(texturasPersonagens[5], 100, 200, 50,50);
				font.draw(batch,"6",150,230);
				batch.draw(texturasPersonagens[6], 200, 200, 50,50);
				font.draw(batch,"7",250,230);
				batch.draw(texturasPersonagens[7], 300, 200, 50,50);
				font.draw(batch,"8",350,230);
				batch.draw(texturasPersonagens[8], 0, 150, 50,50);
				font.draw(batch,"9",50,180);
				batch.draw(texturasPersonagens[9], 100, 150, 50,50);
				font.draw(batch,"10",150,180);

				//A fonte e configurada e depois exibida
				font.setColor(Color.GREEN);
				font.getData().setScale(1.5f,1.5f);


				font.getData().setScale(0.5f,0.5f);
				font.draw(batch,"NAO PISE NO \nLAGARTO OU VOCE \nSERA CANCELADO \nPELOS DEFENSORES \nDOS ANIMAIS",30,450);
				font.draw(batch,"DIGITE O NUMERO \nDO SEU PERSONAGEM",20, 100);

			}





			//O batch e finalizado, caso contrario nao pode ser inicializado depois
			batch.end();


			//Com o toque do usuario na tela, o estado do mesmo e atualizado e o jogo comeca
            //TODO: detectar onde o usuario tocou/utilizar botoes


			final Sprite temp [] = new Sprite[10];

			//Cria Sprites para todos os icones dos personagens, a partir de suas TextureRegion
			for (int i = 0; i < 10; i++) {
				temp[i] = new Sprite(texturasPersonagens[i]);
			}


			final StringBuilder escolhidoPersonagem = new StringBuilder();

			Gdx.input.setInputProcessor(new InputProcessor() {
				@Override
				public boolean keyDown(int keycode) {
					escolhidoPersonagem.append(Input.Keys.toString(keycode));
					try {


						personagemAtual = Integer.parseInt(escolhidoPersonagem.toString())-1;
						setWalkAnimation(personagemAtual);
						estadoJogo = DURANTE_JOGO;
					}
					catch (Exception e){
						personagemAtual = 0;
					}
					return false;
				}

				@Override
				public boolean keyUp(int keycode) {
					return false;
				}

				@Override
				public boolean keyTyped(char character) {
					return false;
				}

				@Override
				public boolean touchDown(int screenX, int screenY, int pointer, int button) {
					camera.unproject(touchPoint.set(Gdx.input.getX(),Gdx.input.getY(),0));
					//camera.setToOrtho(true);
					configureCamera();

					System.out.println(touchPoint.x+" - "+touchPoint.y);

					//Percorre todos os icones de personagens e checa se algum deles foi tocado
					for (int i = 0; i < 10; i++) {
						if(temp[i].getBoundingRectangle().contains( touchPoint.x, touchPoint.y)){
							personagemAtual = i;
							setWalkAnimation(i);
							estadoJogo = DURANTE_JOGO;

							break;
						}
					}
					return false;
				}

				@Override
				public boolean touchUp(int screenX, int screenY, int pointer, int button) {
					return false;
				}

				@Override
				public boolean touchDragged(int screenX, int screenY, int pointer) {
					return false;
				}

				@Override
				public boolean mouseMoved(int screenX, int screenY) {
					return false;
				}

				@Override
				public boolean scrolled(int amount) {
					return false;
				}
			});



		}
		else if(estadoJogo.equals(DURANTE_JOGO)) {

		    //A camera e o batch sao inicializados
			camera.update();
			batch.setProjectionMatrix(camera.combined);
			batch.begin();

			//O fundo e desenhado varias vezes, ja que a camera vai se movimentar
			for (int i = 0; i < 30; i++) {
				batch.draw(backgroundTexture, i * 1450, 0, 3400, 800);
			}

			//Os lagartos sao desenhados utilizando o vetor definido anteriormente
			for (Rectangle r : lagartoPositions) {
				batch.draw(lagartoTexture, r.x, r.y, r.width, r.height);
			}

			//TODO: definir a altura e largura do personagem para cada um
			//O jogador e desenhado seguindo as variaveis que armazenam sua posicao
			batch.draw(walkAnimation.getKeyFrame(time, true), manPosition.x, manPosition.y, manPosition.width, manPosition.height);

			//O batch e finalizado
			batch.end();

			//As variaveis que guardam a posicao do jogador sao atualizadas para a proxima renderizacao da tela
            //A velocidade vertical sempre esta empurrando o jogdor para baixo, simulando gravidade
			manPosition.x += manVelocity.x * Gdx.graphics.getDeltaTime();
			manPosition.y += manVelocity.y * Gdx.graphics.getDeltaTime();
			manVelocity.y -= 1000 * Gdx.graphics.getDeltaTime();


			//Se o jogador toca a tela, sua velocidade vertizal aumenta e ele pula
			if (Gdx.input.isTouched() && manPosition.y == 0)
				manVelocity.y = 500;
			Gdx.input.setInputProcessor(new InputProcessor() {
				@Override
				public boolean keyDown(int keycode) {
					if(keycode == Input.Keys.SPACE){
						manVelocity.y = 500;
					}
					return false;
				}

				@Override
				public boolean keyUp(int keycode) {
					return false;
				}

				@Override
				public boolean keyTyped(char character) {
					return false;
				}

				@Override
				public boolean touchDown(int screenX, int screenY, int pointer, int button) {
					return false;
				}

				@Override
				public boolean touchUp(int screenX, int screenY, int pointer, int button) {
					return false;
				}

				@Override
				public boolean touchDragged(int screenX, int screenY, int pointer) {
					return false;
				}

				@Override
				public boolean mouseMoved(int screenX, int screenY) {
					return false;
				}

				@Override
				public boolean scrolled(int amount) {
					return false;
				}
			});

			//Caso a gravidade empurre o jogador muito para baixo, ele sempre volta para o "chao", na posicao zero
			if (manPosition.y < 0) {
				manPosition.y = 0;
				manVelocity.y = 0;
			}

			//A camera move-se linearmente para a direita
			camera.translate((manVelocity.x) * Gdx.graphics.getDeltaTime(), 0);

			//A distancia entre o lagarto mais proximo e o jogador e calculada e caso seja menor que 90
            //o estado do jogo muda, ja que ele perdeu, e o som de colisao e reproduzido
			for (Rectangle r : lagartoPositions) {
				if (r.overlaps(manPosition) && r.getCenter(new Vector2()).dst(manPosition.getCenter(new Vector2())) < 90) {
					collisionSound.play();
					estadoJogo = PERDEU_JOGO;
					configureCamera();
					break;
				}
			}

			//Se o jogador chega nessa posicao, ele terminou o percurso e ganhou
            //TODO: fazer isso de form dinamica ao inves de usar uma posicao estatica
			if (manPosition.x > 30000){
				estadoJogo = GANHOU_JOGO;
				resetGame();
			}
		}
		else if (estadoJogo.equals(GANHOU_JOGO)){

		    //A tela e limpa e a camera atualizada
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			camera.update();

		}
		else if(estadoJogo.equals(PERDEU_JOGO)){

		    //A tela e limpa e a camera atualizada
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			camera.update();

			//O batch e inicializado, e utilizado para desenhar a imagem de perdedor e a fonte com as instrucoes para recomecar
			batchEstatico.begin();


			if(Gdx.app.getType() == Application.ApplicationType.Android) {

				batchEstatico.draw(canceladoTexture,0,100,1500,1000);

				font.getData().setScale(2);
				font.draw(batchEstatico,"PERDEU \nPERDEU\n\n\nToque \npara \nrecomecar",20,1500);

			}
			else{
				batchEstatico.draw(canceladoTexture,0,100,700,400);

				font.getData().setScale(0.5f);
				font.draw(batchEstatico,"PERDEU \nPERDEU\n\n\n\n\n\n\n\n\n\n\nToque \npara \nrecomecar",20,350);
			}
			//O batch e finalizado
			batchEstatico.end();

			//Se o jogador clica na tela, o jogo reinicia
			if (Gdx.input.isTouched()){
				estadoJogo = DURANTE_JOGO;
				personagemAtual = personagemAtual == 10 ? 0 : personagemAtual+1;
				resetGame();
			}
		}

		//O tempo e atualizado a cada renderizacao da tela
		time += Gdx.graphics.getDeltaTime();



	}


	//A camera e a posicao do jogador sao reconfigurados para um novo jogo
	public void resetGame(){
		configureCamera();
		manPosition = new Rectangle(0,0,250,200);
		manVelocity = new Vector2(500,0);
	}


	public Texture buscaTexturaPersonagem(String nome){
		return new Texture(Gdx.files.internal("iconesPersonagens/"+nome+".png"));
	}

	public void setWalkAnimation(int personagem){

		int COLS = 0, ROWS = 0;

		//Carrega a textura do sprite do personagem principal
		Texture walksheet = new Texture(Gdx.files.internal("walksheet/walk"+personagens[personagem]+"sheet.png"));

		switch (personagem){
			case 0: case 3: case 7: COLS = 10; ROWS = 1; break;
			case 1: COLS = 10; ROWS = 2; break;
			case 2: COLS = 8; ROWS = 1; break;
			case 4: COLS = 5; ROWS = 3; break;
			case 5: case 9: COLS = 1; ROWS = 7; break;
			case 6: COLS = 7; ROWS = 1; break;
			case 8: COLS = 1; ROWS = 11; break;
		}

		//Recorta o sprite e guarda em uma matriz
		TextureRegion [][] tmp= TextureRegion.split(walksheet,walksheet.getWidth() / COLS, walksheet.getHeight() / ROWS);
		TextureRegion [] walkFrames = new TextureRegion[COLS * ROWS];

		int index2 = 0;

		//Percorre a matriz e transfere todos os frames para o vetor
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				walkFrames[index2++] = tmp[i][j];
			}
		}

		//Inicializa uma Animation passando os frames guardados
		walkAnimation = new Animation<TextureRegion>(0.08f, walkFrames);


	}
	//Ajusta a camera
	public void configureCamera(){
		if(Gdx.graphics.getHeight() > Gdx.graphics.getWidth())
			camera.setToOrtho(false, 800, 800f * Gdx.graphics.getHeight()/Gdx.graphics.getWidth());
		else
			camera.setToOrtho(false, 800f * Gdx.graphics.getWidth() / Gdx.graphics.getHeight(), 800);
	}

	@Override
	public void resize(int width, int height) {
		configureCamera();
	}

	@Override
	public void dispose () {
		batch.dispose();

	}

}
