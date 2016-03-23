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

open class BattleContainer(var x : Float, var y : Float){
    var hp = 5
}

class PlayerContainer(player : Entity) : BattleContainer(20f, 200f){
    init{
        hp = 2
    }
    var skills = player.getComponent(SkillsComponent::class.java)     
}

class EnemyContainer : BattleContainer(200f, 200f){
}

