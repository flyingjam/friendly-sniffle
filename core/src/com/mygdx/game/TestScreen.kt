package com.mygdx.game

import com.badlogic.gdx.Screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.scenes.scene2d.*

class TestScreen : Screen{

    var stage = Stage(ScreenViewport()) 
    var skin = ui.Skin("uiskin.json")

    init{
        Gdx.input.setInputProcessor(stage)
    }
    override fun render(dt : Float){
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(dt)
        stage.draw()
    }
    override fun dispose(){
        stage.dispose()
    }
    override fun show(){
    }
    override fun hide(){
    }
    override fun resize(width : Int, height : Int){}
    override fun resume(){}
    override fun pause(){}
}
