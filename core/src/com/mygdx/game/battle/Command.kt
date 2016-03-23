package com.mygdx.game.battle

import com.mygdx.game.SkillsComponent

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

class CommandState(var skills : SkillsComponent){

    var state : Int = 0
    private val totalStates = 3

    private var startTime : Float = 0f
    private var lock : Boolean = false
    private var timeBetweenActions = 0.1f

    fun update(dt : Float){
        if (lock){
            startTime += dt
            if (startTime > timeBetweenActions)
                lockOff()
        }
    }

    fun draw(batch : SpriteBatch, font : BitmapFont, x : Float = 0f, y : Float = 0f){
        val message = "Command State : " + state
        font.draw(batch, message, 100f, 120f)
        when(state){
            0 -> font.draw(batch, skills.getAttack()?.name ?: "error", x, y)
            1 -> font.draw(batch, skills.getRange()?.name ?: "error", x, y)
            2 -> font.draw(batch, "SKILL", x, y)
        }
    }
    
    fun getSkill() : Skill? {
        return when(state){
            0 -> skills.getAttack()
            1 -> skills.getRange()
            else -> null
        }
    }

    private fun lockOn(){
        lock = true
        startTime = 0f
    }

    private fun lockOff(){
        lock = false
        startTime = 0f
    }

    fun increment(){
        if (!lock){
            state = (state + 1) % totalStates
            lockOn() 
        } 
    }

    fun decrement(){
        if (!lock){
            state = if ((state - 1) < 0) (totalStates - 1) else (state - 1)
            lockOn() 
        }
    }
}

