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

open class BattleState(var cycle : BattleCycle){ 
    open fun update(dt : Float){}
    open fun draw(batch : SpriteBatch, font : BitmapFont){}
    open fun onEnter(){}
    open fun onExit(){}
}

class PlayerTurn(cycle : BattleCycle) : BattleState(cycle){
    val skills = cycle.p.skills
    val command = CommandState(skills)
    var chosen_command = -1

    override fun update(dt : Float){
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            command.increment()
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            command.decrement()
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)){
            val skill = command.getSkill()
            if (skill != null)
                cycle.switch(PlayerTurnAnimation(cycle, skill))
            else
                cycle.switch(EnemyTurn(cycle)) 
        }

        command.update(dt)
    }

    override fun draw(batch : SpriteBatch, font : BitmapFont){ 
        command.draw(batch, font, 100f, 100f)
        font.draw(batch, "Player State", 100f, 400f)
    }

    override fun onEnter(){}
    override fun onExit(){}

}

class PlayerTurnAnimation(cycle : BattleCycle, var skill : Skill) : BattleState(cycle){
    
    override fun update(dt: Float){
        skill.update(dt, cycle.p, cycle.e)
        if (skill.isDone()){
            cycle.switch(EnemyTurn(cycle))
        }
    }

    override fun draw(batch : SpriteBatch, font : BitmapFont){
        skill.draw(batch, cycle.p)
    }


    override fun onEnter(){}
    override fun onExit(){ 
    }

}

class EnemyTurn(cycle : BattleCycle) : BattleState(cycle){
    var random = Random()
    override fun update(dt : Float){
        val decision = random.nextInt(3)
        when(decision){
            0 -> cycle.p.hp -= 1
            1 -> cycle.p.hp -= 0
            2 -> cycle.p.hp -= 5
        }
        cycle.switch(PlayerTurn(cycle))
         
    }
    override fun draw(batch : SpriteBatch, font : BitmapFont){
        font.draw(batch, "Enemy State", 100f, 100f)
    }

    override fun onEnter(){}
    override fun onExit(){}

}

class BattleCycle(var batch : SpriteBatch, var font : BitmapFont, var player : Entity){
    
    var p = PlayerContainer(player)
    var e = EnemyContainer()
    //TODO:
    //fuckin' terrible, make sure containers are parameters of PlayerTurn constructor later
    var state : BattleState = PlayerTurn(this)
    var statetime = 0f

    var hp = player.getComponent(StatComponent::class.java)

    fun update(dt : Float){
        state.update(dt)
    }
    fun draw(){
        font.draw(batch, "Enemy HP: " + e.hp, 200f, 300f)
        font.draw(batch, "Player HP: " + p.hp, 200f, 320f)
        state.draw(batch, font)
    }
    fun switch(target : BattleState){
        state.onExit()
        state = target
        state.onEnter()
    }
    fun didEnd() : Boolean{
        if (p.hp <= 0 || e.hp <= 0){
            hp.hp = p.hp
            return true
        }
        return false

    }
}


