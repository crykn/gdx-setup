package com.badlogic.gdx.setup.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import static com.badlogic.gdx.setup.SetupUi.*;

public class SummaryTable extends Table  {
    public void populate() {
        clearChildren();
        setBackground(skin.getDrawable("window"));
        pad(10);
        
        row();
        defaults().space(35f);
        Image image = new Image(skin.getDrawable("logo-libgdx"));
        add(image);
    
        row();
        Label label = new Label("PROJECT SUCCESSFULLY GENERATED", skin, "button");
        add(label);
        
        if (warnings.length > 0) {
            row();
            Table table = new Table();
            table.pad(10);
            ScrollPane scrollPane = new ScrollPane(table, skin);
            scrollPane.setFadeScrollBars(false);
            scrollPane.setScrollbarsOnTop(false);
            add(scrollPane).minHeight(50);

            for (String warning : warnings) {
                label = new Label(warning, skin);
                table.add(label);
                table.row();
            }
        }
        
        row();
        Table table = new Table();
        add(table);
        
        table.defaults().space(20).uniform();
        TextButton textButton = new TextButton("LIBGDX.COM", skin, "link");
        table.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                linkWorker.openLink(true, "https://libgdx.com/");
            }
        });
        
        textButton = new TextButton("SETUP GUIDE", skin, "link");
        table.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                linkWorker.openLink(true, "https://libgdx.com/dev/setup/");
            }
        });
    
        textButton = new TextButton("COMMUNITY", skin, "link");
        table.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                linkWorker.openLink(true, "https://discord.gg/6pgDK9F");
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
