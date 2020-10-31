package com.badlogic.gdx.setup.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.setup.backend.GenerateProjectParams;

import static com.badlogic.gdx.Application.ApplicationType.WebGL;
import static com.badlogic.gdx.setup.SetupUi.*;

public class ClassicProjectTable extends Table  {
    private GenerateProjectParams params = new GenerateProjectParams();
    private TextButton generateButton;
    
    public ClassicProjectTable() {
        params.appName = "my-gdx-game";
        params.packageName = "com.mygdx.game";
        params.mainClass = "Main";
        params.withDesktop = true;
        
        InputListener traversalListener = new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (Gdx.app.getType() == WebGL && keycode == Keys.TAB) {
                    TextField textField = ((TextField) event.getListenerActor());
                    textField.next(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT));
                    return true;
                }
                return false;
            }
        };
        
        setBackground(skin.getDrawable("window"));
        pad(10);
        
        row();
        Image image = new Image(skin.getDrawable("logo-libgdx-small"));
        add(image);
        
        row();
        image = new Image(skin.getDrawable("divider-horizontal"));
        add(image).growX().space(15);
        
        row();
        defaults().space(50f);
        Table outer = new Table();
        add(outer).expand();

        Table table = new Table();
        outer.add(table);
    
        table.defaults().spaceRight(5);
        Label label = new Label("PROJECT NAME", skin);
        table.add(label).right();
    
        TextField textField = new TextField(params.appName, skin);
        textField.setName("keyboard-focus");
        textField.setMessageText("my-gdx-game");
        table.add(textField);
        textField.addListener(traversalListener);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.appName = ((TextField) actor).getText();
                generateButton.setDisabled(!isDataValid());
            }
        });
    
        table.row();
        label = new Label("PACKAGE", skin);
        table.add(label).right();
    
        textField = new TextField(params.packageName, skin);
        textField.setMessageText("com.mygdx.game");
        table.add(textField).left();
        textField.addListener(traversalListener);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.packageName = ((TextField) actor).getText();
                generateButton.setDisabled(!isDataValid());
            }
        });
    
        table.row();
        label = new Label("MAIN CLASS", skin);
        table.add(label).right();
    
        textField = new TextField(params.mainClass, skin);
        textField.setMessageText("Main");
        table.add(textField).left();
        textField.addListener(traversalListener);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.mainClass = ((TextField) actor).getText();
                generateButton.setDisabled(!isDataValid());
            }
        });

        outer.row();
        image = new Image(skin, "divider-horizontal");
        outer.add(image).growX().space(10);

        outer.row();
        table = new Table();
        outer.add(table);

        table.defaults().left();
        CheckBox checkBox = new CheckBox("DESKTOP", skin);
        checkBox.setChecked(params.withDesktop);
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.withDesktop = ((CheckBox) actor).isChecked();
                generateButton.setDisabled(!isDataValid());
            }
        });

        table.row();
        checkBox = new CheckBox("ANDROID", skin);
        checkBox.setChecked(params.withAndroid);
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.withAndroid = ((CheckBox) actor).isChecked();
                generateButton.setDisabled(!isDataValid());
            }
        });

        table.row();
        checkBox = new CheckBox("IOS", skin);
        checkBox.setChecked(params.withIos);
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.withIos = ((CheckBox) actor).isChecked();
                generateButton.setDisabled(!isDataValid());
            }
        });

        table.row();
        checkBox = new CheckBox("HTML5", skin);
        checkBox.setChecked(params.withHtml);
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.withHtml = ((CheckBox) actor).isChecked();
                generateButton.setDisabled(!isDataValid());
            }
        });
        
        row();
        table = new Table();
        add(table).growX();

        TextButton textButton = new TextButton("PREVIOUS", skin, "small");
        table.add(textButton).uniform();
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                slideRightTable(landingTable);
            }
        });

        table.add().expandX();
        
        generateButton = new TextButton("GENERATE", skin, "small");
        generateButton.setDisabled(!isDataValid());
        table.add(generateButton).uniform();
        generateButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.gdxVersion = "1.9.12";
                slideDownFadeInTable(new GenerateLoadingTable(params));
            }
        });
    }
    
    public void updateKeyboardFocus() {
        TextField textField = findActor("keyboard-focus");
        stage.setKeyboardFocus(textField);
        textField.selectAll();
    }
    
    public boolean isDataValid() {
        return params.packageName != null && params.appName != null && params.mainClass != null &&
                !params.packageName.equals("") && !params.appName.equals("") && !params.mainClass.equals("") &&
                (params.withAndroid || params.withDesktop || params.withHtml || params.withIos);
    }
}
