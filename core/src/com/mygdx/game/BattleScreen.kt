package com.mygdx.game

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

class BasicAttackSkill : Skill("Attack", SkillType.ATTACK){
    
    var animationLock = true
    val speed = 200 //temp
    val proximity = 5 //temp
    
    //temp, use resource allocater later
    //no disposing for now, memory leak
    var regions = TextureAtlas("sprites.atlas")
    var animation = Animation(.5f, regions.getRegions())
    var time = 0f
    var done = false
    override fun update(dt : Float, user : BattleContainer, vararg targets : BattleContainer){
        //Will only ever have 1 target
        val enemy = targets.get(0)
        if(user.x <= (enemy.x + proximity) && !animation.isAnimationFinished(time)){
            //Player will always be to the left of the enemies, so we can
            //assume that he will need to move left
            user.x += speed * dt
        }
        else if (user.x >= 20f && animation.isAnimationFinished(time)){
            user.x -= speed * dt
        }
        else if (user.x <= 20f && animation.isAnimationFinished(time)){
            done = true
        }
        else{
            animationLock = false 
            time += dt
        }
    }

    override fun draw(batch : SpriteBatch, user : BattleContainer){
        batch.draw(animation.getKeyFrame(0f), user.x, user.y)
        if (!animationLock){
            val frame = animation.getKeyFrame(time) 
            batch.draw(frame, user.x, user.y)
            //play animation
        }
    }

    override fun isDone() : Boolean{
        return done
    }

}

enum class BattleType{
    NORMAL
}

class CommandState(){

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
            0 -> font.draw(batch, "ATTACK", x, y)
            1 -> font.draw(batch, "DEFEND", x, y)
            2 -> font.draw(batch, "SKILL", x, y)
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



open class BattleState(var cycle : BattleCycle){ 
    open fun update(dt : Float){}
    open fun draw(batch : SpriteBatch, font : BitmapFont){}
    open fun onEnter(){}
    open fun onExit(){}
}

class PlayerTurn(cycle : BattleCycle) : BattleState(cycle){
    val command = CommandState()
    var chosen_command = -1

    override fun update(dt : Float){
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            command.increment()
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            command.decrement()
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)){
            cycle.switch(PlayerTurnAnimation(cycle, command.state))
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

class PlayerTurnAnimation(cycle : BattleCycle, var action : Int) : BattleState(cycle){
    
    var attack = BasicAttackSkill()
    override fun update(dt: Float){
        attack.update(dt, cycle.p, cycle.e)
        if (attack.isDone()){
            cycle.switch(EnemyTurn(cycle))
        }
    }

    override fun draw(batch : SpriteBatch, font : BitmapFont){
        attack.draw(batch, cycle.p)
    }


    override fun onEnter(){}
    override fun onExit(){
        when(action){
            0 -> cycle.enemyhp -= 1
            1 -> cycle.enemyhp -= 0
            2 -> cycle.enemyhp -= 5
        }
    }

}

class EnemyTurn(cycle : BattleCycle) : BattleState(cycle){
    var random = Random()
    override fun update(dt : Float){
        val decision = random.nextInt(3)
        when(decision){
            0 -> cycle.hp.hp -= 1
            1 -> cycle.hp.hp -= 0
            2 -> cycle.hp.hp -= 5
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
    var state : BattleState = PlayerTurn(this)
    
    var p = PlayerContainer()
    var e = EnemyContainer()
    var playerhp = 10
    var enemyhp = 10
    var tTexture = Texture("123.png")
    var regions = TextureAtlas("sprites.atlas")
    var animation = Animation(.5f, regions.getRegions())
    var statetime = 0f

    var hp = player.getComponent(StatComponent::class.java)

    fun update(dt : Float){
        state.update(dt)
    }
    fun draw(){
        font.draw(batch, "Enemy HP: " + enemyhp, 200f, 300f)
        font.draw(batch, "Player HP: " + hp.hp, 200f, 320f)
        state.draw(batch, font)
    }
    fun switch(target : BattleState){
        state.onExit()
        state = target
        state.onEnter()
    }
    fun didEnd() : Boolean{
        return (hp.hp <= 0 || enemyhp <= 0)
    }
}

open class BattleContainer(var x : Float, var y : Float){

}

class PlayerContainer : BattleContainer(20f, 200f){

}

class EnemyContainer : BattleContainer(200f, 200f){

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


