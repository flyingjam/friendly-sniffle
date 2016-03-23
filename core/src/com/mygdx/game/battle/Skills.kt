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

enum class SkillType{
    ATTACK, RANGE, SKILL
}

open class Skill(var name : String = "DEFAULT", var type : SkillType){
    open fun update(dt : Float, user : BattleContainer, vararg targets : BattleContainer){}
    open fun draw(batch : SpriteBatch, user : BattleContainer){}
    open fun isDone() : Boolean {return false}
}

open class BasicAttackSkill(var attackAnimation : Animation, 
    var effect : (user : BattleContainer, targets : Array<out BattleContainer>) -> Unit) : 
    Skill("fuck", SkillType.ATTACK){

    //true basicattackskill
    enum class States{
        FORWARD, ANIMATION, EFFECT, BACKWARD, DONE
    }

    val speed = 300f
    val proximity = 10f
    var done = false
    var time = 0f
    var state = States.FORWARD

    override fun update(dt : Float, user : BattleContainer, vararg targets : BattleContainer){

        //if multiple targets, will always walk to first
        val enemy = targets.get(0)
        when(state){
            States.FORWARD -> 
                {
                    if (user.x <= (enemy.x + proximity))
                        user.x += speed * dt
                    else
                        state = States.ANIMATION
                }
            States.ANIMATION -> 
                {
                    time += dt
                    if (attackAnimation.isAnimationFinished(time))
                        state = States.EFFECT
                }
            States.EFFECT -> 
                {
                    effect(user, targets)
                    //play effect animation..?
                    state = States.BACKWARD
                }
            States.BACKWARD ->
                {
                    if (user.x >= 20f)
                        user.x -= speed * dt
                    else
                        state = States.DONE
                }
            States.DONE ->
                {
                    done = true
                }
        }
    }

    override fun draw(batch : SpriteBatch, user : BattleContainer){
        if (state != States.ANIMATION)
            batch.draw(attackAnimation.getKeyFrame(0f), user.x, user.y)
        else
            batch.draw(attackAnimation.getKeyFrame(time), user.x, user.y)
    }

    override fun isDone() : Boolean {
        return done
    }

}


open class BasicRangeSkill(var attackAnimation : Animation, 
    var effect : (user : BattleContainer, targets : Array<out BattleContainer>) -> Unit) : 
    Skill("Range", SkillType.RANGE){

    //true basicattackskill
    enum class States{
        ANIMATION, EFFECT, DONE
    }

    val speed = 50f
    val proximity = 10f
    var done = false
    var time = 0f
    var state = States.ANIMATION

    override fun update(dt : Float, user : BattleContainer, vararg targets : BattleContainer){

        //if multiple targets, will always walk to first
        val enemy = targets.get(0)
        when(state){ 
            States.ANIMATION -> 
                {
                    time += dt
                    if (attackAnimation.isAnimationFinished(time))
                        state = States.EFFECT
                }
            States.EFFECT -> 
                {
                    effect(user, targets)
                    //play effect animation..?
                    state = States.DONE
                }
            States.DONE ->
                {
                    done = true
                }
        }
    }

    override fun draw(batch : SpriteBatch, user : BattleContainer){
        batch.draw(attackAnimation.getKeyFrame(time), user.x, user.y)
        
    }

    override fun isDone() : Boolean {
        return done
    }

}


