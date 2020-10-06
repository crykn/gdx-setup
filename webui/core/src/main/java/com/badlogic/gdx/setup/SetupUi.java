package com.badlogic.gdx.setup;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.setup.backend.BackendClient;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.setup.tables.*;
import com.badlogic.gdx.setup.widgets.LibBuilder.LibBuilderStyle;
import com.badlogic.gdx.setup.widgets.WizardProgress.ProgressGroupStyle;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class SetupUi extends ApplicationAdapter {
	public static SetupUi setupUi;
	public static Stage stage;
	public static Skin skin;
	public static Table root;
	public static LandingTable landingTable;
	public static ProjectTable projectTable;
	public static LibrariesTable librariesTable;
	public static OptionsTable optionsTable;
	public static ClassicProjectTable classicProjectTable;
	public static GenerateLoadingTable generateLoadingTable;
	public static RetrieveDataLoadingTable retrieveDataLoadingTable;
	public static Table currentTable;
	public static final float INTRO_TRANSITION_TIME = 1f;
	public static final float TRANSITION_TIME = 1.2f;
	public static final float OUTRO_TRANSITION_TIME = 1.6f;
	public static BackendClient backendClient;
	public static List<String> supportedGDXVersions;
	
	@Override
	public void create() {
		setupUi = this;
		
		backendClient = new BackendClient();
		
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		skin = new Skin(Gdx.files.internal("skin/skin.json"));
		
		ProgressGroupStyle progressGroupStyle = new ProgressGroupStyle(skin.getDrawable("progress-back-10"), skin.get("progress",
				ButtonStyle.class), skin.get("progress", LabelStyle.class));
		skin.add("default", progressGroupStyle);
		
		LibBuilderStyle libBuilderStyle = new LibBuilderStyle();
		libBuilderStyle.category = skin.get(LabelStyle.class);
		libBuilderStyle.tooltipLabel = skin.get(LabelStyle.class);
		libBuilderStyle.delete = skin.get("delete", ImageTextButtonStyle.class);
		libBuilderStyle.add = skin.get("add", ButtonStyle.class);
		libBuilderStyle.listItem = skin.get(CheckBoxStyle.class);
		libBuilderStyle.check = skin.get("check", ImageTextButtonStyle.class);
		libBuilderStyle.popBackground = skin.getDrawable("list-library-10");
		libBuilderStyle.popStageBackground = skin.getDrawable("stage-background");
		libBuilderStyle.tooltipBackground = skin.getDrawable("list-library-10");
		libBuilderStyle.popTooltipBackground = skin.getDrawable("list-10");
		skin.add("default", libBuilderStyle);
		
		root = new Table();
		root.setBackground(skin.getDrawable("bg-10"));
		root.setFillParent(true);
		stage.addActor(root);
		
		landingTable = new LandingTable();
		projectTable = new ProjectTable();
		librariesTable = new LibrariesTable();
		optionsTable = new OptionsTable();
		classicProjectTable = new ClassicProjectTable();
		generateLoadingTable = new GenerateLoadingTable();
		retrieveDataLoadingTable = new RetrieveDataLoadingTable();

		firstTable(retrieveDataLoadingTable);
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
    
    public static void firstTable(Table firstTable) {
        root.clearChildren();
        root.add(firstTable).minSize(600, 530);
        root.validate();
        currentTable = firstTable;
        firstTable.addAction(alpha(1.0f, OUTRO_TRANSITION_TIME / 2, Interpolation.fade));
        firstTable.setColor(1, 1, 1, 0);
    }
	
	public static void slideDownTable(Table introTable) {
	    root.clearChildren();
		root.add(introTable).minSize(600, 530);
		root.validate();
		currentTable = introTable;
		introTable.addAction(sequence(
				moveTo(introTable.getX(), stage.getHeight()),
				moveTo(introTable.getX(), introTable.getY(), INTRO_TRANSITION_TIME, Interpolation.fade)
		));
		introTable.setPosition(introTable.getX(), stage.getHeight());
	}

	public static void previousTable(Table previousTable) {
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

	public static void nextTable(Table nextTable) {
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

	public static void finalTable(Table nextTable) {
		currentTable.addAction(sequence(
				moveTo(currentTable.getX(), -currentTable.getHeight(), OUTRO_TRANSITION_TIME / 2, Interpolation.fade),
				run(() -> {
					root.clearChildren();
					root.add(nextTable).minSize(600, 530);
					root.validate();
					currentTable = nextTable;
					nextTable.addAction(sequence(
							alpha(1.0f, OUTRO_TRANSITION_TIME / 2, Interpolation.fade)
					));
					nextTable.setColor(1, 1, 1, 0);
				})
		));
	}
}