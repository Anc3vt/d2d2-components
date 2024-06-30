package com.ancevt.d2d2.compas.component

interface Panel : Component {

    fun addComponent(component:Component)

    fun removeComponent(component:Component)
}