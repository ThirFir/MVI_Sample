package com.sample.mvisample

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax

abstract class BaseContainerHost<STATE : Any, SIDE_EFFECT : Any>(

): ContainerHost<STATE, SIDE_EFFECT>, ViewModel() {

    protected suspend fun<T> Result<T>.reduceResult(
        syntax: Syntax<STATE, SIDE_EFFECT>,
        onSuccess: (T) -> STATE,
        onFailure: (Throwable) -> STATE
    ) {
        with(syntax) {
            this@reduceResult.onSuccess {
                reduce { onSuccess(it) }
            }.onFailure {
                reduce { onFailure(it) }
            }
        }
    }

    protected suspend fun<T> Result<T>.reduceOnSuccess(
        syntax: Syntax<STATE, SIDE_EFFECT>,
        onSuccess: (T) -> STATE
    ) {
        with(syntax) {
            this@reduceOnSuccess.onSuccess {
                reduce { onSuccess(it) }
            }
        }
    }
}