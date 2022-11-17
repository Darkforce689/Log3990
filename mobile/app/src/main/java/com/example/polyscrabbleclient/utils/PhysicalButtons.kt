package com.example.polyscrabbleclient.utils

import java.util.*


object PhysicalButtons {
    private val backPressedStack = Stack<() -> Unit>()

    fun reset() {
        backPressedStack.clear()
    }

    fun getBackPress(): (() -> Unit)? {
        return if (backPressedStack.isEmpty()) return null else backPressedStack.pop()
    }

    fun pushBackPress(onBackPress: () -> Unit) {
        backPressedStack.push(onBackPress)
    }

    fun popBackPress() {
        if (backPressedStack.isEmpty()) {
            return
        }
        backPressedStack.pop()
    }
}
