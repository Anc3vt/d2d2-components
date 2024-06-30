package com.ancevt.d2d2.compas.component

interface ComponentFactory {

    fun createButton(): Button

    fun createCheckbox(): Checkbox

    fun createLabel(): Label

    fun createPanel(): Panel

    fun createTextField():TextField


}