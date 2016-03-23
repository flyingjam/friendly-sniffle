package com.mygdx.game

import com.mygdx.game.battle.BattleScreen
import com.mygdx.game.battle.Skill
import com.mygdx.game.battle.BattleType
import com.mygdx.game.battle.BattleContainer
import com.mygdx.game.battle.BasicAttackSkill
import com.mygdx.game.battle.BasicRangeSkill

import com.badlogic.gdx.Input
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.Texture


val PlayerHeight = 10f
val PlayerWidth = 5f

enum class BodyCategory{
    PLAYER, ENEMY, GEOMETRY
}
class BodyUserData(val category : BodyCategory, val entity : Entity = Entity()){ 

}
class FeetSensorData(var contacts : Int = 0){
    val name = "FeetSensor"
}

class PlayerBattleContact(var game : Cac) : ContactListener{

    override fun beginContact(contact : Contact){
        val bodyA = contact.getFixtureA().getBody().getUserData() as BodyUserData
        val bodyB = contact.getFixtureB().getBody().getUserData() as BodyUserData
        if (bodyA.category == BodyCategory.PLAYER && bodyB.category == BodyCategory.ENEMY){
            game.setScreen(BattleScreen(game, BattleType.NORMAL, bodyA.entity))
        }
        if (bodyA.category == BodyCategory.ENEMY && bodyB.category == BodyCategory.PLAYER){ 
            game.setScreen(BattleScreen(game, BattleType.NORMAL, bodyA.entity))
        }
    }

    override fun endContact(contact : Contact){

    }

    override fun preSolve(contact : Contact, manifold : Manifold){}
    override fun postSolve(contact : Contact, impulse : ContactImpulse){}
}

class FeetContact : ContactListener{

    override fun endContact(contact : Contact){
        var userDataA = contact.getFixtureA().getUserData()
        if (userDataA != null){
            var userData = userDataA as FeetSensorData
            userData.contacts -= 1
        }
        
        var userDataB = contact.getFixtureB().getUserData()
        if (userDataB != null){
            var userData = userDataB as FeetSensorData
            userData.contacts -= 1
        }
    }
    override fun beginContact(contact : Contact){
        var userDataA = contact.getFixtureA().getUserData()
        if (userDataA != null){
            var userData = userDataA as FeetSensorData
            userData.contacts += 1
        }
        
        var userDataB = contact.getFixtureB().getUserData()
        if (userDataB != null){
            var userData = userDataB as FeetSensorData
            userData.contacts += 1
        }

    }
    override fun preSolve(contact : Contact, manifold : Manifold){
    }
    override fun postSolve(contact : Contact, impulse : ContactImpulse){
    }

}

fun createPlayer(world : World, engine : Engine, x : Float, y : Float) : Entity{
    var player = Entity()
    val physics = PlayerPhysicsComponent(world, player, 50f, 50f)
    engine.addEntity(player)
    player.add(InputComponent())
    player.add(StatComponent())
    player.add(PositionComponent())
    player.add(GraphicComponent(Texture("pmaro.png"), -15f, -20f))
    player.add(physics)
    var regions = TextureAtlas("sprites.atlas")
    val animation = Animation(0.5f, regions.getRegions())

    fun effect(user : BattleContainer, targets : Array<out BattleContainer>){
        val enemy = targets.get(0)
        enemy.hp -= 1
    }

    val skill = SkillsComponent()
    skill.add(BasicAttackSkill(animation, ::effect))
    skill.add(BasicRangeSkill(animation, ::effect))
    player.add(skill)
    return player
}

fun createEnemy(world : World, engine : Engine, x : Float, y : Float) : Entity{
    var enemy = Entity()
    val physics = EnemyPhysicsComponent(world, enemy, x, y)
    engine.addEntity(enemy)
    enemy.add(StatComponent())
    enemy.add(physics)
    return enemy
}

class GameScreen(val game : Cac) : Screen{

    var shape = ShapeRenderer()
    var x : Float = 0f;
    var world = World(Vector2(0f, -980f), true)
    var camera : OrthographicCamera = OrthographicCamera()
    var DebugRenderer = Box2DDebugRenderer()
    var listener = FeetContact()
    lateinit var ground : Body

    val engine = Engine()
    var player = createPlayer(world, engine, 40f, 40f)
    var enemy = createEnemy(world, engine, 100f, 100f)
    
    var batch = SpriteBatch()
    var font = BitmapFont()
    init{
        camera.setToOrtho(false, 800f, 400f)  
        var groundBD = BodyDef()
        groundBD.position.set(Vector2(0f, 10f))
        ground = world.createBody(groundBD)
        ground.setUserData(BodyUserData(BodyCategory.GEOMETRY))
        
        var groundBox = PolygonShape()
        groundBox.setAsBox(400f, 10f)
        ground.createFixture(groundBox, 10f)
        groundBox.dispose()

        world.setContactListener(listener)
        world.setContactListener(PlayerBattleContact(game))
        engine.addSystem(InputSystem())
        engine.addSystem(ApplyInput())
        engine.addSystem(DrawSystem(batch))
        engine.addSystem(UpdatePosition())
    }

    override fun render(dt : Float){
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        val hp = player.getComponent(StatComponent::class.java) 
        batch.begin()
        font.draw(batch, "PlayerHP : " + hp.hp, 100f, 100f) 
        engine.update(dt)
        batch.end()

        camera.update()
        DebugRenderer.render(world, camera.combined)
        world.step(1/60f, 6, 2)
        
    }
   
    override fun dispose(){
        shape.dispose()
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
