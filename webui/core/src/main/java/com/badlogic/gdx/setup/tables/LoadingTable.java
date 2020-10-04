package com.badlogic.gdx.setup.tables;

import com.badlogic.gdx.scenes.scene2d.ui.*;

import static com.badlogic.gdx.setup.SetupUi.*;

public class LoadingTable extends Table  {
    public LoadingTable() {
        Image image = new Image(skin, "loading-animation");
        add(image);
    }
}
