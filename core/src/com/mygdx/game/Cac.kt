package com.mygdx.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

fun clearScrean(){
    Gdx.gl.glClearColor(1f, 0f, 0f, 0f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
}

class Cac : Game (){
    internal lateinit var batch: SpriteBatch
    internal lateinit var texture: Texture
    
    internal lateinit var game : GameScreen
    override fun create(){
        batch = SpriteBatch()
        texture = Texture("badlogic.jpg")
        game = GameScreen(this)
        this.setScreen(game)
        //this.setScreen(BattleScreen(this, BattleType.NORMAL))
    }

    override fun render(){
        super.render()
   }

    override fun dispose(){
        batch.dispose()
    }
     
}
