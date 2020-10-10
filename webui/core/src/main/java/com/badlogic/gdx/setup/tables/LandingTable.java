package com.badlogic.gdx.setup.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import static com.badlogic.gdx.setup.SetupUi.*;
import static com.badlogic.gdx.utils.Align.bottomRight;

public class LandingTable extends Table  {
    public LandingTable() {
        setBackground(skin.getDrawable("window"));
        pad(10);
    
        add().expandY();
    
        row();
        defaults().space(50f);
        Image image = new Image(skin.getDrawable("logo-libgdx"));
        add(image);
    
        row();
        Table table = new Table();
        add(table);
    
        table.defaults().spaceRight(5);
        Label label = new Label("LIBGDX", skin);
        table.add(label).right();
    
        label = new Label(libGdxVersion, skin, "light");
        table.add(label).left();
    
        table.row();
        label = new Label("SNAPSHOT", skin);
        table.add(label).right();
    
        label = new Label(snapshotVersion, skin, "light");
        table.add(label).left();
    
        table.row();
        label = new Label("SETUP", skin);
        table.add(label).right();
    
        label = new Label(setupVersion, skin, "light");
        table.add(label).left();
    
        row();
        table = new Table();
        add(table);
    
        TextButton textButton = new TextButton("CREATE NEW PROJECT", skin, "big");
        table.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                slideLeftTable(classicProjectTable);
            }
        });
    
        row();
        defaults().clearActor();
        textButton = new TextButton("libgdx.com", skin, "link");
        add(textButton).align(bottomRight).expand();
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://libgdx.com/");
            }
        });
    }
}
