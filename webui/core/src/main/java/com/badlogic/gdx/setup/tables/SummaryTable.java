package com.badlogic.gdx.setup.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import static com.badlogic.gdx.setup.SetupUi.*;

public class SummaryTable extends Table  {
    public SummaryTable() {
        setBackground(skin.getDrawable("window"));
        pad(10);
        
        row();
        defaults().space(50f);
        Image image = new Image(skin.getDrawable("logo-libgdx"));
        add(image);
    
        row();
        Label label = new Label("PROJECT SUCCESSFULLY GENERATED", skin, "button");
        add(label);
        
        row();
        Table table = new Table();
        add(table);
        
        table.defaults().space(20).uniform();
        TextButton textButton = new TextButton("LIBGDX.COM", skin, "link");
        table.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://libgdx.com/");
            }
        });
        
        textButton = new TextButton("SETUP GUIDE", skin, "link");
        table.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://libgdx.com/dev/setup/");
            }
        });
    
        textButton = new TextButton("COMMUNITY", skin, "link");
        table.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://discord.gg/6pgDK9F");
            }
        });
    
        row();
        textButton = new TextButton("CREATE NEW PROJECT", skin, "big");
        add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                slideLeftTable(classicProjectTable);
            }
        });
    }
}
