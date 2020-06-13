package me.xx2bab.polyfill.matrix.annotation

enum class InitStage { PRE_BUILD, AFTER_EVALUATE }

annotation class ProviderConfig(val initStage: InitStage)