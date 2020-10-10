package com.badlogic.gdx.setup;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.setup.backend.BackendClient;
import com.badlogic.gdx.setup.tables.ClassicProjectTable;
import com.badlogic.gdx.setup.tables.LandingTable;
import com.badlogic.gdx.setup.tables.SummaryTable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class SetupUi extends ApplicationAdapter {
	public static SetupUi setupUi;
	public static Stage stage;
	public static Skin skin;
	public static Table root;
	public static LandingTable landingTable;
	public static ClassicProjectTable classicProjectTable;
	public static SummaryTable summaryTable;
	public static Table currentTable;
	public static final float SLOW_TRANSITION_TIME = 1.5f;
	public static final float TRANSITION_TIME = 1.2f;
	public static BackendClient backendClient;
	public static String[] supportedGDXVersions = new String[]{};
	public static String buildVersion;
	public static final String libGdxVersion = "1.9.11";
	public static final String snapshotVersion = "1.9.12-SNAPSHOT";
	public static final String setupVersion = "0.0.1";
	
	@Override
	public void create() {
		setupUi = this;
		
		backendClient = new BackendClient();
		
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		skin = new Skin(Gdx.files.internal("skin/skin.json"));
		
		root = new Table();
		root.setBackground(skin.getDrawable("bg-10"));
		root.setFillParent(true);
		stage.addActor(root);
		
		RetrieveData retrieveData = new RetrieveData();
		
		landingTable = new LandingTable();
		classicProjectTable = new ClassicProjectTable();
		summaryTable = new SummaryTable();

		slideDownTable(landingTable);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();
		if (Gdx.input.isKeyJustPressed(Keys.F5)) {
			dispose();
			create();
		}
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void dispose() {
		skin.dispose();
	}
    
    public static void fadeInTable(Table firstTable) {
        root.clearChildren();
        root.add(firstTable).minSize(600, 530);
        root.validate();
        currentTable = firstTable;
        firstTable.addAction(alpha(1.0f, SLOW_TRANSITION_TIME / 2, Interpolation.fade));
        firstTable.setColor(1, 1, 1, 0);
    }
    
    public static void slideDownTable(Table firstTable) {
        root.clearChildren();
        root.add(firstTable).minSize(600, 530);
        root.validate();
        currentTable = firstTable;
        firstTable.addAction(moveTo(firstTable.getX(), firstTable.getY(), SLOW_TRANSITION_TIME / 2, Interpolation.fade));
        firstTable.setPosition(firstTable.getX(), stage.getHeight());
    }
	
	public static void fadeOutSlideDownTable(Table introTable) {
        currentTable.addAction(sequence(
                fadeOut(SLOW_TRANSITION_TIME / 2),
                run(() -> {
                    root.clearChildren();
                    root.add(introTable).minSize(600, 530);
                    root.validate();
                    currentTable = introTable;
                    introTable.addAction(sequence(
                            moveTo(introTable.getX(), stage.getHeight()),
                            moveTo(introTable.getX(), introTable.getY(), SLOW_TRANSITION_TIME / 2, Interpolation.fade)
                    ));
                    introTable.setPosition(introTable.getX(), stage.getHeight());
                })
        ));
	}

	public static void slideRightTable(Table previousTable) {
		currentTable.addAction(sequence(
				moveTo(stage.getWidth(), currentTable.getY(), TRANSITION_TIME / 2, Interpolation.exp5),
				run(() -> {
					root.clearChildren();
					root.add(previousTable).minSize(600, 530);
					root.validate();
					currentTable = previousTable;
					previousTable.addAction(sequence(
							moveTo(-previousTable.getWidth(), previousTable.getY()),
							moveTo(previousTable.getX(), previousTable.getY(), TRANSITION_TIME / 2, Interpolation.exp5)
					));
					previousTable.setPosition(stage.getWidth(), previousTable.getY());
				})
		));
	}

	public static void slideLeftTable(Table nextTable) {
		currentTable.addAction(sequence(
				moveTo(-currentTable.getWidth(), currentTable.getY(), TRANSITION_TIME / 2, Interpolation.fade),
				run(() -> {
					root.clearChildren();
					root.add(nextTable).minSize(600, 530);
					root.validate();
					currentTable = nextTable;
					nextTable.addAction(sequence(
							moveTo(stage.getWidth(), nextTable.getY()),
							moveTo(nextTable.getX(), nextTable.getY(), TRANSITION_TIME / 2, Interpolation.fade)
					));
					nextTable.setPosition(stage.getWidth(), nextTable.getY());
				})
		));
	}

	public static void slideDownFadeInTable(Table nextTable) {
		currentTable.addAction(sequence(
				moveTo(currentTable.getX(), -currentTable.getHeight(), SLOW_TRANSITION_TIME / 2, Interpolation.fade),
				run(() -> {
					root.clearChildren();
					root.add(nextTable).minSize(600, 530);
					root.validate();
					currentTable = nextTable;
					nextTable.addAction(sequence(
							alpha(1.0f, SLOW_TRANSITION_TIME / 2, Interpolation.fade)
					));
					nextTable.setColor(1, 1, 1, 0);
				})
		));
	}
    
    public static void crossFadeTable(Table nextTable) {
        currentTable.addAction(sequence(
                fadeOut(SLOW_TRANSITION_TIME / 2),
                run(() -> {
                    root.clearChildren();
                    root.add(nextTable).minSize(600, 530);
                    root.validate();
                    currentTable = nextTable;
                    nextTable.addAction(sequence(
                            alpha(1.0f, SLOW_TRANSITION_TIME / 2, Interpolation.fade)
                    ));
                    nextTable.setColor(1, 1, 1, 0);
                })
        ));
    }
}