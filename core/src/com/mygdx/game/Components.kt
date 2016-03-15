package com.mygdx.game


import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.*

import com.badlogic.gdx.math.Vector2

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture

import java.util.ArrayList

fun PlayerPhysicsComponent(world : World, entity : Entity, x : Float, y : Float) : PhysicsComponent{
    val PlayerWidth = 10f
    val PlayerHeight = 20f
    var bodyDef = BodyDef()
    bodyDef.type = BodyDef.BodyType.DynamicBody
    bodyDef.fixedRotation = true
    bodyDef.position.set(x, y)

    var body = world.createBody(bodyDef)
    body.setUserData(BodyUserData(BodyCategory.PLAYER, entity))

    var shape = PolygonShape()
    shape.setAsBox(PlayerWidth, PlayerHeight)
    
    var fixture = FixtureDef()
    fixture.shape = shape
    fixture.density = 1f
    fixture.friction = .5f
    //fixture.restitution = 0f
    
    body.createFixture(fixture)

    //sensor
    shape.setAsBox(PlayerWidth * .9f, 1f, Vector2(0f, -PlayerHeight*1.1f), 0f)
    fixture.shape = shape
    fixture.density = 0f
    fixture.isSensor = true
    var fix = body.createFixture(fixture)
    fix.setUserData(FeetSensorData())
    shape.dispose()

    return PhysicsComponent(body)
}

fun EnemyPhysicsComponent(world : World, entity : Entity, x : Float, y : Float) : PhysicsComponent{
    val PlayerWidth = 5f
    val PlayerHeight = 10f
    var bodyDef = BodyDef()
    bodyDef.type = BodyDef.BodyType.DynamicBody
    bodyDef.fixedRotation = true
    bodyDef.position.set(x, y)

    var body = world.createBody(bodyDef)
    body.setUserData(BodyUserData(BodyCategory.ENEMY, entity))
    var shape = PolygonShape()
    shape.setAsBox(PlayerWidth, PlayerHeight)
    
    var fixture = FixtureDef()
    fixture.shape = shape
    fixture.density = 1f
    fixture.friction = .5f
    //fixture.restitution = 0f
    
    body.createFixture(fixture)

    //sensor
    shape.setAsBox(PlayerWidth * .9f, 1f, Vector2(0f, -PlayerHeight*1.1f), 0f)
    fixture.shape = shape
    fixture.density = 0f
    fixture.isSensor = true
    var fix = body.createFixture(fixture)
    fix.setUserData(FeetSensorData())
    shape.dispose()

    return PhysicsComponent(body)
}

class PhysicsComponent(var body : Body)  : Component{        

}

class InputComponent : Component{
    var left = false
    var right = false
    var up = false
    var down = false
}

class StatComponent(var hp : Int = 10, var attack : Int = 1, var defense : Int= 1) : Component{

}

class GraphicComponent(val texture : Texture, val x : Float = 0f, val y : Float = 0f) : Component{

}

class PositionComponent(var x : Float = 0f, var y : Float= 0f) : Component{

}

open class SkillsComponent : Component{
    var skills : ArrayList<Skill> = ArrayList<Skill>()

    fun add(vararg additions : Skill){
        for (skill in additions){
            skills.add(skill)
        }
    }
    
    fun getAttack() : Skill? {
        for (skill in skills){
            if(skill.type == SkillType.ATTACK)
                return skill
        }

        return null
    }

    fun getRange() : Skill?{
        for (skill in skills){
            if (skill.type == SkillType.RANGE)
                return skill
        }

        return null
    }
    //holds skills
    //one skill is range, one is melee
    //rest is a list of skills
    //a skill is something that has an animation and does damage
    //should have a name
    
}

class PlayerSkillsComponent : SkillsComponent(){

}
