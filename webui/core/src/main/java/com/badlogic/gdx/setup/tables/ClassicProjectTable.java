package com.badlogic.gdx.setup.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.setup.backend.GenerateProjectParams;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

import static com.badlogic.gdx.Application.ApplicationType.WebGL;
import static com.badlogic.gdx.setup.SetupUi.*;

public class ClassicProjectTable extends Table  {
    private GenerateProjectParams params = new GenerateProjectParams();
    private TextButton generateButton;
    private ObjectSet<String> extensions;
    private ObjectSet<String> warnings;
    private Label warningLabel;
    
    public ClassicProjectTable() {
        params.appName = "my-gdx-game";
        params.packageName = "com.mygdx.game";
        params.mainClass = "Main";
        params.withDesktop = true;
        params.withAndroid = true;
        params.withIos = true;
        params.withHtml = true;
        params.extensions = new String[0];
        extensions = new ObjectSet<>();
        warnings = new ObjectSet<>();
        
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
        Table bottom = new Table();
        outer.add(bottom);
    
        bottom.defaults().top().space(10);
        table = new Table();
        bottom.add(table);

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
                
                if (extensions.contains("freetype") && params.withHtml) addWarning("Freetype is not compatible with the HTML5 backend.");
                else removeWarning("Freetype is not compatible with the HTML5 backend.");
    
                if (extensions.contains("bullet") && params.withHtml) addWarning("Bullet is not compatible with the HTML5 backend.");
                else removeWarning("Bullet is not compatible with the HTML5 backend.");
            }
        });
        
        table = new Table();
        bottom.add(table);
        
        table.defaults().left();
        checkBox = new CheckBox("Bullet", skin);
        checkBox.setChecked(extensions.contains("bullet"));
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setExtension("bullet", ((CheckBox) actor).isChecked());
    
                if (extensions.contains("bullet") && params.withHtml) addWarning("Bullet is not compatible with the HTML5 backend.");
                else removeWarning("Bullet is not compatible with the HTML5 backend.");
            }
        });
    
        table.row();
        checkBox = new CheckBox("Freetype", skin);
        checkBox.setChecked(extensions.contains("freetype"));
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setExtension("freetype", ((CheckBox) actor).isChecked());
    
                if (extensions.contains("freetype") && params.withHtml) addWarning("Freetype is not compatible with the HTML5 backend.");
                else removeWarning("Freetype is not compatible with the HTML5 backend.");
            }
        });
    
        table.row();
        checkBox = new CheckBox("Tools", skin);
        checkBox.setChecked(extensions.contains("tools"));
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setExtension("tools", ((CheckBox) actor).isChecked());
            }
        });
    
        table.row();
        checkBox = new CheckBox("Controllers", skin);
        checkBox.setChecked(extensions.contains("controllers"));
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setExtension("controllers", ((CheckBox) actor).isChecked());
            }
        });
    
        table.row();
        checkBox = new CheckBox("Box2D", skin);
        checkBox.setChecked(extensions.contains("box2d"));
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setExtension("box2d", ((CheckBox) actor).isChecked());
            }
        });
    
        table.row();
        checkBox = new CheckBox("Box2dLights", skin);
        checkBox.setChecked(extensions.contains("box2dlights"));
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setExtension("box2dlights", ((CheckBox) actor).isChecked());
            }
        });
    
        table.row();
        checkBox = new CheckBox("Ashley", skin);
        checkBox.setChecked(extensions.contains("ashley"));
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setExtension("ashley", ((CheckBox) actor).isChecked());
            }
        });
    
        table.row();
        checkBox = new CheckBox("AI", skin);
        checkBox.setChecked(extensions.contains("ai"));
        table.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setExtension("ai", ((CheckBox) actor).isChecked());
            }
        });
        
        row();
        warningLabel = new Label("", skin);
        warningLabel.setAlignment(Align.center);
        warningLabel.setWrap(true);
        add(warningLabel).growX().spaceBottom(20);
        
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
    
    private void setExtension(String name, boolean active) {
        if (!active) extensions.remove(name);
        else extensions.add(name);
        params.extensions = extensions.iterator().toArray().toArray(String.class);
    }
    
    private void addWarning(String warning) {
        warnings.add(warning);
        warningLabel.setText(warning);
    }
    
    private void removeWarning(String warning) {
        warnings.remove(warning);
        warningLabel.setText(warnings.size > 0 ? warnings.iterator().toArray().peek() : "");
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
