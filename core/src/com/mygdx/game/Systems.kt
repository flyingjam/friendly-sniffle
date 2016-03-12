package com.mygdx.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.utils.ImmutableArray

import com.badlogic.gdx.Input
import com.badlogic.gdx.Gdx

import com.badlogic.gdx.graphics.g2d.SpriteBatch

class InputSystem() : EntitySystem() {
    lateinit var entities : ImmutableArray<Entity> 
    var im : ComponentMapper<InputComponent> = ComponentMapper.getFor(InputComponent::class.java)
    override fun addedToEngine(engine : Engine){
        entities = engine.getEntitiesFor(Family.all(InputComponent::class.java).get())
    }

    override fun update(dt : Float){
        for (entity in entities){
            val input = im.get(entity)
            
            input.left = Gdx.input.isKeyPressed(Input.Keys.A)
            input.right = Gdx.input.isKeyPressed(Input.Keys.D)
            input.up = Gdx.input.isKeyPressed(Input.Keys.W)
        }
    }
}

class UpdatePosition : EntitySystem() {
    lateinit var entities : ImmutableArray<Entity>
    var positionm = ComponentMapper.getFor(PositionComponent::class.java)
    var physicsm = ComponentMapper.getFor(PhysicsComponent::class.java)
    override fun addedToEngine(engine : Engine){
        entities = engine.getEntitiesFor(Family.all(PositionComponent::class.java, PhysicsComponent::class.java).get())
    }
    override fun update(dt : Float){
        for (entity in entities){
            val position = positionm.get(entity)
            val physics = physicsm.get(entity)
            val bodyPos = physics.body.getPosition()
            position.x = bodyPos.x 
            position.y = bodyPos.y
        }
    }
}

class DrawSystem(var batch : SpriteBatch) : EntitySystem(){
    lateinit var entities : ImmutableArray<Entity>
    var gm = ComponentMapper.getFor(GraphicComponent::class.java)
    var pm = ComponentMapper.getFor(PositionComponent::class.java)
    override fun addedToEngine(engine : Engine){
        entities = engine.getEntitiesFor(Family.all(GraphicComponent::class.java, PositionComponent::class.java).get())
    }

    override fun update(dt : Float){
        for (entity in entities){
            val graphics = gm.get(entity)
            val position = pm.get(entity)
            batch.draw(graphics.texture, position.x + graphics.x, position.y + graphics.y)
        }
    }
    
}
class ApplyInput : EntitySystem() {

    lateinit var entities : ImmutableArray<Entity> 
    var im : ComponentMapper<InputComponent> = ComponentMapper.getFor(InputComponent::class.java)
    var pm : ComponentMapper<PhysicsComponent> = ComponentMapper.getFor(PhysicsComponent::class.java)

    override fun addedToEngine(engine : Engine){
        entities = engine.getEntitiesFor(Family.all(InputComponent::class.java, PhysicsComponent::class.java).get())
    }
    
    override fun update(dt : Float){
        for (entity in entities){
            val input = im.get(entity)
            val physics = pm.get(entity)
            
            var body = physics.body
            val pos = body.getPosition()
            val mass = body.getMass()
            val impulse = mass * 20f


            //get foot fixture data
            var contactCount = 0
            for (fixture in body.getFixtureList()){
                val userData = fixture.getUserData()
                if (userData != null){
                    val footData = userData as FeetSensorData
                    contactCount = footData.contacts
                }
            }
            if (input.left)
                body.applyLinearImpulse(-impulse, 0f, pos.x, pos.y, true)
            if (input.right) 
                body.applyLinearImpulse(impulse, 0f, pos.x, pos.y, true)
            if (input.up && contactCount > 0)
                body.applyLinearImpulse(0f, mass * 70, pos.x, pos.y, true)
        }
    }

}
