package com.mygdx.game.battle

import com.mygdx.game.*

import com.badlogic.gdx.Input
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.TimeUtils

import com.badlogic.gdx.graphics.Texture
import java.util.Random

enum class BattleType{
    NORMAL
}

class BattleScreen(val game : Cac, val type : BattleType, val player : Entity) : Screen{

    var camera = OrthographicCamera()
    var DebugRenderer = Box2DDebugRenderer()
    var font = BitmapFont()
    var batch = SpriteBatch()
    var battle = BattleCycle(batch, font, player)
    init{
        font.setColor(Color.WHITE)
    }
    //note, entities are empty for now :)
    override fun render(dt : Float){
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        battle.draw()
        batch.end()
        battle.update(dt)
        if (battle.didEnd()){
            game.setScreen(game.game)
        }
        camera.update()
    }
   
    override fun dispose(){
        batch.dispose()
        font.dispose()
    }

    override fun show(){
    }

    override fun hide(){
    }

    override fun resize(width : Int, height : Int){
    }

    override fun resume(){
    }

    override fun pause(){
    }
}


