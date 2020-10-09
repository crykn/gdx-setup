package com.badlogic.gdx.setup.tables;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.setup.backend.GenerateProjectParams;

import static com.badlogic.gdx.setup.SetupUi.slideDownFadeInTable;
import static com.badlogic.gdx.setup.SetupUi.landingTable;
import static com.badlogic.gdx.setup.SetupUi.slideRightTable;
import static com.badlogic.gdx.setup.SetupUi.skin;

public class ClassicProjectTable extends Table  {
    private GenerateProjectParams params = new GenerateProjectParams();
    
    public void populate() {
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
    
        TextField textField = new TextField("", skin);
        textField.setMessageText("my-gdx-game");
        table.add(textField);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.appName = ((TextField) actor).getText();
            }
        });
        
        table.row();
        label = new Label("MAIN CLASS", skin);
        table.add(label).right();
    
        textField = new TextField("", skin);
        textField.setMessageText("Main");
        table.add(textField).left();
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.mainClass = ((TextField) actor).getText();
            }
        });

        outer.row();
        image = new Image(skin, "divider-horizontal");
        outer.add(image).growX().space(10);

        outer.row();
        table = new Table();
        outer.add(table);

        table.defaults().left();
        CheckBox checkBox = new CheckBox("DESKTOP LWJGL3", skin);
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.withDesktop = ((CheckBox) actor).isChecked();
            }
        });

        table.row();
        checkBox = new CheckBox("ANDROID", skin);
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.withAndroid = ((CheckBox) actor).isChecked();
            }
        });

        table.row();
        checkBox = new CheckBox("IOS ROBOVM", skin);
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.withIos = ((CheckBox) actor).isChecked();
            }
        });

        table.row();
        checkBox = new CheckBox("HTML5 GWT", skin);
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.withHtml = ((CheckBox) actor).isChecked();
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
        
        textButton = new TextButton("GENERATE", skin, "small");
        table.add(textButton).uniform();
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                params.gdxVersion = "1.9.11";
                params.packageName = "asdf";
                slideDownFadeInTable(new GenerateLoadingTable(params));
            }
        });
    }
}
